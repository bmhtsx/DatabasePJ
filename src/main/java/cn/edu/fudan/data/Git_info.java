package cn.edu.fudan.data;


import cn.edu.fudan.dao.CommitDAO;
import cn.edu.fudan.entity.Commit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Properties;

public class Git_info {
    /**
     * getMatcher 正则表达式方法
     * @param regex
     * @param source
     * @return
     */
    public static String getMatcher(String regex, String source) {
        String str = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            str = matcher.group(1); // 只取第一组
        }
        return str;
    }

    /**
     * 获取Git本地仓库版本号
     *
     * @return versionID 版本号
     * @throws IOException
     * @throws GitAPIException
     */
    public static String Log(String repositoryDir) throws IOException, GitAPIException {
        String versionID = "";
        Repository localRepo;
        localRepo = new FileRepository(repositoryDir + "/.git");
        Git git = new Git(localRepo);
        System.out.println("获取本地");
        Iterable<RevCommit> iterable = git.log().all().call();
        for (RevCommit u : iterable) {
            //版本id
            String ID = String.valueOf(u.getId());
            versionID = getMatcher("commit\\s(\\w+)\\s?", ID);
            break;
        }
        git.close();
        return versionID;
    }


    static Git git;
    //历史记录
    public Commit getLatestInfo(Commit parent_commit,String branch) {
        String git_path="";
        try {
            Properties properties = new Properties();
            properties.load(new FileReader("src/pjInfo.properties"));
            git_path = properties.getProperty("git_path");
        } catch (Exception e) {
            e.printStackTrace();
        }
        int git_id=0;
        File gitDir = new File(git_path+".git");
        Commit latest_commit=new Commit();
        CommitDAO c=new CommitDAO();
        try {

            if (git == null) {
                git = Git.open(gitDir);
            }
            Iterable<RevCommit> gitlog= git.log().call();
            for (RevCommit revCommit : gitlog) {
                String version=revCommit.getName();//版本号
                version+=" "+revCommit.getAuthorIdent().getName();
                version+=" "+revCommit.getAuthorIdent().getEmailAddress();
                version+=" "+revCommit.getAuthorIdent().getWhen();//时间
                System.out.println(version);
                latest_commit.setCommitHash(revCommit.getName());
                Timestamp commit_time=CalTime.strToSqlDate(CalTime.checkDate(revCommit.getAuthorIdent().getWhen().toString()),"yyyy-MM-dd HH:mm:ss") ;
                latest_commit.setCommitTime(commit_time);
                latest_commit.setBranch(branch);
                latest_commit.setCommitter(revCommit.getAuthorIdent().getName());
                latest_commit.setRepository(git_path+".git");
                if(revCommit.getParentCount()!=0){
                    Timestamp parent_commit_time=CalTime.strToSqlDate(CalTime.checkDate(revCommit.getParent(0).getAuthorIdent().getWhen().toString()),"yyyy-MM-dd HH:mm:ss") ;
                    parent_commit.setCommitTime(parent_commit_time);
                    parent_commit.setCommitHash(revCommit.getParent(0).getName());
                    parent_commit.setCommitter(revCommit.getParent(0).getAuthorIdent().getName());
                }
                else {
                    parent_commit.setCommitHash(null);
                }
                git_id= c.insert(latest_commit);
                latest_commit.setId(git_id);
                break;


            }
        }catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latest_commit;
    }

}
