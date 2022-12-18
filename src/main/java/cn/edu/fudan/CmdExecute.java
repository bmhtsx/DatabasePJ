package cn.edu.fudan;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class CmdExecute {
    static void exeCmd(String commandStr, String filepath) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr, null, new File(filepath));
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println(sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
