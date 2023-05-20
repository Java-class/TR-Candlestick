package tr.traderepublic.candlesticks.candlesticks.exceptions;

public class PartnerEventReceiveException extends RuntimeException {

    public PartnerEventReceiveException(String message, String receiver, String error) {
        super(String.format("The %s websocket failed to convert and send message: %s with error: %s",
                message, receiver, error));
    }
}
