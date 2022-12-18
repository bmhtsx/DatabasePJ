package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.entity.InstCase;


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

            String sql = "insert into instcase (status, type, commit_new, commit_last, create_time, update_time, committer_new, committer_last, duration_time) values(?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, instCase.getStatus());
            ps.setString(2, instCase.getType());
            ps.setString(3, instCase.getCommitNew());
            ps.setString(4, instCase.getCommitLast());
            ps.setString(5, instCase.getCreateTime());
            ps.setString(6, instCase.getUpdateTime());
            ps.setString(7, instCase.getCommitterNew());
            ps.setString(8, instCase.getCommitterLast());
            ps.setInt(9, instCase.getDurationTime());

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

            String sql = "update instcase set status=?, type=?, commit_new=?, commit_last=?, create_time=?, update_time=?, committer_new=?, committer_last=?, duration_time=? where id=?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, instCase.getStatus());
            ps.setString(2, instCase.getType());
            ps.setString(3, instCase.getCommitNew());
            ps.setString(4, instCase.getCommitLast());
            ps.setString(5, instCase.getCreateTime());
            ps.setString(6, instCase.getUpdateTime());
            ps.setString(7, instCase.getCommitterNew());
            ps.setString(8, instCase.getCommitterLast());
            ps.setInt(9, instCase.getDurationTime());
            ps.setInt(10, instCase.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    void addToList(List<InstCase> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            InstCase instcase = new InstCase();
            instcase.setId(rs.getInt("id"));
            instcase.setStatus(rs.getString("status"));
            instcase.setType(rs.getString("type"));
            instcase.setCommitNew(rs.getString("commit_new"));
            instcase.setCommitLast(rs.getString("commit_last"));
            instcase.setCreateTime(rs.getString("create_time"));
            instcase.setUpdateTime(rs.getString("update_time"));
            instcase.setCommitterNew(rs.getString("committer_new"));
            instcase.setCommitterLast(rs.getString("committer_last"));
            instcase.setDurationTime(rs.getInt("duration_time"));

            list.add(instcase);
        }
    }

    public List<InstCase> getInstByTypeInLatestCommit(String type) {
        List<InstCase> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from commit order by id desc limit 1";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            String commitLast = rs.getString("commit_hash");

            String sql2 = "select * from instcase where commit_last=? and type=?";
            ps = conn.prepareStatement(sql2);

            ps.setString(1, commitLast);
            ps.setString(2, type);
            rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<InstCase> getSortedInstByTimeInLatestCommit(Boolean greater) {
        List<InstCase> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from commit order by id desc limit 1";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            String commitLast = rs.getString("commit_hash");

            String sql2 = greater ?
                    "select * from instcase where commit_last=? order by duration_time" :
                    "select * from instcase where commit_last=? order by duration_time desc";
            ps = conn.prepareStatement(sql2);

            ps.setString(1, commitLast);
            rs = ps.executeQuery();

            addToList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Integer> getAvgAndMedOfTimeInLatestCommit(String type) {
        List<Integer> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from commit order by id desc limit 1";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            String commitLast = rs.getString("commit_hash");

            String sql2 = "select avg(duration_time) from instcase";
            ps = conn.prepareStatement(sql2);
            rs = ps.executeQuery();
            list.add(rs.getInt(1));

            String sql3 = "select avg(duration_time) from (select duration_time,@a:=@a+1 b from instcase,(select @a:=0) t2 order by duration_time) t where b between @a/2 and @a/2+1";
            ps = conn.prepareStatement(sql3);
            rs = ps.executeQuery();
            list.add(rs.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<InstCase> getNewInstByCommit(String commitHash) {
        List<InstCase> list = new ArrayList<>();
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

    public List<InstCase> getSolvedInstByCommit(String commitHash) {
        List<InstCase> list = new ArrayList<>();
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

    public List<InstCase> getNewInstByTime(String startTime, String endTime) {
        List<InstCase> list = new ArrayList<>();
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

    public List<InstCase> getSolvedInstByTime(String startTime, String endTime) {
        List<InstCase> list = new ArrayList<>();
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

    public List<InstCase> getNewInstByAuthor(String author) {
        List<InstCase> list = new ArrayList<>();
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

    public List<InstCase> getSolvedInstByTime(String author) {
        List<InstCase> list = new ArrayList<>();
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
