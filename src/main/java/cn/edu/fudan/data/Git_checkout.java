package cn.edu.fudan.data;

import cn.edu.fudan.CmdExecute;
import cn.edu.fudan.DBConnection;
import cn.edu.fudan.dao.CommitDAO;
import cn.edu.fudan.entity.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Git_checkout {
    private Git git;
    private Repository repository;

    public List<Commit> getCommitMessagesInBranch(String branchName){
        List<Commit> commitMessages = new ArrayList<>();
        //List<Commit> parentCommitMessages = new ArrayList<>();
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("src/pj_info.properties"));
            String git_path = properties.getProperty("git_path");
            String sonar_cmd = properties.getProperty("sonar_cmd");
            git = Git.open(new File(git_path));
            repository = git.getRepository();

            Commit commitMessage = null;
            //Commit parentCommitMessage = null;
            Iterable<RevCommit> commits = git.log().all().call();
            RevWalk walk = new RevWalk(repository);
            for (RevCommit commit : commits) {
                commitMessage = new Commit();
                //parentCommitMessage = new Commit();
                boolean foundInThisBranch = false;
                RevCommit targetCommit = walk.parseCommit(commit.getId());
                for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {

                    if (e.getKey().startsWith("refs/remotes/origin")) {
                        if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
                            String foundInBranch = e.getValue().getTarget().getName();
                            System.out.println(e.getValue().getTarget().getName());
                            if (foundInBranch.contains(branchName)) {
                                // 如果只想获取commit merge的记录，则在此处添加一个条件即可
                                // if(targetCommit.getParents().length==2) {
                                foundInThisBranch = true;
                                break;
                            }
                        }
                    }
                }

                if (foundInThisBranch) {
                    commitMessage.setCommitHash(commit.getName());
                    commitMessage.setCommitter(commit.getAuthorIdent().getName());
                    Timestamp commit_time=CalTime.strToSqlDate(CalTime.checkDate(commit.getAuthorIdent().getWhen().toString()),"yyyy-MM-dd HH:mm:ss") ;
                    commitMessage.setCommitTime(commit_time);
                    commitMessage.setBranch(branchName);
                    commitMessage.setRepository(git_path + ".git");

                    //parentCommitMessage.setRepository(git_path + ".git");
                    //commitMessage.setMergeBranchCommitId(commit.getParent(1).getName());
                    //parentCommitMessages.add(parentCommitMessage);
                    commitMessages.add(commitMessage);


                }
            }
            for (int i = commitMessages.size()-1; i >=0; i--) {
                commitMessage=commitMessages.get(i);
                Commit parentCommitMessage=null;
                if(i+1<commitMessages.size())parentCommitMessage=commitMessages.get(i+1);
                String cmd="git checkout "+commitMessage.getCommitHash();
                CmdExecute.exeCmd(cmd,git_path);
                CmdExecute.exeCmd(sonar_cmd,git_path);
                CommitDAO c=new CommitDAO();
                int git_id=c.insert(commitMessage)-1;
                String s=null;
                s=issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
                issue_info.toMap(s,git_id);
                commitMessage.setId(git_id);

                int parent_commit_id=-1;
                Timestamp parent_commit_time=null;
                String parent_committer="";
                if(parentCommitMessage!=null){
                    parent_commit_id=parentCommitMessage.getId();
                    parent_commit_time=parentCommitMessage.getCommitTime();
                    parent_committer=parentCommitMessage.getCommitter();
                    cmd="git checkout "+parentCommitMessage.getCommitHash();
                    CmdExecute.exeCmd(cmd,git_path);
                }
                Matcher _matcher =new Matcher();
                _matcher.matcher(commitMessage,parent_commit_id,parent_commit_time,parent_committer);
            }
            if(!commitMessages.isEmpty()){
                String cmd="git checkout "+commitMessages.get(0).getCommitHash();
                CmdExecute.exeCmd(cmd,git_path);
            }
        }
        catch (GitAPIException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return commitMessages;
    }
}
