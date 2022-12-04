package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.InstCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstcaseDAO {

    Connection conn = null;
    PreparedStatement ps = null;

    public int insert(InstCase instCase) {
        int res = 0;
        try {
            conn = DBConnection.getConn();

            String sql = "insert into instcase (status, type, commit_new, commit_last, create_time, update_time, committer_new, committer_last) values(?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, instCase.getStatus());
            ps.setString(2, instCase.getType());
            ps.setString(3, instCase.getCommitNew());
            ps.setString(4, instCase.getCommitLast());
            ps.setString(5, instCase.getCreateTime());
            ps.setString(6, instCase.getUpdateTime());
            ps.setString(7, instCase.getCommitterNew());
            ps.setString(8, instCase.getCommitterLast());

            ps.executeUpdate();

            sql = "select LAST_INSERT_ID();";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            res = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return res;
    }
}
