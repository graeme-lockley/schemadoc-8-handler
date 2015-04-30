package za.co.no9.jdbcdry.tools.schemadoc;

import freemarker.template.TemplateException;
import org.apache.maven.plugin.logging.Log;
import za.co.no9.jdbcdry.adaptor.DBDriver;
import za.co.no9.jdbcdry.model.DatabaseMetaData;
import za.co.no9.jdbcdry.model.GenerationException;
import za.co.no9.jdbcdry.model.ToolHandler;
import za.co.no9.jdbcdry.port.jsqldslmojo.ConfigurationException;
import za.co.no9.jdbcdry.port.jsqldslmojo.TableFilter;
import za.co.no9.jdbcdry.port.jsqldslmojo.Target;
import za.co.no9.util.FreeMarkerUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Handler implements ToolHandler {
    private Log log;
    private HandlerTarget target;

    @Override
    public void setup(Log log, Target target) {
        this.log = log;
        this.target = HandlerTarget.from(target);
    }

    @Override
    public void process(Connection connection, TableFilter tableFilter) throws SQLException, ConfigurationException, GenerationException {
        DBDriver dbDriver = target.getDBDriver(connection);
        DatabaseMetaData databaseMetaData = dbDriver.databaseMetaData();

        target.generatorTargetRoot().mkdirs();
        File outputFile = target.getTemplateOutputFile();

        try (PrintStream fos = new PrintStream(new FileOutputStream(outputFile))) {
            fos.println(FreeMarkerUtils.template(
                    assembleMap(
                            from("target", target),
                            from("databaseMetaData", databaseMetaData),
                            from("tableFilter", tableFilter)), target.template().orElse("schemadoc/template.ftl")));
        } catch (TemplateException | FileNotFoundException ex) {
            throw new GenerationException(ex);
        }

        try {
            String[] args = {"/bin/sh", "-c", target.getPostCommand()};
            log.info("SchemaDoc: " + args[0] + " " + args[1] + " '" + args[2] + "'");
            Runtime.getRuntime().exec(args);
        } catch (IOException ex) {
            throw new GenerationException(ex);
        }
    }

    private Map<String, Object> assembleMap(Map.Entry<String, Object>... entries) {
        Map<String, Object> result = new HashMap<>();
        Stream.of(entries).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    private static MapEntry from(String key, Object value) {
        return new MapEntry(key, value);
    }


    static class MapEntry implements Map.Entry<String, Object> {
        private String key;
        private Object value;

        MapEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public Object setValue(Object value) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
