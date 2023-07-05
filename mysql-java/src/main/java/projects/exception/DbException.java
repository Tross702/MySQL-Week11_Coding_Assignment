package projects.exception;

@SuppressWarnings("serial")
public class DbException extends RuntimeException {

	//Extend RuntimeException. Override the following constructors from the superclass:
	public DbException () {	
	}
	
    public DbException(String message) {
        super(message);
    }

    public DbException(Throwable cause) {
        super(cause);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}