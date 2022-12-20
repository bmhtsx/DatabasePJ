package cn.edu.fudan;

import cn.edu.fudan.dao.InstcaseDAO;
import cn.edu.fudan.data.CalTime;
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
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void read() throws IOException {
        String status;
        Timestamp start, end;
        do {
            System.out.println("--------------------------------------------------");
            System.out.println(" * `last`: query in last commit");
            System.out.println(" * `specific`: query in specific commit");
            System.out.println(" * `time`: query in a period of time");
            System.out.println(" * `author`: query by the author");
            System.out.println(" * `analysis`: data analysis and statistics");
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
                        case "time_avg":
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
                    print10(instcaseDAO.getInstByStatusAndTime(status, start, end));
                    break;
                case "author":
                    System.out.println("your name: ");
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
                    print10(instcaseDAO.getInstByStatusAndAuthorAndTime(status1, status2, name, start, end));
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
                        case "over":
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
                case "q":
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

    static void printAvg(List<Integer> list) {
        System.out.println("average = " + list.get(0));
        System.out.println("median = " + list.get(1));
    }
}
