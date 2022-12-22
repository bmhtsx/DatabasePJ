package cn.edu.fudan.dao;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.data.CalTime;
import cn.edu.fudan.entity.Commit;
import cn.edu.fudan.entity.InstCase;
import cn.edu.fudan.entity.Instance;


import java.sql.*;
import java.util.*;

public class InstcaseDAO {

    Connection conn = null;
    PreparedStatement ps = null;
    InstanceDAO instanceDAO = new InstanceDAO();

    public int insert(InstCase instCase) {
        int res = 0;
        try {
            conn = DBConnection.getConn();

            String sql = "insert into instcase (status, type, inst_last, commit_new, commit_last, create_time, update_time, committer_new, committer_last, duration_time) values(?,?,?,?,?,?,?,?,?,?)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, instCase.getStatus());
            ps.setString(2, instCase.getType());
            ps.setInt(3, instCase.getInstLast());
            ps.setInt(4, instCase.getCommitNew());
            ps.setInt(5, instCase.getCommitLast());
            ps.setTimestamp(6, instCase.getCreateTime());
            ps.setTimestamp(7, instCase.getUpdateTime());
            ps.setString(8, instCase.getCommitterNew());
            ps.setString(9, instCase.getCommitterLast());
            ps.setInt(10, instCase.getDurationTime());

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

            String sql = "update instcase set status=?, type=?, inst_last=?, commit_new=?, commit_last=?, create_time=?, update_time=?, committer_new=?, committer_last=?, duration_time=? where id=?";
            ps = conn.prepareStatement(sql);

            ps.setString(1, instCase.getStatus());
            ps.setString(2, instCase.getType());
            ps.setInt(3, instCase.getInstLast());
            ps.setInt(4, instCase.getCommitNew());
            ps.setInt(5, instCase.getCommitLast());
            ps.setTimestamp(6, instCase.getCreateTime());
            ps.setTimestamp(7, instCase.getUpdateTime());
            ps.setString(8, instCase.getCommitterNew());
            ps.setString(9, instCase.getCommitterLast());
            ps.setInt(10, instCase.getDurationTime());
            ps.setInt(11, instCase.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    void addToInstcaseList(List<InstCase> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            InstCase instcase = new InstCase();
            instcase.setId(rs.getInt("id"));
            instcase.setStatus(rs.getString("status"));
            instcase.setType(rs.getString("type"));
            instcase.setInstLast(rs.getInt("inst_last"));
            instcase.setCommitNew(rs.getInt("commit_new"));
            instcase.setCommitLast(rs.getInt("commit_last"));
            instcase.setCreateTime(rs.getTimestamp("create_time"));
            instcase.setUpdateTime(rs.getTimestamp("update_time"));
            instcase.setCommitterNew(rs.getString("committer_new"));
            instcase.setCommitterLast(rs.getString("committer_last"));
            instcase.setDurationTime(rs.getInt("duration_time"));

            list.add(instcase);
        }
    }

    void addToCommitList(List<Commit> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            Commit commit = new Commit();
            commit.setId(rs.getInt("id"));
            commit.setCommitHash(rs.getString("commit_hash"));
            commit.setBranch(rs.getString("branch"));
            commit.setRepository(rs.getString("repository"));
            commit.setCommitter(rs.getString("committer"));
            commit.setCommitTime(rs.getTimestamp("commit_time"));

            list.add(commit);
        }
    }

    void addToInstanceList(List<Instance> list, ResultSet rs) throws SQLException {
        while (rs.next()) {
            list.add(instanceDAO.addToInstance(rs));
        }
    }

    public List<Instance> getInstByTypeInLatestCommit(String type) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select max(id) from commit";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int commitLast = rs.getInt(1);

            String sql2 = "select * from instance where commit_id=? and type=?";
            ps = conn.prepareStatement(sql2);

            ps.setInt(1, commitLast);
            ps.setString(2, type);
            rs = ps.executeQuery();

            addToInstanceList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<String> getSortedInstByTimeInLatestCommit(Boolean greater) {
        List<Instance> list = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select max(id) from commit";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int commitLast = rs.getInt(1);

            String sql2 = greater ?
                    "select i.*, ic.duration_time from instance i join (select inst_last, duration_time from instcase where commit_last = ?) ic on i.id = ic.inst_last order by ic.duration_time" :
                    "select i.*, ic.duration_time from instance i join (select inst_last, duration_time from instcase where commit_last = ?) ic on i.id = ic.inst_last order by ic.duration_time desc";
            ps = conn.prepareStatement(sql2, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);

            ps.setInt(1, commitLast);
            rs = ps.executeQuery();

            addToInstanceList(list, rs);
            rs.first();
            for (Instance instance : list) {
                stringList.add("durationTime=" + CalTime.calTime(rs.getInt("duration_time")) + ", " + instance.toString());
                rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return stringList;
    }

    public List<Integer> getAvgAndMedOfTimeInLatestCommit(String type) {
        List<Integer> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select max(id) from commit";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int commitLast = rs.getInt(1);

            String sql2 = "select round(avg(duration_time)) from instcase where type=? and commit_last=?";

            ps = conn.prepareStatement(sql2);
            ps.setString(1, type);
            ps.setInt(2, commitLast);
            rs = ps.executeQuery();
            rs.next();
            list.add(rs.getInt(1));
//
//            String sql3 = "";
//            ps = conn.prepareStatement(sql3);
//            ps.executeQuery();
//            String sql4 = "set @rowindex := 0;select round(avg(duration_time)) from (select @rowindex:=@rowindex+1 as rowindex, duration_time from instcase where type=? order by duration_time) as S where S.rowindex in (floor((@rowindex+1)/2), ceil((@rowindex+1)/2));";
//            ps = conn.prepareStatement(sql4);
//            ps.setString(1, type);
//            rs = ps.executeQuery();
//            rs.next();
//            list.add(rs.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Commit> getCommitByTimeDesc() {
        List<Commit> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = "select * from commit order by id desc";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            addToCommitList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getInstByStatusAndCommitAndType(String status, int commitId, String type) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = null;
            if (Objects.equals(status, "new"))
                sql = "select * from instance where id in (select inst_last from instcase where commit_new=? and type=?)";
            else if (Objects.equals(status, "fixed"))
                sql = "select * from instance where id in (select inst_last from instcase where commit_last=? and type=? and status='CLOSED')";
            ps = conn.prepareStatement(sql);

            ps.setInt(1, commitId);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();

            addToInstanceList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getInstByStatusAndTimePeriod(String status, Timestamp startTime, Timestamp endTime) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = null;
            if (Objects.equals(status, "new"))
                sql = "select * from instance where id in (select inst_last from instcase where create_time>=? and create_time<=?)";
            else if (Objects.equals(status, "fixed"))
                sql = "select * from instance where id in (select inst_last from instcase where status='CLOSED' and update_time>=? and update_time<=?)";
            ps = conn.prepareStatement(sql);

            ps.setTimestamp(1, startTime);
            ps.setTimestamp(2, endTime);
            ResultSet rs = ps.executeQuery();

            addToInstanceList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public List<Instance> getInstByStatusAndAuthorAndTimePeriod(String status1, String status2, String author, Timestamp startTime, Timestamp endTime) {
        List<Instance> list = new ArrayList<>();
        try {
            conn = DBConnection.getConn();
            String sql = null;
            if (Objects.equals(status1, "new")) {
                if (Objects.equals(status2, "self"))
                    sql = "select * from instance where id in (select inst_last from instcase where committer_new=? and create_time>=? and create_time<=?)";
                else if (Objects.equals(status2, "others"))
                    sql = "select * from instance where id in (select inst_last from instcase where committer_new!=? and create_time>=? and create_time<=?)";
            } else if (Objects.equals(status1, "fixed")) {
                if (Objects.equals(status2, "self"))
                    sql = "select * from instance where id in (select inst_last from instcase where status='CLOSED' and committer_last=? and update_time>=? and update_time<=?)";
                else if (Objects.equals(status2, "others"))
                    sql = "select * from instance where id in (select inst_last from instcase where status='CLOSED' and committer_last!=? and update_time>=? and update_time<=?)";
            }
            ps = conn.prepareStatement(sql);

            ps.setString(1, author);
            ps.setTimestamp(2, startTime);
            ps.setTimestamp(3, endTime);
            ResultSet rs = ps.executeQuery();

            addToInstanceList(list, rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        } return list;
    }

    public void getStatisticsByTime(Timestamp startTime, Timestamp endTime) {
        try {
            conn = DBConnection.getConn();

            String sql1 = "select type, count(*) from instcase where create_time>=? and create_time<=? group by type";
            ps = conn.prepareStatement(sql1);
            ps.setTimestamp(1, startTime);
            ps.setTimestamp(2, endTime);
            ResultSet rs = ps.executeQuery();
            System.out.println("new issue number:");
            Map<String, Integer> newmap = new HashMap<>();
            while (rs.next()) {
                newmap.put(rs.getString(1), rs.getInt(2));
                System.out.print(rs.getString(1)+" "+rs.getInt(2)+" ");
            } System.out.println();

            String sql2 = "select type, count(*) from instcase where status=? and create_time>=? and create_time<=? group by type";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, "changed");
            ps.setTimestamp(2, startTime);
            ps.setTimestamp(3, endTime);
            rs = ps.executeQuery();
            System.out.println("resolve rate: ");
            while (rs.next()) {
                System.out.print(rs.getString(1)+" "+String.format("%.3f", (double)rs.getInt(2) / newmap.get(rs.getString(1)))+" ");
            } System.out.println();

            String sql3 = "select type, avg(duration_time) from instcase where create_time>=? and create_time<=? group by type";
            ps = conn.prepareStatement(sql3);
            ps.setTimestamp(1, startTime);
            ps.setTimestamp(2, endTime);
            rs = ps.executeQuery();
            System.out.println("duration time: ");
            while (rs.next()) {
                System.out.print(rs.getString(1)+" "+ CalTime.calTime(rs.getInt(2))+" ");
            } System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    public void getStatisticsByTimeLongerThan(int time) {
        try {
            conn = DBConnection.getConn();
            String sql = "select type, count(*) from instcase where duration_time>=? group by type";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, time);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.print(rs.getString(1)+" "+rs.getInt(2)+" ");
            } System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    public void getStatisticsByAuthor(String author) {
        try {
            conn = DBConnection.getConn();

            String sql1 = "select type, count(*), avg(duration_time) from instcase where status='CLOSED' and committer_new=? and committer_last=? group by type";
            ps = conn.prepareStatement(sql1);
            ps.setString(1, author);
            ps.setString(2, author);
            ResultSet rs = ps.executeQuery();
            System.out.println("self create self resolve:");
            while (rs.next()) {
                System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+CalTime.calTime(rs.getInt(3)));
            }

            String sql2 = "select type, count(*), avg(duration_time) from instcase where status='CLOSED' and committer_new!=? and committer_last=? group by type";
            ps = conn.prepareStatement(sql2);
            ps.setString(1, author);
            ps.setString(2, author);
            rs = ps.executeQuery();
            System.out.println("others create self resolve:");
            while (rs.next()) {
                System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+CalTime.calTime(rs.getInt(3)));
            }

            String sql3 = "select type, count(*), avg(duration_time) from instcase where status='changed' and committer_new=? group by type";
            ps = conn.prepareStatement(sql3);
            ps.setString(1, author);
            rs = ps.executeQuery();
            System.out.println("self create not resolved:");
            while (rs.next()) {
                System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+CalTime.calTime(rs.getInt(3)));
            }

            String sql4 = "select type, count(*), avg(duration_time) from instcase where status='CLOSED' and committer_new=? and committer_last!=? group by type";
            ps = conn.prepareStatement(sql4);
            ps.setString(1, author);
            ps.setString(2, author);
            rs = ps.executeQuery();
            System.out.println("self create others resolve:");
            while (rs.next()) {
                System.out.println(rs.getString(1)+" "+rs.getInt(2)+" "+CalTime.calTime(rs.getInt(3)));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }
    }
}
