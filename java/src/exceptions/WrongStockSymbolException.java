package exceptions;

public class WrongStockSymbolException extends Exception {
	public WrongStockSymbolException() {
		super("Error: Wrong stock symbol");
	}
}
