package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.Instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstanceDAO {
    Connection conn = null;
    PreparedStatement ps = null;

    public int insert(Instance instance) {
        int res = 0;
        try {
            conn = DBConnection.getConn();

            String sql = "insert into instance (commit_id, severity, type, status, author, message, creation_date, update_date) values(?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setInt(1, instance.getCommitId());
            ps.setString(2, instance.getSeverity());
            ps.setString(3, instance.getType());
            ps.setString(4, instance.getStatus());
            ps.setString(5, instance.getAuthor());
            ps.setString(6, instance.getMessage());
            ps.setTimestamp(7, instance.getCreationDate());
            ps.setTimestamp(8, instance.getUpdateDate());

            ps.executeUpdate();

            sql = "select LAST_INSERT_ID();";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            rs.next();
            res = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return res;
    }
}
