package parsing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import resources.Candle;

public class StockParser {

	public native String getStock(String stockName);
	
	static {
		System.loadLibrary("libcurl-x64");
		System.loadLibrary("StockParser");
	}
	
	
	public static void main(String[] args) {
		// StockParser sp = new StockParser();
		// String buffer = sp.getStock("tsla");

		System.out.println(getCurrentPrice("tsla"));
	}
	
	public static ArrayList<Candle> parseString(String stockName) {
		StockParser sp = new StockParser();
		String buffer = sp.getStock(stockName);
		
		ArrayList<Candle> candles = new ArrayList<>();
		
		Pattern pattern;
		Matcher matcher;
		String regex = "([0-9\\.]+),";
		
		
		String timestamp = "\"timestamp\":\\[([^\\]]+)\\]";
		pattern = Pattern.compile(timestamp);
		matcher = pattern.matcher(buffer);
		matcher.find();
		String timestamps = matcher.group(1) + ",";
		ArrayList<Long> timestampList = new ArrayList<>();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(timestamps);
		while(matcher.find()) {
			timestampList.add(Long.parseLong(matcher.group(1)));
		}
		
		String open = "\"open\":\\[([^\\]]+)\\]";
		pattern = Pattern.compile(open);
		matcher = pattern.matcher(buffer);
		matcher.find();
		String opens = matcher.group(1) + ",";
		ArrayList<Double> openList = new ArrayList<>();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(opens);
		while(matcher.find()) {
			openList.add(Double.parseDouble(matcher.group(1)));
		}
		
		String close = "\"close\":\\[([^\\]]+)\\]";
		pattern = Pattern.compile(close);
		matcher = pattern.matcher(buffer);
		matcher.find();
		String closes = matcher.group(1) + ",";
		ArrayList<Double> closeList = new ArrayList<>();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(closes);
		while(matcher.find()) {
			closeList.add(Double.parseDouble(matcher.group(1)));
		}
		
		String high = "\"high\":\\[([^\\]]+)\\]";
		pattern = Pattern.compile(high);
		matcher = pattern.matcher(buffer);
		matcher.find();
		String highs = matcher.group(1) + ",";
		ArrayList<Double> highList = new ArrayList<>();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(highs);
		while(matcher.find()) {
			highList.add(Double.parseDouble(matcher.group(1)));
		}
		
		String low = "\"low\":\\[([^\\]]+)\\]";
		pattern = Pattern.compile(low);
		matcher = pattern.matcher(buffer);
		matcher.find();
		String lows = matcher.group(1) + ",";
		ArrayList<Double> lowList = new ArrayList<>();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(lows);
		while(matcher.find()) {
			lowList.add(Double.parseDouble(matcher.group(1)));
		}
		
		for(int i = 0; i < timestampList.size(); i++) {
			candles.add(new Candle(timestampList.get(i), openList.get(i), closeList.get(i), highList.get(i), lowList.get(i)));
		}
				
		return candles;
	}

	public static double getCurrentPrice(String stockName) {
		StockParser sp = new StockParser();
		String buffer = sp.getStock(stockName);
		String regex = "([0-9\\.]+),";
		String close = "\"close\":\\[([^\\]]+)\\]";
		Pattern pattern = Pattern.compile(close);
		Matcher matcher = pattern.matcher(buffer);
		matcher.find();
		String closes = matcher.group(1) + ",";
		ArrayList<Double> closeList = new ArrayList<>();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(closes);
		while(matcher.find()) {
			closeList.add(Double.parseDouble(matcher.group(1)));
		}
		
		double price = closeList.get(closeList.size() - 1);
		return price;
	}
	
}
