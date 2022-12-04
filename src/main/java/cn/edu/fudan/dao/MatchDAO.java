package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.Match;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MatchDAO {

    Connection conn = null;
    PreparedStatement ps = null;

    public void insert(Match match) {
        try {
            conn = DBConnection.getConn();

            String sql = "insert into `match` (parent_id, child_id) values(?,?)";
            ps = conn.prepareStatement(sql);

            ps.setInt(1, match.getParentId());
            ps.setInt(2, match.getChildId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }
}
