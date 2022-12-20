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
        Date data = null;
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

}
