# schemadoc-8-handler

A handler for the jdbc-8-maven-plugin that produces an image of an underlying database schema directly off of a database.  To see an example of this handler in action take a look at [schemadoc-8-example](https://github.com/graeme-lockley/schemadoc-8-example) which generates a diagram off of an embedded [H2](http://www.h2database.com) database.


# How it works

Looking at the code you'll notice that the code base is tiny as it makes etensive use of a number of components:

- The project [jdbc-8-maven-plugin](https://github.com/graeme-lockley/jdbc-8-maven-plugin) connects to the underlying database and extracts all of the tables, fields and association into a set of value objects.  The class [Handler](https://github.com/graeme-lockley/schemadoc-8-handler/blob/master/src/main/java/za/co/no9/jdbcdry/tools/schemadoc/Handler.java) is the entry point into this project.
- The template engine [FreeMarker](http://freemarker.org) is used to create a dot file from [jdbc-8-maven-plugin](https://github.com/graeme-lockley/jdbc-8-maven-plugin)'s value objects using the default template [template.tfl](src/main/resources/schemadoc/template.ftl) to produce the dot file.
- The tool [Graphviz](http://www.graphviz.org) which is run against the generated dot file to produce an image.
- I used the colouring from [SchemaSpy](http://schemaspy.sourceforge.net) to help me get the basics of a dot file in place.


# Template data model

The template is invoked with a data model which can be interrogated to produce output.  The following elements are present within the data model.

Name             | Purpose | Definition
-----------------|---------|------------
target           | Access to the handler's companion target which provides access to the handler's properties. | [HandlerTarget](https://github.com/graeme-lockley/schemadoc-8-handler/blob/master/src/main/java/za/co/no9/jdbcdry/tools/schemadoc/HandlerTarget.java)
databaseMetaData | The value objects that constitute the database schema. | [DatabaseMetaData](https://github.com/graeme-lockley/jdbc-8-maven-plugin/blob/master/src/main/java/za/co/no9/jdbcdry/tools/DatabaseMetaData.java)
tableFilter      | The filters that are in the jsqldsl.xml file. | [TableFilter](https://github.com/graeme-lockley/jdbc-8-maven-plugin/blob/master/src/main/java/za/co/no9/jdbcdry/port/jsqldslmojo/TableFilter.java)


# Handler properties

A number of properties can be set in the jsqldsl.xml file when using this handler:

Name | Purpose | Default
-----|---------|--------
template | A reference to a resource contained within the project. | schemadoc/template.ftl
template-output | The name of the file that the output from applying the template is to be placed. | target/schemadoc/output.dot
post-command | The command with arguments that is to executed after the output file has been generated. | dot -T png -o target/schemadoc/output.png target/schemadoc/output.dot
