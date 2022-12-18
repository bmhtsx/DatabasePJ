package cn.edu.fudan.data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalTime {

    public static int calDurationTime(String begin, String end) {
        LocalDateTime be = LocalDateTime.parse(begin.substring(0, 19));
        LocalDateTime en = LocalDateTime.parse(end.substring(0, 19));
        Duration duration = Duration.between(be, en);
        return (int) duration.getSeconds();
    }

}
