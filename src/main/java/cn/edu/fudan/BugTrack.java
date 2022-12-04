package cn.edu.fudan;


import cn.edu.fudan.data.issue_info;

public class BugTrack
{
    public static void main( String[] args )
    {
        String s=null;
        s= issue_info.httpGet("http://localhost:9000/api/issues/search?componentKeys=cim&additionalFields=_all&s=FILE_LINE&resolved=false");
        issue_info.toMap(s,0);
    }
}
