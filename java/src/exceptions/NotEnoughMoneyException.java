package exceptions;

public class NotEnoughMoneyException extends Exception {
	public NotEnoughMoneyException() {
		super("Error: Not enough money on balance");
	}
}
