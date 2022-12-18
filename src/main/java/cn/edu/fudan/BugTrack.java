package cn.edu.fudan;

import cn.edu.fudan.data.Git_info;
import cn.edu.fudan.service.scan;

public class BugTrack
{
    //远程库路径
    public static String localPath = "/Users/bigman/software/local";            //下载已有仓库到本地路径
    public static String branchName = "master";   //分支名
    public static String projectName = "FirstGithub";  //项目名

    public static void main( String[] args )
    {
        scan _scan=new scan();
        _scan.scan_latest();
        Git_info info=new Git_info();
        info.getHistoryInfo(true);



//        String filePath = localPath+"/"+projectName;
//        // 初始化adapter
//        GitAdapter gitAdapter = new GitAdapter(TestMain.remotePath,filePath,TestMain.branchName);
//        List<CommitMessage> commitMessages = gitAdapter.getCommitMessages();
//        int i=1;
//        System.out.println("=========================================================================================");
//        for(CommitMessage commitMessage : commitMessages) {
//            System.out.println(i+" "+commitMessage.toString());
//            i++;
//        }
    }
}
