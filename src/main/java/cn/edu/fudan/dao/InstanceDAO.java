package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.Instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstanceDAO {
    Connection conn = null;
    PreparedStatement ps = null;

    public int insert(Instance instance) {
        int res = 0;
        try {
            conn = DBConnection.getConn();

            String sql = "insert into instance (commit_id, severity, type, status, author, message) values(?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setInt(1, instance.getCommitId());
            ps.setString(2, instance.getSeverity());
            ps.setString(3, instance.getType());
            ps.setString(4, instance.getStatus());
            ps.setString(5, instance.getAuthor());
            ps.setString(6, instance.getMessage());
//            ps.setTimestamp(7, instance.getCreationDate());
//            ps.setTimestamp(8, instance.getUpdateDate());

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

    public Instance addToInstance(ResultSet rs) throws SQLException {
        Instance instance = new Instance();
        instance.setId(rs.getInt("id"));
        instance.setCommitId(rs.getInt("commit_id"));
        instance.setSeverity(rs.getString("severity"));
        instance.setType(rs.getString("type"));
        instance.setStatus(rs.getString("status"));
        instance.setAuthor(rs.getString("author"));
        instance.setMessage(rs.getString("message"));
        return instance;
    }

    public List<Instance> getTraceInstance(int instanceId) {
        List<Instance> list = new ArrayList<>();
        Instance instance;
        int id = instanceId;
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instance where id=(select parent_id from `match` where child_id=?)";
            do {
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) break;
                instance = addToInstance(rs);
                list.add(instance);
                id = instance.getId();
            } while (true);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;

    }
}
