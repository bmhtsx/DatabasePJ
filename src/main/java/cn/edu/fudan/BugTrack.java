package cn.edu.fudan;


import cn.edu.fudan.data.issue_info;
import cn.edu.fudan.service.scan;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;

public class BugTrack
{


    public static void main( String[] args )throws IOException
    {
//        String s=null;
//        s= issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
//        issue_info.toMap(s,0);
        scan _scan=new scan();
        _scan.scan_latest();
    }
}
