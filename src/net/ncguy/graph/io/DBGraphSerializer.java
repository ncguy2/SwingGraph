package net.ncguy.graph.io;

import net.ncguy.graph.data.icons.Icons;
import net.ncguy.graph.event.ToastEvent;
import net.ncguy.graph.scene.logic.Node;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;
import org.h2.Driver;

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
        File mv = new File(file.getAbsolutePath() + ".mv.db");
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
        statement.close();
    }

    private static void truncate(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("TRUNCATE WIRES;");
        statement.executeUpdate("TRUNCATE PINS;");
        statement.executeUpdate("TRUNCATE NODES;");
        statement.close();
    }

    private static void writeNode(Connection connection, Node node) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(PREPARED_NODE_ADD);
        preparedStatement.setString(1, node.uuid);
        preparedStatement.setString(2, node.title);
        preparedStatement.setFloat(3, node.location.x);
        //noinspection SuspiciousNameCombination
        preparedStatement.setFloat(4, node.location.y);
        preparedStatement.executeUpdate();
        preparedStatement.close();
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

    public static SceneGraph load(File file) {
        return null;
    }

    private static Connection getConnection(File file) {
        try {
            Class.forName(Driver.class.getCanonicalName());
            String str = "jdbc:h2:"+file.getAbsolutePath();
            System.out.println(str);
//            str += ";MV_STORE=FALSE'MVCC=FALSE";
            str += ";TRACE_LEVEL_FILE=0";
            Connection conn = DriverManager.getConnection(str, "sa", "");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String CREATE_NODE_TABLE = "CREATE TABLE NODES (\n" +
            "    ID INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    UUID TEXT,\n" +
            "    TITLE TEXT,\n" +
            "    LOCATIONX FLOAT,\n" +
            "    LOCATIONY FLOAT\n" +
            ");";

    public static final String CREATE_PIN_TABLE = "CREATE TABLE PUBLIC.PINS\n" +
            "(\n" +
            "    ID INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    NODEID INT,\n" +
            "    ONLEFT BOOL,\n" +
            "    CONNECTED BOOL,\n" +
            "    LABEL TEXT,\n" +
            "    INDEX INT\n"+
            ");\n";

    public static final String CREATE_WIRE_TABLE = "CREATE TABLE WIRES (\n" +
            "    ID INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    PINLEFT INTEGER,\n" +
            "    PINRIGHT INTEGER\n" +
            ");";

    public static final String CREATE_LIBS_TABLE = "CREATE TABLE LIBS\n" +
            "(\n" +
            "    ID INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    LIBCLASS TEXT\n" +
            ");";

    public static final String CREATE_META_TABLE = "CREATE TABLE META\n" +
            "(\n" +
            "    ID INT AUTO_INCREMENT PRIMARY KEY,\n" +
            "    KEY TEXT,\n" +
            "    VALUE TEXT\n" +
            ");";

    public static final String PREPARED_NODE_ADD = "INSERT INTO NODES (UUID, TITLE, LOCATIONX, LOCATIONY) VALUES (?, ?, ?, ?);";
    public static final String PREPARED_PIN_ADD = "INSERT INTO PINS (NODEID, ONLEFT, CONNECTED, LABEL, INDEX) VALUES (?, ?, ?, ?, ?);";
    public static final String PREPARED_WIRE_ADD = "INSERT INTO WIRES (PINLEFT, PINRIGHT) VALUES (?, ?);";

}
