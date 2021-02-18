package notepack.app.domain.exception;

public class GuiNotReadyError extends Exception {

    private Exception e;
    private String message;

    public GuiNotReadyError(String message) {
        super(message);

        this.message = message;
    }

    public Exception getException() {
        return e;
    }
}
