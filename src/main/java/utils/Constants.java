package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Constants {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final String startSemester = "30/09/2019";//TODO : CONFIG FILE (but how? --- static final --- )
    public static final String beginHolyday = "23/12/2019";
    public static final String endHolyday = "05/01/2020";
    public static final String endSemester = "17/01/2020";
    public static StructuraSemestru sem1 = new StructuraSemestru(2,1,startSemester,beginHolyday,endHolyday,endSemester);

    public static int getWeek(LocalDateTime localDateTime){

        String input = localDateTime.format(Constants.DATE_TIME_FORMATTER);
        String format = "dd/MM/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date;
        int week;
        try {
            date = df.parse(input);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            week = cal.get(Calendar.WEEK_OF_YEAR);
        } catch (ParseException e) {
            week = -1;
        }
        return week;
    }

    public static int getWeek(String input){

        String format = "dd/MM/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date;
        int week;
        try {
            date = df.parse(input);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            week = cal.get(Calendar.WEEK_OF_YEAR);
        } catch (ParseException e) {
            week = -1;
        }
        return week;
    }

    public static boolean firstDateIsGreaterThanSecondDate(String stringDate1, String stringDate2){
        String[] date1Parts = stringDate1.split("/");
        String remakeDate1 = date1Parts[2] + "/" + date1Parts[1] + "/" + date1Parts[0];//convert from "dd/mm/yyyy" to "yyyy/mm/dd" so that we can compare strings
        String[] date2Parts = stringDate2.split("/");
        String remakeDate2 = date2Parts[2] + "/" + date2Parts[1] + "/" + date2Parts[0];//convert from "dd/mm/yyyy" to "yyyy/mm/dd" so that we can compare strings

        return remakeDate1.compareTo(remakeDate2) >= 0;
    }

}
