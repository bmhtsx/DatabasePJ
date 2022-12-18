package cn.edu.fudan;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class SQScan {

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

    public static void scanProject() {
        String cmd = "cmd /c cd d:\\Desktop\\SoftwareEngineering2022 & dir";
        exeCmd("D:\\sonar-scanner-4.7.0.2747-windows\\bin\\sonar-scanner.bat -D sonar.projectKey=cim", "d:\\Desktop\\SoftwareEngineering2022");
    }
}
