package cn.edu.fudan.data;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Git_reset {
    private Git git;
    private Repository repository;
    private Ref branchRef;
    public String branchName;

    public List<Commit> getCommitMessagesInBranch() throws IOException, GitAPIException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/pj_info.properties"));
        String git_path = properties.getProperty("git_path");
        git = Git.open(new File(git_path));
        repository = git.getRepository();
        List<Commit> commitMessages = new ArrayList<>();
        Commit commitMessage = null;
        Iterable<RevCommit> commits = git.log().all().call();
        RevWalk walk = new RevWalk(repository);
        for(RevCommit commit:commits) {
            commitMessage = new Commit();
            boolean foundInThisBranch = false;
            RevCommit targetCommit = walk.parseCommit(commit.getId());
            for(Map.Entry< String,Ref> e : repository.getAllRefs().entrySet()){
// e.getKey()
                if(e.getKey().startsWith("refs/remotes/origin")) {
                    if(walk.isMergedInto(targetCommit,walk.parseCommit(e.getValue().getObjectId()))) {
                        String foundInBranch = e.getValue().getTarget().getName();
// foundInBranch = foundInBranch.replace("refs/heads","");
                        if(foundInBranch.contains(branchName)) {
                            // 如果只想获取commit merge的记录，则在此处添加一个条件即可
                            // if(targetCommit.getParents().length==2) {
                            foundInThisBranch = true;
                            break;
                        }
                    }
                }
            }

            if(foundInThisBranch) {
                commitMessage.setCommitHash(commit.getName());
                commitMessage.setCommitter(commit.getAuthorIdent().getName());
                commitMessage.setCommitTime(commit.getAuthorIdent().getWhen().toString());
                commitMessage.setBranch(branchName);
                commitMessage.setRepository(git_path+".git");
                //commitMessage.setMergeBranchCommitId(commit.getParent(1).getName());
                commitMessages.add(commitMessage);
            }
        }
        return commitMessages;
    }
}
