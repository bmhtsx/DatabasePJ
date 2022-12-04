package cn.edu.fudan;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    static Connection conn;
    static PreparedStatement ps;

    public static Connection getConn() {
        Properties properties = new Properties();
        ClassLoader classLoader = DBConnection.class.getClassLoader();
        Connection conn =null;

        // 读取JDBC配置文件，进行连接
        try {
            InputStream is = classLoader.getResourceAsStream("jdbc.properties");
            properties.load(is);
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            String url = properties.getProperty("url");
            String driverClass = properties.getProperty("driverClass");

            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
        } return conn;

    }

    public static void close(Connection conn, PreparedStatement ps) {
        try {
            conn.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
