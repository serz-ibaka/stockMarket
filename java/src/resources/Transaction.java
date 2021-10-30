package resources;

import parsing.StockParser;

public class Transaction {

	private int id;
	private Stock stock;
	private User user;
	private int count;
	private double price;
	private long timestamp;
	private double currentPrice;
	
	public int getId() {
		return id;
	}
	public Stock getStock() {
		return stock;
	}
	public User getUser() {
		return user;
	}
	public int getCount() {
		return count;
	}
	public double getPrice() {
		return price;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public double getCurrentPrice() {
		return currentPrice;
	}
	
	public Transaction(int id, Stock stock, User user, int count, double price, long timestamp) {
		this.id = id;
		this.stock = stock;
		this.user = user;
		this.count = count;
		this.price = price;
		this.timestamp = timestamp;
		
		currentPrice = StockParser.getCurrentPrice(stock.getStockName());
	}
	
	@Override
	public String toString() {
		return id + ". " + count + " " + stock.getStockName() + " stocks at a price of " + price + ". Current price: " + currentPrice;
	}
	
	
}
