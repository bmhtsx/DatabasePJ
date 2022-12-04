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
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public int getHistoryInfo() {
        int git_id=0;
        File gitDir = new File("D:\\学习资料\\作业\\程序设计\\软件工程\\git_test\\SE_H5_Back_End\\.git");
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
                latest_commit.setCommitTime(revCommit.getAuthorIdent().getWhen().toString());
                latest_commit.setBranch("master");
                latest_commit.setCommitter(revCommit.getAuthorIdent().getName());
                latest_commit.setRepository("D:\\学习资料\\作业\\程序设计\\软件工程\\git_test\\SE_H5_Back_End\\.git");
                git_id= c.insert(latest_commit);
            }
        }catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return git_id;
    }

}
