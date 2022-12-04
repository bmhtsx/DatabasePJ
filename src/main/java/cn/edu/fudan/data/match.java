package cn.edu.fudan.data;

import cn.edu.fudan.issue.core.process.RawIssueMatcher;
import cn.edu.fudan.issue.entity.dbo.Location;
import cn.edu.fudan.issue.entity.dbo.RawIssue;
import cn.edu.fudan.issue.util.AnalyzerUtil;
import cn.edu.fudan.issue.util.AstParserUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class match {
    private static final String baseRepoPath = System.getProperty("user.dir");
    private static final String SEPARATOR = System.getProperty("file.separator");

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
        preRawIssue1.setFileName("src/main/test.java");
        preRawIssue1.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        preRawIssue1.setCommitId("commit1");
        Location preLocation1 = new Location();
        preLocation1.setStartLine(10);
        preLocation1.setEndLine(10);
        preLocation1.setStartToken(0);
        preRawIssue1.setLocations(Collections.singletonList(preLocation1));

        RawIssue preRawIssue2 = new RawIssue();
        preRawIssue2.setUuid("preRawIssue2");
        preRawIssue2.setType(type);
        preRawIssue2.setFileName("src/main/test.java");
        preRawIssue2.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        preRawIssue2.setCommitId("commit1");
        Location preLocation2 = new Location();
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
        curRawIssue1.setFileName("src/main/test.java");
        curRawIssue1.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        curRawIssue1.setCommitId("commit2");
        Location curLocation1 = new Location();
        curLocation1.setStartLine(10);
        curLocation1.setEndLine(10);
        curLocation1.setStartToken(0);
        curRawIssue1.setLocations(Collections.singletonList(curLocation1));

        RawIssue curRawIssue2 = new RawIssue();
        curRawIssue2.setUuid("curRawIssue2");
        curRawIssue2.setType(type);
        curRawIssue2.setFileName("src/main/test.java");
        curRawIssue2.setDetail("Cast one of the operands of this multiplication operation to a \"long\".---MINOR");
        curRawIssue2.setCommitId("commit2");
        Location curLocation2 = new Location();
        curLocation2.setStartLine(11);
        curLocation2.setEndLine(11);
        curLocation2.setStartToken(0);
        curRawIssue2.setLocations(Collections.singletonList(curLocation2));

        curRawIssueList.add(curRawIssue1);
        curRawIssueList.add(curRawIssue2);

        AnalyzerUtil.addExtraAttributeInRawIssues(curRawIssueList, baseRepoPath);

        //3. 进行映射
        // 前一个版本的缺陷 后一个版本的缺陷 当前版本的文件中所有方法及变量名
        RawIssueMatcher.match(preRawIssueList, curRawIssueList, AstParserUtil.getMethodsAndFieldsInFile(baseRepoPath + SEPARATOR + "src/main/resources/testFile/commit2/test.java"));

        System.out.println("preRawIssue1:matches " + preRawIssue1.getMappedRawIssue().getUuid());
        System.out.println("preRawIssue2:matches " + preRawIssue2.getMappedRawIssue().getUuid());
        System.out.println("curRawIssue1:matches " + curRawIssue1.getMappedRawIssue().getUuid());
        System.out.println("curRawIssue2:matches " + curRawIssue2.getMappedRawIssue().getUuid());

    }

}
