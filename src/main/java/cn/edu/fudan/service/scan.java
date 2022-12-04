package cn.edu.fudan.service;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.data.Git_info;
import cn.edu.fudan.data.issue_info;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class scan {
    int commit_id;
    int parent_commit_id;

    Connection conn = null;
    PreparedStatement ps = null;

    public void scan_latest(){
        try {
            conn = DBConnection.getConn();
            ps = conn.prepareStatement("select LAST_INSERT_ID();");
            ResultSet rs = ps.executeQuery();
            parent_commit_id = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }

        Git_info git_info=new Git_info();
        commit_id=git_info.getHistoryInfo();
        String s=null;
        s=issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        issue_info.toMap(s,commit_id);
    }
}
