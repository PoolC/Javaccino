package com.emotie.api.common.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeService {

    public static final int SECOND = 60;
    public static final int MINUTE = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;


    public static String calculateTime(LocalDateTime registered){
        long currentTime = System.currentTimeMillis();
        long registerTime = Date.from(registered.atZone(ZoneId.systemDefault()).toInstant()).getTime();

        long difference = (currentTime - registerTime) / 1000;
        String result;

        if (difference < SECOND) {
            // sec
            result = difference + "초 전";
        } else if ((difference /= SECOND) < MINUTE) {
            // min
            result = difference + "분 전";
        } else if ((difference /= MINUTE) < HOUR) {
            // hour
            result = difference + "시간 전";
        } else if ((difference /= HOUR) < DAY) {
            // day
            result = difference + "일 전";
        } else if ((difference /= DAY) < MONTH) {
            // day
            result = difference + "달 전";
        } else {
            result = difference + "년 전";
        }

        return result;
    }

}
