package exceptions;

public class WrongPasswordException extends Exception {

	public WrongPasswordException() {
		super("Error: Wrong password");
	}
	
}
