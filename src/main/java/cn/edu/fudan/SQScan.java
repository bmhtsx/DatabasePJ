package cn.edu.fudan;

public class SQScan {

    public static void scanProject() {
        CmdExecute.exeCmd("D:\\sonar-scanner-4.7.0.2747-windows\\bin\\sonar-scanner.bat -D sonar.projectKey=cim", "d:\\Desktop\\SoftwareEngineering2022");
    }
}
