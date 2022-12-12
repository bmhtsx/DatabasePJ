package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.InstCase;
import cn.edu.fudan.entity.Instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            rs.next();
            res = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return res;
    }

    public void update(InstCase instCase) {
        try {
            conn = DBConnection.getConn();

            String sql = "update instcase set status=?, type=?, commit_new=?, commit_last=?, create_time=?, update_time=?, committer_new=?, committer_last=? where id=?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, instCase.getStatus());
            ps.setString(2, instCase.getType());
            ps.setString(3, instCase.getCommitNew());
            ps.setString(4, instCase.getCommitLast());
            ps.setString(5, instCase.getCreateTime());
            ps.setString(6, instCase.getUpdateTime());
            ps.setString(7, instCase.getCommitterNew());
            ps.setString(8, instCase.getCommitterLast());
            ps.setInt(9, instCase.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    void addToList(List<Instance> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Instance instance = new Instance();
            instance.setId(rs.getInt("id"));
            instance.setCommitId(rs.getInt("commit_id"));
            instance.setSeverity(rs.getString("severity"));
            instance.setType(rs.getString("type"));
            instance.setStatus(rs.getString("status"));
            instance.setAuthor(rs.getString("author"));
            instance.setMessage(rs.getString("message"));
            instance.setCreationDate(rs.getString("creation_date"));
            instance.setUpdateDate(rs.getString("update_date"));

            list.add(instance);
        }
    }

    public List<Instance> getNewInstByCommit(String commitHash) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instcase where commit_new = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, commitHash);
            ResultSet rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getSolvedInstByCommit(String commitHash) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instcase where commit_new = ? and status = 'SOLVED'";
            ps = conn.prepareStatement(sql);

            ps.setString(1, commitHash);
            ResultSet rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getNewInstByTime(String startTime, String endTime) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instcase where create_time >= ? and create_time <= ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, startTime);
            ps.setString(2, endTime);
            ResultSet rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getSolvedInstByTime(String startTime, String endTime) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instcase where status = 'SOLVED' and update_time >= ? and update_time <= ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, startTime);
            ps.setString(2, endTime);
            ResultSet rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getNewInstByAuthor(String author) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instcase where committer_new = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, author);
            ResultSet rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getSolvedInstByTime(String author) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from instcase where status = 'SOLVED' and committer_last = ?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, author);
            ResultSet rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }
}
