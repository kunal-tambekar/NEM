package zx.zample.nem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kunal on 4/16/17.
 */

public class DateUtil {

    public static long getDate(String date, String currentFormat){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(currentFormat);
        Date d;
        long millis =0;
        try{

            d = simpleDateFormat.parse(date);
            millis = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public static String convertDate(String dateToConvert, String currentFormat, String newFormat){

        String newDateStr ="";

        SimpleDateFormat format = new SimpleDateFormat(currentFormat);
        Date d = null;
        try{
            d = format.parse(dateToConvert);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(d!=null){
            format.applyPattern(newFormat);
            newDateStr = format.format(d);
        }

        return newDateStr;

    }

    public static Date convertToDate(String date, String format) throws ParseException{

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.parse(date);

    }

}
