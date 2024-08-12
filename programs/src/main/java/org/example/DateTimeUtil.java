package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static long dateToLong(String str){
        Date simpleDateFormat = null;
        try {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd").parse(str);
        }catch (Exception e){
            e.printStackTrace();
        }

        return simpleDateFormat.getTime();
    }

    public static String longToDate(long epoch){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(epoch);
        String date = simpleDateFormat.format(d);
        return date;
    }

}
