package notepack.app.domain.exception;

public class InternalError extends Exception {
    
    private Exception e;
    private String message;
    
    public InternalError(String message, Exception e) {
        super(message);
        
        this.message = message;
        this.e = e;
    }
    
    public Exception getException() {
        return e;
    }
}
