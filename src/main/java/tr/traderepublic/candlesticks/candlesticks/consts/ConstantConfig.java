package tr.traderepublic.candlesticks.candlesticks.consts;

/**
 * Here is Constant Config class for Business Config
 *
 * @author Mostafa Anbarmoo
 * @project CandleSticks
 * @created 2023-05-19 10:23
 */

public class ConstantConfig {
    /**
     * The instrument endpoint canonical path
     */
    public final static String INSTRUMENTS_ENDPOINT = "/instruments";

    /**
     * The quote endpoint canonical path
     */
    public final static String QUOTES_ENDPOINT = "/quotes";

    /**
     * Index of first quote received in quote history list based on specific timeChunk
     */
    public final static Integer FIRST_QUOTE_RECEIVED_INDEX = 0;

    /**
     * Default System timezone
     */
    public final static String TIMEZONE_ID = "Asia/Tehran";
}
