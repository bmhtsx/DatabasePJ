package cn.edu.fudan.data;

import org.apache.maven.shared.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class CalTime {
    public static String checkDate(String dateStr){
        if(StringUtils.isBlank(dateStr)){
            return "";
        }
        if(!dateStr.contains("CST")){
            return "";
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date data;
        try {
            data = sdf2.parse(dateStr);
            String formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data);
            System.out.println(formatDate);
            return formatDate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

/**
 2  * 将String字符串转换为java.sql.Timestamp格式日期,用于数据库保存
 3  * @param strDate
 4  *            表示日期的字符串
 5  * @param dateFormat
 6  *            传入字符串的日期表示格式（如："yyyy-MM-dd HH:mm:ss"）
 7  * @return java.sql.Timestamp类型日期对象（如果转换失败则返回null）
 8  */
    public static java.sql.Timestamp strToSqlDate(String strDate, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        java.util.Date date = null;
        try {
            date = sf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.sql.Timestamp dateSQL = new java.sql.Timestamp(date.getTime());
        return dateSQL;
    }

    public static int calDurationTime(String begin, String end) {
        LocalDateTime be = LocalDateTime.parse(begin.substring(0, 19));
        LocalDateTime en = LocalDateTime.parse(end.substring(0, 19));
        Duration duration = Duration.between(be, en);
        return (int) duration.getSeconds();
    }

    static int year = 31536000;
    static int day = 86400;
    static int hour = 3600;
    static int minute = 60;

    public static String calTime(int second) {
        String ret = "";

        if (second >= year) {
            ret = second / year + "y";
            second %= year;
        }
        if (second >= day) {
            ret += second / day + "d";
            second %= day;
        }
        if (second >= hour) {
            ret += second / hour + "h";
            second %= hour;
        }
        if (second >= minute) {
            ret += second / minute + "m";
            second %= minute;
        }
        if (second > 0) {
            ret += second + "s";
        }
        return ret;
    }

    public static int strToTime(String str) {
        int t = Integer.parseInt(str.substring(0, str.length()-1));
        switch (str.substring(str.length()-1)) {
            case "y" : return t * year;
            case "d" : return t * day;
            case "h" : return t * hour;
            case "m" : return t * minute;
            case "s" : return t;
        }
        return 0;
    }

}
