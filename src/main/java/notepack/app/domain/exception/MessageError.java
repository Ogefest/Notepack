package notepack.app.domain.exception;

public class MessageError extends InternalError implements UserErrorMessage {

    public MessageError(String message, Exception e) {
        super(message, e);
    }

}
