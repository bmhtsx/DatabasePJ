package cn.edu.fudan.data;

import org.apache.maven.shared.utils.StringUtils;

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

    public static int calDurationTime(String begin, String end) {
        LocalDateTime be = LocalDateTime.parse(begin.substring(0, 19));
        LocalDateTime en = LocalDateTime.parse(end.substring(0, 19));
        Duration duration = Duration.between(be, en);
        return (int) duration.getSeconds();
    }

}
