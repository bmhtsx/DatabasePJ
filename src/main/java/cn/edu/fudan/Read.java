package cn.edu.fudan;

import cn.edu.fudan.dao.InstanceDAO;
import cn.edu.fudan.dao.InstcaseDAO;
import cn.edu.fudan.data.CalTime;
import cn.edu.fudan.data.Git_checkout;
import cn.edu.fudan.entity.InstCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Read {

    static InstcaseDAO instcaseDAO = new InstcaseDAO();
    static InstanceDAO instanceDAO = new InstanceDAO();
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void read() throws IOException {
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
                            break;
                        default:
                            break;
                    }
                    break;
                case "search":
                    readSearch();
                    break;
                case "quit":
                    quit=true;
                    break;
                default:
                    break;
            }
        } while (!quit);

    }

    public static void readSearch() throws IOException {
        String status;
        Timestamp start, end;
        do {
            System.out.println("--------------------------------------------------");
            System.out.println(" * `last`: query in last commit");
            System.out.println(" * `specific`: query in specific commit");
            System.out.println(" * `time`: query in a period of time");
            System.out.println(" * `author`: query by the author");
            System.out.println(" * `analysis`: data analysis and statistics");
            System.out.println(" * `trace`: trace the instance from the last commit to first");
            System.out.println(" * tips: you can get instance id by query methods, then `trace`");
            System.out.println(" * `quit`: quit the program");
            System.out.println("--------------------------------------------------");
            switch (br.readLine()) {
                case "last":
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ** `type`: query by type");
                    System.out.println(" ** `time`: query by duration time");
                    System.out.println(" ** `avg` : average and median of during time");
                    System.out.println("--------------------------------------------------");
                    switch (br.readLine()) {
                        case "type":
                            System.out.println("choose the type (`CODE_SMELL`, `BUG`)");
                            print10(instcaseDAO.getInstByTypeInLatestCommit(br.readLine()));
                            break;
                        case "time":
                            System.out.println("choose `greater` or `less`");
                            if (Objects.equals(br.readLine(), "greater")) {
                                print10(instcaseDAO.getSortedInstByTimeInLatestCommit(true));
                            } else {
                                print10(instcaseDAO.getSortedInstByTimeInLatestCommit(false));
                            }
                            break;
                        case "avg":
                            System.out.println("choose the type (`CODE_SMELL`, `BUG`)");
                            printAvg(instcaseDAO.getAvgAndMedOfTimeInLatestCommit(br.readLine()));
                            break;
                        default:
                            break;
                    }
                    break;
                case "specific":
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ** `new`: query the creation of issue");
                    System.out.println(" ** `fixed`: query the fixed issue");
                    System.out.println("--------------------------------------------------");
                    status = br.readLine();
                    System.out.println("choose the commit:");
                    print10(instcaseDAO.getCommitByTimeDesc());
                    System.out.print("commit_id: ");
                    int commitId = Integer.parseInt(br.readLine());
                    System.out.print("choose the type (`CODE_SMELL`, `BUG`): ");
                    String type = br.readLine();
                    print10(instcaseDAO.getInstByStatusAndCommitAndType(status, commitId, type));
                    break;
                case "time":
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ** `new`: query the creation of issue");
                    System.out.println(" ** `fixed`: query the fixed issue");
                    System.out.println("--------------------------------------------------");
                    status = br.readLine();
                    System.out.println("time format: `yyyy-MM-dd HH:mm:ss`");
                    System.out.print("start time: ");
                    start = Timestamp.valueOf(br.readLine());
                    System.out.print("end time: ");
                    end = Timestamp.valueOf(br.readLine());
                    print10(instcaseDAO.getInstByStatusAndTimePeriod(status, start, end));
                    break;
                case "author":
                    System.out.print("your name: ");
                    String name = br.readLine();
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ** `new`: query the creation of issue");
                    System.out.println(" ** `fixed`: query the fixed issue");
                    System.out.println("--------------------------------------------------");
                    String status1 = br.readLine();
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ** `self`: query the issue created by self");
                    System.out.println(" ** `others`: query the issue created by others");
                    System.out.println("--------------------------------------------------");
                    String status2 = br.readLine();
                    System.out.println("time format: `yyyy-MM-dd HH:mm:ss`");
                    System.out.print("start time: ");
                    start = Timestamp.valueOf(br.readLine());
                    System.out.print("end time: ");
                    end = Timestamp.valueOf(br.readLine());
                    print10(instcaseDAO.getInstByStatusAndAuthorAndTimePeriod(status1, status2, name, start, end));
                    break;
                case "analysis":
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ** `time`: analysis by a period of time");
                    System.out.println(" ** `longer`: analysis by duration time longer than");
                    System.out.println(" ** `author`: analysis by the author");
                    System.out.println("--------------------------------------------------");
                    switch (br.readLine()) {
                        case "time":
                            System.out.println("time format: `yyyy-MM-dd HH:mm:ss`");
                            System.out.print("start time: ");
                            start = Timestamp.valueOf(br.readLine());
                            System.out.print("end time: ");
                            end = Timestamp.valueOf(br.readLine());
                            instcaseDAO.getStatisticsByTime(start, end);
                            break;
                        case "longer":
                            System.out.println("time format: `1y` or `45d` or `15m`...");
                            System.out.print("duration time longer than: ");
                            instcaseDAO.getStatisticsByTimeLongerThan(CalTime.strToTime(br.readLine()));
                            break;
                        case "author":
                            System.out.print("your name: ");
                            instcaseDAO.getStatisticsByAuthor(br.readLine());
                            break;
                    }
                    break;
                case "trace":
                    System.out.print("instance id: ");
                    print10track(instanceDAO.getTraceInstance(Integer.parseInt(br.readLine())));
                    break;
                case "quit":
                    return;
                default:
                    break;
            }
        } while (true);
    }

    static void print10(List<?> list) throws IOException {
        System.out.println("There are "+list.size()+" records in total");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
            if ((i+1) % 10 == 0) {
                System.out.println("use `enter` to show more, or `q` to quit");
                if (Objects.equals(br.readLine(), "q"))
                    return;
            }
        }
    }

    static void print10track(List<?> list) throws IOException {
        System.out.println("There are "+list.size()+" records in total");
        for (int i = 0; i < list.size(); i++) {
            System.out.println("--"+((i==0)?'-':'>')+' '+list.get(i).toString());
            if (i != list.size()-1) System.out.println("|");
            if ((i+1) % 10 == 0) {
                System.out.println("use `enter` to show more, or `q` to quit");
                if (Objects.equals(br.readLine(), "q"))
                    return;
            }
        }
    }

    static void printAvg(List<Integer> list) {
        System.out.println("average = " + CalTime.calTime(list.get(0)));
        //System.out.println("median = " + list.get(1));
    }
}
