package net.ncguy.graph.io;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.data.icons.Icons;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Guy on 20/01/2017.
 */
public class DBGraphSerializer {

    public static void save(File file, SceneGraph graph) throws SQLException {
        File mv = new File(file.getAbsolutePath());
        try {
            Files.deleteIfExists(mv.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Connection connection = getConnection(file);
        try {

            if (connection == null) {
                new ToastEvent("Failed to connect to file").setImagePath(Icons.Icon.WARNING_WHITE).fire();
                return;
            }
            connection.setAutoCommit(true);

                createStructure(connection);

            Set<Node> graphNodes = graph.nodes;
            List<Node> sortedNodes = graphNodes.stream().sorted(Comparator.comparing(n -> n.title)).collect(Collectors.toList());

            sortedNodes.forEach(n -> {
                try {
                    writeNode(connection, n);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            List<Pin> pins = new ArrayList<>();
            graphNodes.forEach(node -> pins.addAll(node.getPinList()));
            pins.sort(Comparator.comparing(p -> p.label));
            pins.forEach(p -> {
                try {
                    writePin(connection, p, sortedNodes.indexOf(p.owningNode) + 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            pins.stream()
                    .filter(Pin::isConnected)
                    .forEach(pin -> {
                        Pin other = pin.connected;
                        try {
                            writeWire(connection, pins.indexOf(pin) + 1, pins.indexOf(other) + 1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

            connection.close();
        }catch (SQLException e) {
            e.printStackTrace();
            if(connection != null)
                connection.close();
        }
    }

    private static void createStructure(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(CREATE_NODE_TABLE);
        statement.executeUpdate(CREATE_PIN_TABLE);
        statement.executeUpdate(CREATE_WIRE_TABLE);
        statement.executeUpdate(CREATE_LIBS_TABLE);
        statement.executeUpdate(CREATE_META_TABLE);
        statement.executeUpdate(CREATE_PROPERTY_TABLE);
        statement.close();
    }

    private static void truncate(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("TRUNCATE WIRES;");
        statement.executeUpdate("TRUNCATE PINS;");
        statement.executeUpdate("TRUNCATE NODES;");
        statement.executeUpdate("TRUNCATE LIBS;");
        statement.executeUpdate("TRUNCATE META;");
        statement.executeUpdate("TRUNCATE NODE_PROPERTIES;");
        statement.close();
    }

    private static void writeNode(Connection connection, Node node) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(PREPARED_NODE_ADD, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, node.uuid);
        preparedStatement.setString(2, node.getClass().getCanonicalName());
        preparedStatement.setString(3, node.title);
        preparedStatement.setFloat(4, node.location.x);
        preparedStatement.setFloat(5, node.location.y);
        preparedStatement.executeUpdate();

        ResultSet rs = preparedStatement.getGeneratedKeys();
        int id = -1;
        if(rs.next())
            id = rs.getInt(1);
        rs.close();

        preparedStatement.close();

        if(id >= 0)
            writeNodeProperties(connection, node, id);
    }

    private static void writePin(Connection connection, Pin pin, int nodeIndex) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(PREPARED_PIN_ADD);
        preparedStatement.setInt(1, nodeIndex);
        preparedStatement.setBoolean(2, pin.onLeft);
        preparedStatement.setBoolean(3, pin.isConnected());
        preparedStatement.setString(4, pin.label);
        preparedStatement.setInt(5, pin.index);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private static void writeWire(Connection connection, int leftIndex, int rightIndex) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(PREPARED_WIRE_ADD);
        preparedStatement.setInt(1, leftIndex);
        preparedStatement.setInt(2, rightIndex);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    private static void writeNodeProperties(Connection connection, Node node, int nodeId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(PREPARED_PROPERTY_ADD);

        preparedStatement.setInt(1, nodeId);

        List<MutableProperty> properties = node.GetMutableProperties();

//        Gson gson = new Gson();

//        for (MutableProperty property : properties) {
//            preparedStatement.setString(2, property.getName());
//            preparedStatement.setString(3, gson.toJson(property.get()));
//            preparedStatement.executeUpdate();
//        }

        preparedStatement.close();
    }

    public static SceneGraph load(File file) {
        /* TODO Load graph from database file
         * Load nodes into map
         * Load wires into list
         * Connect wires to nodes using pin associations
         * Write properties to the nodes
         * Load correct libraries for graph
         */
        return null;
    }

    private static Connection getConnection(File file) {
        try {
//            Class.forName(Driver.class.getCanonicalName());
            String str = "jdbc:sqlite:"+file.getAbsolutePath();
            System.out.println(str);
//            str += ";MV_STORE=FALSE'MVCC=FALSE";
            Connection conn = DriverManager.getConnection(str, "sa", "");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String CREATE_NODE_TABLE = "CREATE TABLE NODES (\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    CLASSNAME TEXT,\n" +
            "    UUID TEXT,\n" +
            "    TITLE TEXT,\n" +
            "    LOCATIONX FLOAT,\n" +
            "    LOCATIONY FLOAT\n" +
            ");";

    public static final String CREATE_PIN_TABLE = "CREATE TABLE PINS\n" +
            "(\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    NODEID INT,\n" +
            "    ONLEFT BOOL,\n" +
            "    CONNECTED BOOL,\n" +
            "    LABEL TEXT,\n" +
            "    'INDEX' INT\n"+
            ");\n";

    public static final String CREATE_WIRE_TABLE = "CREATE TABLE WIRES (\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    PINLEFT INTEGER,\n" +
            "    PINRIGHT INTEGER\n" +
            ");";

    public static final String CREATE_LIBS_TABLE = "CREATE TABLE LIBS\n" +
            "(\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    LIBCLASS TEXT\n" +
            ");";

    public static final String CREATE_META_TABLE = "CREATE TABLE META\n" +
            "(\n" +
            "    ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "    KEY TEXT,\n" +
            "    VALUE TEXT\n" +
            ");";

    public static final String CREATE_PROPERTY_TABLE = "CREATE TABLE NODE_PROPERTIES\n" +
            "(\n"+
            "   ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "   NODEID INT,\n" +
            "   KEY TEXT,\n" +
            "   VALUE TEXT,\n" +
            ");";

    public static final String PREPARED_NODE_ADD = "INSERT INTO NODES (UUID, TITLE, LOCATIONX, LOCATIONY) VALUES (?, ?, ?, ?);";
    public static final String PREPARED_PIN_ADD = "INSERT INTO PINS (NODEID, ONLEFT, CONNECTED, LABEL, 'INDEX') VALUES (?, ?, ?, ?, ?);";
    public static final String PREPARED_WIRE_ADD = "INSERT INTO WIRES (PINLEFT, PINRIGHT) VALUES (?, ?);";
    public static final String PREPARED_PROPERTY_ADD = "INSERT INTO NODE_PROPERTIES (NODEID, KEY, VALUE) VALUES (?, ?, ?);";

}
