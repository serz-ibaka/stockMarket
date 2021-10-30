package resources;

import java.util.ArrayList;

import parsing.StockParser;

public class Stock {

	private String stockName;
	private ArrayList<Candle> candles;
	
	public Stock(String stockName) {
		this.stockName = stockName;
		
		candles = StockParser.parseString(stockName);
	}

	public String getStockName() {
		return stockName;
	}
	public ArrayList<Candle> getCandles() {
		return candles;
	}
	
	
	
	
}
