package exceptions;

public class UsernameExistsException extends Exception {

	public UsernameExistsException() {
		super("Error: Username already exists!");
	}
}
