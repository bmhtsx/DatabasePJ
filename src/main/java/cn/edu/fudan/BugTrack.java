package cn.edu.fudan;

import cn.edu.fudan.data.Git_checkout;
import cn.edu.fudan.data.Git_info;
import cn.edu.fudan.service.scan;

import java.io.IOException;

public class BugTrack
{
    public static void main( String[] args ){
        try {
            scan _scan=new scan();
            _scan.scan_latest("master");

//            Git_checkout git_checkout=new Git_checkout();
//            git_checkout.getCommitMessagesInBranch("master");
            Read.read();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
