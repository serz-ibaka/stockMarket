package exceptions;

public class UserNotExistsException extends Exception {

	public UserNotExistsException() {
		super("Error: This user doesn't exist");
	}
}
