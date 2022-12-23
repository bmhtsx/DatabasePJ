package cn.edu.fudan;

import cn.edu.fudan.data.Git_checkout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BugTrack
{
    public static void main( String[] args ){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            boolean quit=false;
            do {
                System.out.println("--------------------------------------------------");
                System.out.println(" * `scan`: scan local project");
                System.out.println(" * 'search': query in database");
                System.out.println(" * `quit`: quit the program");
                System.out.println("--------------------------------------------------");
                switch (br.readLine()) {
                    case "scan":
                        System.out.println("--------------------------------------------------");
                        System.out.println(" ** `latest`: scan local project seen as latest commit");
                        System.out.println(" ** `all`: scan all commit in certain branch");
                        System.out.println("--------------------------------------------------");
                        switch (br.readLine()) {
                            case "latest":
                                System.out.println("Please enter the name of the branch:");
                                Scan _scan=new Scan();
                                _scan.scan_latest(br.readLine());
                                break;
                            case "all":
                                System.out.println("Please enter the name of the branch:");
                                Git_checkout git_checkout=new Git_checkout();
                                git_checkout.getCommitMessagesInBranch(br.readLine());
                            default:
                                break;
                        }
                        break;
                    case "search":
                        Read.read();
                    case "quit":
                        quit=true;
                        break;
                    default:
                        break;
                }
            } while (!quit);

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
