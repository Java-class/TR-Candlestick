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

    public static Date getRoundFloor(long timeMilliSeconds) {
        LocalDateTime ldt = Instant.ofEpochMilli(timeMilliSeconds)
                .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID)).toLocalDateTime();
        LocalDateTime roundFloor = ldt.truncatedTo(ChronoUnit.MINUTES);
        return java.util.Date
                .from(roundFloor.atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID))
                        .toInstant());
    }

    public static Date getRoundCeiling(long timeMilliSeconds) {
        LocalDateTime ldt = Instant.ofEpochMilli(timeMilliSeconds)
                .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID)).toLocalDateTime();
        LocalDateTime roundCeiling = ldt.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        return java.util.Date
                .from(roundCeiling.atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID))
                        .toInstant());
    }
}
