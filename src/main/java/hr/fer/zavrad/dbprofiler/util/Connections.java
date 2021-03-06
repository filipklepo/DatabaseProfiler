package hr.fer.zavrad.dbprofiler.util;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import hr.fer.zavrad.dbprofiler.model.*;
import hr.fer.zavrad.dbprofiler.model.rule.NumericRangeRule;
import hr.fer.zavrad.dbprofiler.model.rule.RegularExpressionRule;
import hr.fer.zavrad.dbprofiler.model.rule.Rules;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.TreeItem;
import sun.reflect.generics.tree.Tree;

import javax.swing.*;
import java.sql.*;
import java.util.*;

/**
 * Utility class intended for database profiling with JDBC driver in JavaFX environment.
 * Enables straightforward retrieval of metadata and content of databases, and generating column statistics.
 *
 * @author Filip
 */
public final class Connections {

    private static final Map<Integer, TableColumnType> COLUMN_TYPE;
    private static final List<String> IGNORABLE_SCHEMAS;

    static {
        COLUMN_TYPE = new HashMap<>();

        COLUMN_TYPE.put(-9, TableColumnType.NVARCHAR);
        COLUMN_TYPE.put(-7, TableColumnType.BIT);
        COLUMN_TYPE.put(-6, TableColumnType.TINYINT);
        COLUMN_TYPE.put(-5, TableColumnType.BIGINT);
        COLUMN_TYPE.put(-4, TableColumnType.LONGVARBINARY);
        COLUMN_TYPE.put(-3, TableColumnType.VARBINARY);
        COLUMN_TYPE.put(-2, TableColumnType.BINARY);
        COLUMN_TYPE.put(-1, TableColumnType.LONGVARCHAR);
        COLUMN_TYPE.put(0, TableColumnType.NULL);
        COLUMN_TYPE.put(1, TableColumnType.CHAR);
        COLUMN_TYPE.put(2, TableColumnType.NUMERIC);
        COLUMN_TYPE.put(3, TableColumnType.DECIMAL);
        COLUMN_TYPE.put(4, TableColumnType.INTEGER);
        COLUMN_TYPE.put(5, TableColumnType.SMALLINT);
        COLUMN_TYPE.put(6, TableColumnType.FLOAT);
        COLUMN_TYPE.put(7, TableColumnType.REAL);
        COLUMN_TYPE.put(8, TableColumnType.DOUBLE);
        COLUMN_TYPE.put(12, TableColumnType.VARCHAR);
        COLUMN_TYPE.put(91, TableColumnType.DATE);
        COLUMN_TYPE.put(92, TableColumnType.TIME);
        COLUMN_TYPE.put(93, TableColumnType.TIMESTAMP);
        COLUMN_TYPE.put(1111, TableColumnType.OTHER);
        COLUMN_TYPE.put(2003, TableColumnType.TEXT);

        IGNORABLE_SCHEMAS = new ArrayList<>();
        IGNORABLE_SCHEMAS.add("sys");
        IGNORABLE_SCHEMAS.add("information_schema");
        IGNORABLE_SCHEMAS.add("pg_catalog");
    }

    public static boolean isNumericColumn(TableColumnType tableColumnType) {
        return tableColumnType == TableColumnType.TINYINT   ||
                tableColumnType == TableColumnType.BIGINT   ||
                tableColumnType == TableColumnType.NUMERIC  ||
                tableColumnType == TableColumnType.DECIMAL  ||
                tableColumnType == TableColumnType.INTEGER  ||
                tableColumnType == TableColumnType.SMALLINT ||
                tableColumnType == TableColumnType.FLOAT    ||
                tableColumnType == TableColumnType.REAL     ||
                tableColumnType == TableColumnType.DOUBLE;
    }

    public static boolean isTextualColumn(TableColumnType tableColumnType) {
        return tableColumnType == TableColumnType.LONGVARCHAR ||
                tableColumnType == TableColumnType.CHAR       ||
                tableColumnType == TableColumnType.VARCHAR    ||
                tableColumnType == TableColumnType.NVARCHAR   ||
                tableColumnType == TableColumnType.TEXT;
    }

    private Connections() {
    }

    public static Optional<TableColumnType> getColumnType(int columnType) {
        return COLUMN_TYPE.containsKey(columnType) ? Optional.of(COLUMN_TYPE.get(columnType)) : Optional.empty();
    }

    public static List<String> getTableNames(Connection connection) {
        return getTableNames(connection, null);
    }

    public static ObservableList<String> getTableNames(Connection connection, String schema) {
        ObservableList<String> tableNames = FXCollections.observableArrayList();

        try {
            ResultSet rs = connection.getMetaData().getTables(
                    null, schema, null, new String[]{"TABLE"});

            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return tableNames;
    }

    private static Map<String, List<String>> getForeignKeysByTables(Connection connection) {
        Map<String, List<String>> foreignKeysByTables = new HashMap<>();

        try {
            DatabaseMetaData metaData = connection.getMetaData();

            for(String tableName : getTableNames(connection)) {
                foreignKeysByTables.put(tableName, null);

                ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);
                while (foreignKeys.next()) {
                    String foreignKeyTableName = foreignKeys.getString("PKTABLE_NAME");

                    if(Objects.isNull(foreignKeysByTables.get(tableName))) {
                        foreignKeysByTables.put(tableName, new ArrayList<>());
                    }
                    foreignKeysByTables.get(tableName).add(foreignKeyTableName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foreignKeysByTables;
    }

    public static void createDatabaseRelationshipDiagram(List<Table> tables, SwingNode swingNode,
                                                         Connection connection){

        Map<String, List<String>> foreignKeysByTables = getForeignKeysByTables(connection);

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        graph.setCellsEditable(false);

        Map<String, Object> edgeStyle = new HashMap<String, Object>();
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        edgeStyle.put(mxConstants.STYLE_ORTHOGONAL, true);
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION);
        edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edgeStyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        edgeStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#446299");
        Map<String, Object> vertexStyle = new HashMap<String, Object>();
        vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        vertexStyle.put(mxConstants.STYLE_EDITABLE, mxConstants.NONE);

        graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);

        Map<String, Object> vertexMap = new HashMap<>();
        try {
            int i = 0;
            for(Table table : tables){
                String[] lines = table.toString().split(System.lineSeparator());
                int longestLineLength = Arrays.stream(lines).mapToInt(String::length).max().getAsInt();

                Object vertex = null;
                if(lines.length == 1) {
                    vertex = graph.insertVertex(
                            parent, null, table, i, i, longestLineLength * 10,
                            25,"swimlane;rounded=1;");
                } else {
                    vertex = graph.insertVertex(
                            parent, null, table, i, i, longestLineLength * 7,
                            lines.length * 16,"swimlane;rounded=1;");
                }

                vertexMap.put(table.getName(), vertex);
            }

            for(Table table : tables){
                if(Objects.nonNull(foreignKeysByTables.get(table.getName()))){

                    for(String referencedTable : foreignKeysByTables.get(table.getName())){
                        if(!tables.stream().filter(t -> t.getName().equals(referencedTable)).findAny().isPresent()) {
                            continue;
                        }

                        graph.insertEdge(
                                parent, null, "", vertexMap.get(table.getName()), vertexMap.get(referencedTable));
                    }
                }
            }
        }
        finally {
            graph.getModel().endUpdate();
        }

        graph.setCellsEditable(false);
        graph.setCellsMovable(false);
        graph.setEdgeLabelsMovable(false);
        graph.setCellsMovable(false);

        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());

        mxGraphComponent graphComponent = new mxGraphComponent(graph);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(graphComponent);
            }
        });
    }

    public static Optional<TreeItem> getDatabaseSchema(Connection connection) {
        try {
            TreeItem<ProfilerObject> rootItem = new TreeItem(
                    new Database(connection.getMetaData().getDatabaseProductName()));
            rootItem.setExpanded(true);

            TreeItem<ProfilerObject> rules = new TreeItem<>(new Rules());

            rules.getChildren().addAll(
                    new TreeItem<ProfilerObject>(new RegularExpressionRule()),
                    new TreeItem<ProfilerObject>(new NumericRangeRule()));

            rootItem.getChildren().add(rules);

            List<String> schemaNames = getSchemaNames(connection);
            for(String schemaName : schemaNames) {

                TreeItem<ProfilerObject> schema = new TreeItem<>(new Schema(schemaName));

                System.out.println(schemaName);
                for (String tableName : getTableNames(connection, schemaName)) {
                    System.out.println("\t" + tableName);
                    Table table = new Table(connection, tableName, true, schemaName);
                    table.setShowRowsCount(true);
                    TreeItem<ProfilerObject> tableItem = new TreeItem(table);
                    if(table.getRowCount() == 0) {
                        continue;
                    }
                    if(schemaNames.size() > 1) {
                        schema.getChildren().add(tableItem);
                    } else {
                        rootItem.getChildren().add(tableItem);
                    }

                    ResultSet resultSet = connection.createStatement()
                            .executeQuery(String.format("SELECT * FROM %s.%s WHERE 1 = 2", schemaName, table.getName()));
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

                    for(int i = 1, length = resultSetMetaData.getColumnCount(); i <= length; ++i) {

                        TreeItem<ProfilerObject> columnItem =
                                new TreeItem<>(new TableColumn(table,
                                        resultSetMetaData.getColumnName(i),
                                        resultSetMetaData.getColumnType(i),
                                        connection,
                                        true));
                        tableItem.getChildren().add(columnItem);
                    }
                }

                if(!schema.getChildren().isEmpty()) {
                    rootItem.getChildren().add(schema);
                }
            }

            return Optional.of(rootItem);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<TableColumnType> getColumnType(String schema, String column, String table, Connection connection) {
        String query = String.format("SELECT %s FROM %s%s WHERE 1 = 0", column, !schema.isEmpty() ? schema  + "." : "", table);

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            return getColumnType(resultSet.getMetaData().getColumnType(1));
        } catch (SQLException e) {}

        return Optional.empty();
    }

    public static List<String> getSchemaNames(Connection connection) {
        List<String> displayableSchemas = new ArrayList<>();

        try {
            List<String> allSchemas = new ArrayList<>();
            ResultSet schemas = connection.getMetaData().getSchemas();

            while(schemas.next()) {
                allSchemas.add(schemas.getString(1));
            }

            for(String schema : allSchemas) {
                if(!getTableNames(connection, schema).isEmpty() && !IGNORABLE_SCHEMAS.contains(schema.toLowerCase())) {
                    displayableSchemas.add(schema);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return displayableSchemas;
    }

    public static List<String> getPrimaryKeys(Connection connection, Table table){
        List<String> primaryKeys = new ArrayList<>();

        try {
            ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, table.getSchemaName(), table.getName());

            while (resultSet.next()) {
                primaryKeys.add(resultSet.getString("COLUMN_NAME"));
            }
        } catch (SQLException e) {
        }

        return primaryKeys;
    }
}
