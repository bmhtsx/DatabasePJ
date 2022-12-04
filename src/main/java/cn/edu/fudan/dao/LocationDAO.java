package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LocationDAO {

    Connection conn = null;
    PreparedStatement ps = null;

    public void insert(Location location) {
        try {
            conn = DBConnection.getConn();

            String sql = "insert into location (component, start_line, end_line, start_offset, end_offset, inst_id) values(?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, location.getComponent());
            ps.setInt(2, location.getStartLine());
            ps.setInt(3, location.getEndLine());
            ps.setInt(4, location.getStartOffset());
            ps.setInt(5, location.getEndOffset());
            ps.setInt(6, location.getInstId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }
}
