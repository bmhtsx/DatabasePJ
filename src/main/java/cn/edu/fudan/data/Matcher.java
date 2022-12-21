package cn.edu.fudan.data;

import cn.edu.fudan.DBConnection;
import cn.edu.fudan.dao.InstcaseDAO;
import cn.edu.fudan.dao.MatchDAO;
import cn.edu.fudan.entity.Commit;
import cn.edu.fudan.entity.InstCase;
import cn.edu.fudan.entity.Match;
import cn.edu.fudan.issue.core.process.RawIssueMatcher;
import cn.edu.fudan.issue.entity.dbo.Location;
import cn.edu.fudan.issue.entity.dbo.RawIssue;
import cn.edu.fudan.issue.util.AnalyzerUtil;
import cn.edu.fudan.issue.util.AstParserUtil;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Matcher {
    private static final String baseRepoPath = System.getProperty("user.dir");
    private static final String SEPARATOR = System.getProperty("file.separator");
    Connection conn = null;
    PreparedStatement ps = null;
    public void matcher(Commit commit, int parent_commit_id){
        List<RawIssue> preRawIssueList = new ArrayList<>();
        List<RawIssue> curRawIssueList = new ArrayList<>();

        Properties properties = new Properties();


        try {
            properties.load(new FileReader("src/pj_info.properties"));
            conn = DBConnection.getConn();
            String sql1 = "select id,type,message FROM instance where commit_id=?;";
            ResultSet rs1;
            try{
                if(parent_commit_id!=-1){
                    ps = conn.prepareStatement(sql1);
                    ps.setInt(1,parent_commit_id);
                    rs1 = ps.executeQuery();
                    while(rs1.next()){
                        RawIssue preRawIssue1 = new RawIssue();
                        int inst_id=rs1.getInt("id");
                        String type=rs1.getString("type");
                        String detail=rs1.getString("message");

                        preRawIssue1.setUuid(""+inst_id);
                        preRawIssue1.setType(type);

                        preRawIssue1.setDetail(detail);
                        preRawIssue1.setCommitId(""+parent_commit_id);

                        Location preLocation1 = new Location();

                        String loc_sql="select component,start_line,end_line,start_offset FROM location where inst_id=?";
                        PreparedStatement ps_loc = conn.prepareStatement(loc_sql);
                        ps_loc.setInt(1,inst_id);
                        ResultSet loc_rs = ps_loc.executeQuery();
                        if(loc_rs.next()){
                            String component=loc_rs.getString("component");
                            preRawIssue1.setFileName(component);
                            int start_line=loc_rs.getInt("start_line");
                            int end_line=loc_rs.getInt("end_line");
                            int start_offset=loc_rs.getInt("start_offset");
                            preLocation1.setStartLine(start_line);
                            preLocation1.setEndLine(end_line);
                            preLocation1.setStartToken(start_offset);
                            preRawIssue1.setLocations(Collections.singletonList(preLocation1));
                        }
                        else{
                            break;
                        }
                        //System.out.println("add_a_new_preiss");
                        preRawIssueList.add(preRawIssue1);
                    }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                String sql2 = "select id,type,message,status FROM instance where commit_id=?;";
                ps = conn.prepareStatement(sql2);
                ps.setInt(1,commit.getId());
                ResultSet rs2 = ps.executeQuery();
                while(rs2.next()){

                    RawIssue curRawIssue1 = new RawIssue();
                    int inst_id=rs2.getInt("id");
                    String type=rs2.getString("type");
                    String detail=rs2.getString("message");
                    String status=rs2.getString("status");
                    curRawIssue1.setUuid(""+inst_id);
                    curRawIssue1.setType(type);

                    curRawIssue1.setDetail(detail);
                    curRawIssue1.setCommitId(""+parent_commit_id);
                    curRawIssue1.setStatus(status);

                    Location curLocation1 = new Location();

                    String loc_sql="select component,start_line,end_line,start_offset FROM location where inst_id=?";
                    PreparedStatement ps_loc = conn.prepareStatement(loc_sql);
                    ps_loc.setInt(1,inst_id);
                    ResultSet loc_rs = ps_loc.executeQuery();
                    if(loc_rs.next()){
                        String component=loc_rs.getString("component");
                        curRawIssue1.setFileName(component);
                        int start_line=loc_rs.getInt("start_line");
                        int end_line=loc_rs.getInt("end_line");
                        int start_offset=loc_rs.getInt("start_offset");
                        curLocation1.setStartLine(start_line);
                        curLocation1.setEndLine(end_line);
                        curLocation1.setStartToken(start_offset);
                        curRawIssue1.setLocations(Collections.singletonList(curLocation1));
                    }
                    //System.out.println("add_a_new_curiss");
                    curRawIssueList.add(curRawIssue1);
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }

            String curcommitter="";
            String precommitter="";
            Timestamp cur_time=commit.getCommitTime();
            Timestamp pre_time=commit.getCommitTime();
            try {
                String sql2 = "select committer,commit_time FROM commit where id=?;";
                ps = conn.prepareStatement(sql2);
                ps.setInt(1,commit.getId());
                ResultSet rs2 = ps.executeQuery();
                if(rs2.next()){
                    curcommitter=rs2.getString("committer");
                    cur_time=rs2.getTimestamp("commit_time");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            InstcaseDAO instcaseDAO=new InstcaseDAO();
            if(parent_commit_id==-1){
                System.out.println("NnParent");
                for (RawIssue rawIssue : curRawIssueList) {
                    InstCase instcase=new InstCase();
                    instcase.setCommitLast(commit.getId());
                    instcase.setCommitNew(commit.getId());
                    instcase.setStatus(rawIssue.getStatus());
                    instcase.setType(rawIssue.getType());
                    //System.out.println(commit.getCommitTime());
                    instcase.setCreateTime(cur_time);
                    instcase.setUpdateTime(cur_time);
                    instcase.setCommitterNew(curcommitter);
                    instcase.setCommitterLast(curcommitter);
                    instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));
                    instcaseDAO.insert(instcase);
                }
                return;
            }
            AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, commit.getRepository());
            AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, commit.getRepository());

            try {
                String sql2 = "select committer,commit_time FROM commit where id=?;";
                ps = conn.prepareStatement(sql2);
                ps.setInt(1,parent_commit_id);
                ResultSet rs2 = ps.executeQuery();
                if(rs2.next()){
                    precommitter=rs2.getString("committer");
                    pre_time=rs2.getTimestamp("commit_time");
                }
            }catch (SQLException e){
                e.printStackTrace();
            }

            System.out.println("MATCHING"+curRawIssueList.size());
            for (RawIssue rawIssue : curRawIssueList) {
                try{
                    String component=rawIssue.getFileName();
                    String filepath=component.substring(component.indexOf(":")+1);
                    String repository_path=properties.getProperty("git_path");
                    System.out.println(repository_path + filepath);
                    RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(repository_path + filepath));
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (RawIssue rawIssue : curRawIssueList) {
                if(rawIssue.getMappedRawIssue()==null){
                    InstCase instcase=new InstCase();
                    instcase.setCommitLast(commit.getId());
                    instcase.setCommitNew(commit.getId());
                    instcase.setStatus(rawIssue.getStatus());
                    instcase.setType(rawIssue.getType());
                    instcase.setCreateTime(cur_time);
                    instcase.setUpdateTime(cur_time);
                    instcase.setCommitterNew(curcommitter);
                    instcase.setCommitterLast(curcommitter);
                    instcase.setDurationTime(0);
                    instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));
                    instcaseDAO.insert(instcase);
                }
                else{
                    try{
                        String loc_sql="select id,create_time,commit_new,committer_new FROM instcase where inst_last=?";
                        ps = conn.prepareStatement(loc_sql);
                        ps.setInt(1,Integer.parseInt(rawIssue.getMappedRawIssue().getUuid()));
                        ResultSet loc_rs = ps.executeQuery();
                        InstCase instcase=new InstCase();
                        if(loc_rs.next()){
                            int id=loc_rs.getInt("id");
                            Timestamp create_time=loc_rs.getTimestamp("create_time");
                            int commit_new=loc_rs.getInt("commit_new");
                            String committer_new=loc_rs.getString("committer_new");
                            instcase.setCommitNew(commit_new);
                            instcase.setStatus(rawIssue.getStatus());
                            instcase.setType(rawIssue.getType());
                            instcase.setUpdateTime(cur_time);
                            instcase.setCommitterNew(committer_new);
                            instcase.setCommitterLast(curcommitter);
                            instcase.setId(id);
                            instcase.setCreateTime(create_time);
                            instcase.setDurationTime((int)((commit.getCommitTime().getTime()-create_time.getTime())/1000));
                            instcase.setCommitLast(commit.getId());
                            instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));
                        }
                        else {
                            System.out.println("Instcase not found, while parent instance exists.");
                            instcase.setCommitLast(commit.getId());
                            instcase.setCommitNew(parent_commit_id);
                            instcase.setStatus(rawIssue.getStatus());
                            instcase.setType(rawIssue.getType());
                            instcase.setCreateTime(pre_time);
                            instcase.setUpdateTime(cur_time);
                            instcase.setCommitterNew(precommitter);
                            instcase.setCommitterLast(curcommitter);
                            instcase.setDurationTime((int)((cur_time.getTime()-pre_time.getTime())/1000));
                            instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));
                            instcaseDAO.insert(instcase);
                        }
                        instcaseDAO.update(instcase);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Match match=new Match();
                    match.setParentId(Integer.parseInt(rawIssue.getMappedRawIssue().getUuid()));
                    match.setChildId(Integer.parseInt(rawIssue.getUuid()));
                    MatchDAO m=new MatchDAO();
                    m.insert(match);
                }
            }
            for (RawIssue rawIssue : preRawIssueList) {
                if(rawIssue.getMappedRawIssue()==null){
                    try{
                        String loc_sql="select id,create_time,commit_new,committer_new FROM instcase where inst_last=?";
                        ps = conn.prepareStatement(loc_sql);
                        ps.setInt(1,Integer.parseInt(rawIssue.getUuid()));
                        ResultSet loc_rs = ps.executeQuery();
                        loc_rs.next();
                        int id=loc_rs.getInt("id");
                        Timestamp create_time=loc_rs.getTimestamp("create_time");
                        int commit_new=loc_rs.getInt("commit_new");
                        String committer_new=loc_rs.getString("committer_new");
                        InstCase instcase=new InstCase();
                        instcase.setCommitNew(commit_new);
                        instcase.setStatus("CLOSED");
                        instcase.setType(rawIssue.getType());
                        instcase.setUpdateTime(cur_time);
                        instcase.setCommitterNew(committer_new);
                        instcase.setCommitterLast(curcommitter);
                        instcase.setId(id);
                        instcase.setCreateTime(create_time);
                        instcase.setDurationTime((int)((commit.getCommitTime().getTime()-create_time.getTime())/1000));
                        instcase.setCommitLast(commit.getId());
                        instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));

                        instcaseDAO.update(instcase);
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            DBConnection.close(conn, ps);
        }
    }
}
