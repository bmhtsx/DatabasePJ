package cn.edu.fudan;

import cn.edu.fudan.entity.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class Database {

    static Connection conn;
    static PreparedStatement ps;

    public void init() throws Exception {
        Properties properties = new Properties();
        ClassLoader classLoader = Database.class.getClassLoader();

        // 读取JDBC配置文件，进行连接
        InputStream is = classLoader.getResourceAsStream("jdbc.properties");
        properties.load(is);
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String url = properties.getProperty("url");
        String driverClass = properties.getProperty("driverClass");

        Class.forName(driverClass);
        conn = DriverManager.getConnection(url, user, password);
    }

    public void insert(Commit commit) {

    }

    public void insert(Instance instance) {

    }

    public void insert(InstCase instCase) {

    }

    public void insert(Location location) {

    }

    public void insert(Match match) {

    }
}
