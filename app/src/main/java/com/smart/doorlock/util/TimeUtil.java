package com.smart.doorlock.util;

import android.content.Context;
import com.smart.doorlock.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT_NO_SECOND = "yyyy-MM-dd HH:mm";
    public static final String DAY_FORMAT = "MM-dd";
    public static final String DAY_TIME_FORMAT = "HH:mm";
    public static final String DATE_FORMAT_TODAY = "今天HH:mm";
    public static final String DATE_FORMAT_PASS_TADAY = "MM月dd日HH:mm";
    public static final String DOUBLE_FORMAT_TWO_POINT = "#0.00";

    public static String convertLongToString(long date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(new Date(date));
    }

    public static long convertStringToLong(String date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        try {
            return sf.parse(date).getTime();
        } catch (Exception exception) {
            return 0;
        }
    }
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);
        Date curDate = new Date(System.currentTimeMillis());
        return dateFormat.format(curDate);
    }

    public static long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String convertDateToString(long date) {
        long ldate = date;
        String weekdays;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(ldate));
        String years = String.valueOf(calendar.get(Calendar.YEAR));
        String mouth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(calendar.DAY_OF_MONTH));
        int weekday = Integer.valueOf(calendar.get(calendar.DAY_OF_WEEK));

        if (Calendar.MONDAY == weekday) {
            weekdays = "周一";
        } else if (Calendar.TUESDAY == weekday) {
            weekdays = "周二";
        } else if (Calendar.WEDNESDAY == weekday) {
            weekdays = "周三";
        } else if (Calendar.THURSDAY == weekday) {
            weekdays = "周四";
        } else if (Calendar.FRIDAY == weekday) {
            weekdays = "周五";
        } else if (Calendar.SATURDAY == weekday) {
            weekdays = "周六";
        } else if (Calendar.SUNDAY == weekday) {
            weekdays = "周日";
        } else {
            weekdays = "";
        }
        int hour = calendar.get(calendar.HOUR_OF_DAY);
        int minute = calendar.get(calendar.MINUTE);

        return years + "-" + mouth + "-" + day + " " + weekdays + " " + FormatZero(hour) + ":" + FormatZero(minute);
    }

    public static String convertDateToStringWithoutWeek(long date) {
        long ldate = date * 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(ldate));
        String years = String.valueOf(calendar.get(Calendar.YEAR));
        String mouth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(calendar.DAY_OF_MONTH));

        int hour = calendar.get(calendar.HOUR_OF_DAY);
        int minute = calendar.get(calendar.MINUTE);

        return years + "-" + mouth + "-" + day + " " + FormatZero(hour) + ":" + FormatZero(minute);
    }

    public static String FormatZero(int a) {
        if (a == 0)
            return "00";
        if (a < 10)
            return "0" + a;
        return a + "";
    }

    public static String convertDate(long dates) {
        long ldate = dates * 1000;
        SimpleDateFormat sf = new SimpleDateFormat(TIME_FORMAT);
        return sf.format(new Date(ldate));
    }

    public static String convertDateDay(long dates) {
        long ldate = dates * 1000;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        return sf.format(new Date(ldate));
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd");
        }
    };

    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toDateString(String longDate) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(Long.valueOf(longDate) * 1000);
        result = format.format(new Date(cl.getTimeInMillis()));
        return result;
    }

    public static String friendlyTime(Context ctx, long sdate) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);
        Date currDate = new Date(sdate);

        String str = formatter.format(currDate);
        Date time = toDate(str);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                long minutes = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1);
                if (minutes > 1) {
                    ftime = minutes + ctx.getString(R.string.minuites_ago);
                } else {
                    ftime = ctx.getString(R.string.just_now);
                }
            } else {
                ftime = hour + ctx.getString(R.string.hours_ago);
            }
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                long minutes = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1);
                if (minutes > 1) {
                    ftime = minutes + ctx.getString(R.string.minuites_ago);
                } else {
                    ftime = ctx.getString(R.string.just_now);
                }
            } else {
                ftime = hour + ctx.getString(R.string.hours_ago);
            }
        } else if (days == 1) {
            ftime = ctx.getString(R.string.yestoday);
        } else if (days == 2) {
            ftime = ctx.getString(R.string.the_day_before_yestoday);
        } else if (days > 2) {
            ftime = convertDateToString(sdate);
        }
        return ftime;
    }

	public static byte[] getDateTimeStr() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = sDateFormat.format(new java.util.Date());
		return date.getBytes();
	}

    public static String formatBcdTime(byte[] time_byte,int pos){
        String time_str = String.format("20%02X%02X%02X%02X%02X%02X", time_byte[pos],time_byte[pos+1],time_byte[pos+2],time_byte[pos+3],time_byte[pos+4],time_byte[pos+5]);
        return time_str;
    }

    public static String getLogDisplayTime(Context ctx,String time_str){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date sDate = null;
        try {
            sDate = sDateFormat.parse(time_str);
        } catch (ParseException e) {
            return "";
        }
        return friendlyTime(ctx,sDate.getTime());
    }
}
