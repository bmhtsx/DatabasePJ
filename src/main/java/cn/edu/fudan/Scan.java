package cn.edu.fudan;

import cn.edu.fudan.data.Git_info;
import cn.edu.fudan.data.Issue_info;
import cn.edu.fudan.data.Matcher;
import cn.edu.fudan.entity.Commit;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Scan {


    Connection conn = null;
    PreparedStatement ps = null;


    public void scan_latest(String branch){
        Commit parent_commit=new Commit();
        String project_name="";
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("src/pjInfo.properties"));
            String git_path = properties.getProperty("git_path");
            String sonar_cmd = properties.getProperty("sonar_cmd");
            project_name = properties.getProperty("project_name");
            //CmdExecute.exeCmd("git checkout "+branch,git_path);
            CmdExecute.exeCmd(sonar_cmd + project_name, git_path);
        }catch (IOException e){
            e.printStackTrace();
        }

        Git_info git_info=new Git_info();
        Commit commit=git_info.getLatestInfo(parent_commit,branch);

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
        s= Issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys="+project_name+"&additionalFields=_all&s=FILE_LINE&resolved=false");
        Issue_info.toMap(s,commit.getId());

        Matcher _matcher =new Matcher();
        _matcher.matcher(commit,parent_commit.getId());
    }
}
