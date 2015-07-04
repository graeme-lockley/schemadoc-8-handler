digraph G {
    node [
        fontname="Helvetica"
        fontsize="11"
        shape="plaintext"
    ];
    edge [
        arrowsize="0.8"
        fontname="Helvetica"
        fontsize="8"
    ];
<#list tableFilter.filter(databaseMetaData.allTables()).toArray() as tableMetaData>
${tableMetaData.tableName().dbName()} [label= <<TABLE BORDER="2" CELLBORDER="1" CELLSPACING="0" BGCOLOR="#ffffff">
    <TR>
        <TD COLSPAN="3" BGCOLOR="#9bab96" ALIGN="CENTER">${tableMetaData.tableName().dbName()}</TD>
    </TR>
    <#list tableMetaData.fields().toArray() as field>
        <TR>
            <TD COLSPAN="2"
                <#if field.isPrimaryKey()>
                BGCOLOR="#bed1b8"
                </#if>
                ALIGN="LEFT"><#if field.isNullable()>
<FONT COLOR="#696969">${field.name()}</FONT>
            <#else>
${field.name()}
            </#if>
            </TD>
            <TD <#if field.isPrimaryKey()>
                    BGCOLOR="#bed1b8"
            </#if>
                    >${field.fieldType()}<#if field.columnSize().isPresent()>(${field.columnSize().get()}<#if field.subWidth().isPresent() && field.subWidth().get() != 0>, ${field.subWidth().get()}</#if>)
            </#if>
            </TD>
        </TR>
    </#list>
</TABLE>
>];
    <#list tableMetaData.foreignKeys().toArray() as foreignKey>
        <#if foreignKey.fkColumns().toArray()[0].isNullable()>
        "${foreignKey.fkTableName().dbName()}" -> "${foreignKey.pkTableName().dbName()}" [dir=back arrowhead=none arrowtail=nonecrowodot headlabel="${foreignKey.pkColumnNames(", ")}" taillabel="${foreignKey.fkColumnNames(", ")}"];
        <#else>
        "${foreignKey.fkTableName().dbName()}" -> "${foreignKey.pkTableName().dbName()}" [dir=both arrowhead=nonetee arrowtail=nonecrowodot headlabel="${foreignKey.pkColumnNames(", ")}" taillabel="${foreignKey.fkColumnNames(", ")}"];
        </#if>
    </#list>
</#list>
}

