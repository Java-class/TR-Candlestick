package tr.traderepublic.candlesticks.candlesticks.util;

import tr.traderepublic.candlesticks.candlesticks.consts.ConstantConfig;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Date Utility class for Generate necessary date format
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-18 13:38
 */

public class DateUtil {

    /**
     * The geTimeChunk method for generate time chunk
     * This method return hour and minute of input time chunk
     * if input data represent the timestamp for 21:10:02 it will return 2102
     *
     * @param timeMilliSeconds the time in milliseconds for convert to chunk time
     * @return String time chunk
     */
    public static String getTimeChunk(long timeMilliSeconds) {
        LocalDateTime ldt = Instant.ofEpochMilli(timeMilliSeconds)
                .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID)).toLocalDateTime();
        String hour = String.valueOf(ldt.getHour());
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String minute = String.valueOf(ldt.getMinute());
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return hour + minute;
    }

    /**
     * The getRoundFloor method for generate round floor of input date time
     * if input data represent the timestamp for 2023-05-21 21:10:02 it will return 2023-05-21 21:10:00
     *
     * @param timeMilliSeconds the time in milliseconds for convert to time floor
     * @return Date
     */
    public static Date getRoundFloor(long timeMilliSeconds) {
        LocalDateTime ldt = Instant.ofEpochMilli(timeMilliSeconds)
                .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID)).toLocalDateTime();
        LocalDateTime roundFloor = ldt.truncatedTo(ChronoUnit.MINUTES);
        return java.util.Date
                .from(roundFloor.atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID))
                        .toInstant());
    }

    /**
     * The getRoundCeiling method for generate round ceiling of input date time
     * if input data represent the timestamp for 2023-05-21 21:10:02 it will return 2023-05-21 21:11:00
     *
     * @param timeMilliSeconds the time in milliseconds for convert to time ceiling
     * @return Date
     */
    public static Date getRoundCeiling(long timeMilliSeconds) {
        LocalDateTime ldt = Instant.ofEpochMilli(timeMilliSeconds)
                .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID)).toLocalDateTime();
        LocalDateTime roundCeiling = ldt.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        return java.util.Date
                .from(roundCeiling.atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID))
                        .toInstant());
    }
}
