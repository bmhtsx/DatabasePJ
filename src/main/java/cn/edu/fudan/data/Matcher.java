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
    public void matcher(Commit commit, int parent_commit_id,String parent_commit_hash){
        List<RawIssue> preRawIssueList = new ArrayList<>();
        List<RawIssue> curRawIssueList = new ArrayList<>();

        Properties properties = new Properties();


        try {
            properties.load(new FileReader("src/pj_info.properties"));
            conn = DBConnection.getConn();
            String sql1 = "select id,type,message FROM instance where commit_id=?;";
            ResultSet rs1;
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
                    System.out.println("add_a_new_preiss");
                    preRawIssueList.add(preRawIssue1);
                }
            }


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
                System.out.println("add_a_new_curiss");
                curRawIssueList.add(curRawIssue1);
            }
            InstcaseDAO instcaseDAO=new InstcaseDAO();
            if(preRawIssueList.isEmpty()){
                System.out.println("NnParent");
                for (RawIssue rawIssue : curRawIssueList) {
                    InstCase instcase=new InstCase();
                    instcase.setCommitLast(commit.getId());
                    instcase.setCommitNew(commit.getId());
                    instcase.setStatus(rawIssue.getStatus());
                    instcase.setType(rawIssue.getType());
                    //System.out.println(commit.getCommitTime());
                    instcase.setCreateTime(commit.getCommitTime());
                    instcase.setUpdateTime(commit.getCommitTime());
                    instcase.setCommitterNew(commit.getCommitter());
                    instcase.setCommitterLast(commit.getCommitter());
                    instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));
                    instcaseDAO.insert(instcase);
                }
                return;
            }
            AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, commit.getRepository());
            AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, commit.getRepository());


            System.out.println("MATCHING"+curRawIssueList.size());
            for (RawIssue rawIssue : curRawIssueList) {

                String component=rawIssue.getFileName();
                String filepath=component.substring(component.indexOf(":")+1);
                String repository_path=properties.getProperty("git_path");
                System.out.println(repository_path + filepath);
                RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(repository_path + filepath));
            }
            for (RawIssue rawIssue : curRawIssueList) {
                if(rawIssue.getMappedRawIssue()==null){
                    InstCase instcase=new InstCase();
                    instcase.setCommitLast(commit.getId());
                    instcase.setCommitNew(commit.getId());
                    instcase.setStatus(rawIssue.getStatus());
                    instcase.setType(rawIssue.getType());
                    instcase.setCreateTime(commit.getCommitTime());
                    instcase.setUpdateTime(commit.getCommitTime());
                    instcase.setCommitterNew(commit.getCommitter());
                    instcase.setCommitterLast(commit.getCommitter());
                    instcase.setDurationTime(0);
                    instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));
                    instcaseDAO.insert(instcase);
                }
                else{
                    String loc_sql="select id,create_time,commit_new FROM instcase where inst_last=?";
                    ps = conn.prepareStatement(loc_sql);
                    ps.setInt(1,Integer.parseInt(rawIssue.getMappedRawIssue().getUuid()));
                    ResultSet loc_rs = ps.executeQuery();
                    loc_rs.next();
                    int id=loc_rs.getInt("id");
                    Timestamp create_time=loc_rs.getTimestamp("create_time");
                    int commit_new=loc_rs.getInt("commit_new");
                    InstCase instcase=new InstCase();
                    instcase.setCommitNew(commit_new);
                    instcase.setStatus(rawIssue.getStatus());
                    instcase.setType(rawIssue.getType());
                    instcase.setUpdateTime(commit.getCommitTime());
                    instcase.setCommitterNew(commit.getCommitter());
                    instcase.setCommitterLast(commit.getCommitter());
                    instcase.setId(id);
                    instcase.setCreateTime(create_time);
                    instcase.setDurationTime((int)((commit.getCommitTime().getTime()-create_time.getTime())/1000));
                    instcase.setCommitLast(commit.getId());
                    instcase.setInstLast(Integer.parseInt(rawIssue.getUuid()));

                    Match match=new Match();
                    match.setParentId(Integer.parseInt(rawIssue.getMappedRawIssue().getUuid()));
                    match.setChildId(Integer.parseInt(rawIssue.getUuid()));
                    MatchDAO m=new MatchDAO();
                    m.insert(match);

                    instcaseDAO.update(instcase);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            DBConnection.close(conn, ps);
        }
    }

    public static void main(String[] args) throws IOException {

        String type = "Math operands should be cast before assignment";

        /**
         * RawIssue 需要字段 type,fileName,detail,Locations,commitId
         * Location 需要字段 startLine,endLine,startToken
         */
        //1. 初始化，写入需要的字段值
        List<RawIssue> preRawIssueList = new ArrayList<>();
        RawIssue preRawIssue1 = new RawIssue();
        preRawIssue1.setUuid("preRawIssue1");
        preRawIssue1.setType(type);
        preRawIssue1.setFileName("cim:src/main/resources/testFile/commit1/test.java");
        preRawIssue1.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        preRawIssue1.setCommitId("commit1");
        Location preLocation1 = new Location();
        preLocation1.setFilePath("src/main/resources/testFile/commit1/test.java");
        preLocation1.setStartLine(10);
        preLocation1.setEndLine(10);
        preLocation1.setStartToken(0);
        preRawIssue1.setLocations(Collections.singletonList(preLocation1));

        RawIssue preRawIssue2 = new RawIssue();
        preRawIssue2.setUuid("preRawIssue2");
        preRawIssue2.setType(type);
        preRawIssue2.setFileName("cim:src/main/resources/testFile/commit1/test.java");
        preRawIssue2.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        preRawIssue2.setCommitId("commit1");
        Location preLocation2 = new Location();
        preLocation2.setFilePath("src/main/resources/testFile/commit1/test.java");
        preLocation2.setStartLine(11);
        preLocation2.setEndLine(11);
        preLocation2.setStartToken(0);
        preRawIssue2.setLocations(Collections.singletonList(preLocation2));

        preRawIssueList.add(preRawIssue1);
        preRawIssueList.add(preRawIssue2);

        //2. 获取缺陷所在方法名 逻辑代码 偏移量
        AnalyzerUtil.addExtraAttributeInRawIssues(preRawIssueList, baseRepoPath);

        List<RawIssue> curRawIssueList = new ArrayList<>();
        RawIssue curRawIssue1 = new RawIssue();
        curRawIssue1.setUuid("curRawIssue1");
        curRawIssue1.setType(type);
        curRawIssue1.setFileName("src/main/resources/testFile/commit2/test.java");
        curRawIssue1.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        curRawIssue1.setCommitId("commit2");
        Location curLocation1 = new Location();
        curLocation1.setFilePath("src/main/resources/testFile/commit2/test.java");
        curLocation1.setStartLine(10);
        curLocation1.setEndLine(10);
        curLocation1.setStartToken(0);
        curRawIssue1.setLocations(Collections.singletonList(curLocation1));

        RawIssue curRawIssue2 = new RawIssue();
        curRawIssue2.setUuid("curRawIssue2");
        curRawIssue2.setType(type);
        curRawIssue2.setFileName("cim:src/main/resources/testFile/commit2/test.java");
        curRawIssue2.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        curRawIssue2.setCommitId("commit2");
        Location curLocation2 = new Location();
        curLocation2.setFilePath("src/main/resources/testFile/commit2/test.java");
        curLocation2.setStartLine(11);
        curLocation2.setEndLine(11);
        curLocation2.setStartToken(0);
        curRawIssue2.setLocations(Collections.singletonList(curLocation2));

        curRawIssueList.add(curRawIssue1);
        //curRawIssueList.add(curRawIssue2);

        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath);

        //3. 进行映射
        // 前一个版本的缺陷 后一个版本的缺陷 当前版本的文件中所有方法及变量名
        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));

        System.out.println("preRawIssue1:matches " + preRawIssue1.getMappedRawIssue().getUuid());
        System.out.println("preRawIssue2:matches " + preRawIssue2.getMappedRawIssue().getUuid());
        System.out.println("curRawIssue1:matches " + curRawIssue1.getMappedRawIssue().getUuid());
        //System.out.println("curRawIssue2:matches " + curRawIssue2.getMappedRawIssue().getUuid());

    }

}
