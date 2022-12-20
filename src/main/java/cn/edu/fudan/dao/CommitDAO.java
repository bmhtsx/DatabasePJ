package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.Commit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommitDAO {

    Connection conn = null;
    PreparedStatement ps = null;

    public int insert(Commit commit) {
        int res = 0;
        try {
            conn = DBConnection.getConn();

            String sql = "insert into commit (commit_hash, branch, repository, committer, commit_time) values(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, commit.getCommitHash());
            ps.setString(2, commit.getBranch());
            ps.setString(3, commit.getRepository());
            ps.setString(4, commit.getCommitter());
            ps.setTimestamp(5, commit.getCommitTime());

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
        }
        return res;
    }
}
