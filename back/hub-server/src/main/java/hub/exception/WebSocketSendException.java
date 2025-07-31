package hub.exception;

public class WebSocketSendException extends RuntimeException {

    public WebSocketSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
