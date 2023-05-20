package tr.traderepublic.candlesticks.candlesticks.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tr.traderepublic.candlesticks.candlesticks.consts.ConstantConfig;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-20 21:03
 */
class DateUtilTest {

    @Test
    void getTimeChunk() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 5, 20, 21, 1, 30);
        String expectedTimeChunk = "2101";
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID));
        String computedTimeChunk = DateUtil.getTimeChunk(localDateTime.toInstant(zdt.getOffset()).toEpochMilli());
        Assertions.assertEquals(expectedTimeChunk, computedTimeChunk);
    }

    @Test
    void getRoundFloor() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 5, 20, 21, 1, 30);
        Date expectedDate = java.util.Date
                .from(LocalDateTime
                        .of(2023, 5, 20, 21, 1, 0)
                        .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID))
                        .toInstant());
        Date computedDate = DateUtil.getRoundFloor(ZonedDateTime.of(localDateTime, ZoneId.of(ConstantConfig.TIMEZONE_ID))
                .toInstant().toEpochMilli());
        Assertions.assertEquals(expectedDate, computedDate);
    }

    @Test
    void getRoundCeiling() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 5, 20, 21, 1, 30);
        Date expectedDate = java.util.Date
                .from(LocalDateTime
                        .of(2023, 5, 20, 21, 2, 0)
                        .atZone(ZoneId.of(ConstantConfig.TIMEZONE_ID))
                        .toInstant());
        Date computedDate = DateUtil.getRoundCeiling(ZonedDateTime.of(localDateTime, ZoneId.of(ConstantConfig.TIMEZONE_ID))
                .toInstant().toEpochMilli());
        Assertions.assertEquals(expectedDate, computedDate);
    }
}