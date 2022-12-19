package cn.edu.fudan;

import cn.edu.fudan.dao.InstcaseDAO;
import cn.edu.fudan.entity.InstCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Read {

    static InstcaseDAO instcaseDAO = new InstcaseDAO();
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void read() throws IOException {
        String status, start, end;
        do {
            System.out.println("you can use `last`, `specific`, `time`, `author`, `analysis`");
            switch (br.readLine()) {
                case "last":
                    System.out.println("you can use `type` `time` `time_avg`");
                    switch (br.readLine()) {
                        case "type":
                            System.out.println("choose the type(`CODE_SMELL`, `BUG`)");
                            print10(instcaseDAO.getInstByTypeInLatestCommit(br.readLine()));
                            break;
                        case "time":
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
                    System.out.println("you can use `new`, `fixed`");
                    status = br.readLine();
                    print10(instcaseDAO.getCommitByTimeDesc());
                    System.out.print("commit_hash: ");
                    String commitHash = br.readLine();
                    System.out.print("type(`CODE_SMELL`, `BUG`): ");
                    String type = br.readLine();
                    print10(instcaseDAO.getInstByStatusAndCommitAndType(status, commitHash, type));
                case "time":
                    System.out.println("you can use `new`, `fixed`");
                    status = br.readLine();
                    System.out.println("time format: `2000-01-01T`");
                    System.out.print("start time: ");
                    start = br.readLine();
                    System.out.print("end time: ");
                    end = br.readLine();
                    print10(instcaseDAO.getInstByStatusAndTime(status, start, end));
                case "author":
                    System.out.println("your name: ");
                    String name = br.readLine();
                    System.out.println("you can use `new`, `fixed`");
                    String status1 = br.readLine();
                    System.out.println("`self` or `others`");
                    String status2 = br.readLine();
                    System.out.print("start time: ");
                    start = br.readLine();
                    System.out.print("end time: ");
                    end = br.readLine();
                    print10(instcaseDAO.getInstByStatusAndAuthorAndTime(status1, status2, name, start, end));
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
