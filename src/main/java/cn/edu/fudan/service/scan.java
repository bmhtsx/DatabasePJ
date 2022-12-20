package cn.edu.fudan.service;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.data.Git_info;
import cn.edu.fudan.data.issue_info;
import cn.edu.fudan.data.Matcher;
import cn.edu.fudan.entity.Commit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class scan {
    Commit parent_commit;

    Connection conn = null;
    PreparedStatement ps = null;


    public void scan_latest(){
        Git_info git_info=new Git_info();
        Commit commit=git_info.getLatestInfo(parent_commit);

        if(parent_commit.getCommitHash()!=null) {
            try {
                conn = DBConnection.getConn();
                ps = conn.prepareStatement("select id FROM commit where commit_hash=? and repository=?;");
                ps.setString(1, parent_commit.getCommitHash());
                ps.setString(2, parent_commit.getRepository());
                ResultSet rs = ps.executeQuery();
                rs.next();
                parent_commit.setId(rs.getInt(1));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.close(conn, ps);
            }
        }
        else {
            parent_commit.setId(-1);
        }


        String s=null;
        s=issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        issue_info.toMap(s,commit.getId());

        Matcher _matcher =new Matcher();
        _matcher.matcher(commit,parent_commit.getId(),parent_commit.getCommitTime(),parent_commit.getCommitter());
    }
}
