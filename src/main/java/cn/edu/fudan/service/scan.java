package cn.edu.fudan.service;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.data.Git_info;
import cn.edu.fudan.data.issue_info;
import cn.edu.fudan.data.match;
import cn.edu.fudan.entity.Commit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class scan {
    int commit_id;
    int parent_commit_id=-1;
    String parent_commit_hash=null;

    Connection conn = null;
    PreparedStatement ps = null;

    public void scan_latest(){
        try {
            conn = DBConnection.getConn();
            ps = conn.prepareStatement("select MAX(id) FROM commit;");
            ResultSet rs = ps.executeQuery();
            rs.next();
            parent_commit_id = rs.getInt(1);
            ps = conn.prepareStatement("select commit_hash FROM commit where id=?;");
            rs = ps.executeQuery();
            rs.next();
            parent_commit_hash = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(conn, ps);
        }

        Git_info git_info=new Git_info();
        Commit commit=git_info.getHistoryInfo(true);
        String s=null;
        s=issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        issue_info.toMap(s,commit.getId());

        match _match=new match();
        _match.matcher(commit,parent_commit_id,parent_commit_hash);
    }

    public void scan_all(){

    }

    public int getCommit_id(){return commit_id;}

    public int getParent_commit_id(){return parent_commit_id;}
}
