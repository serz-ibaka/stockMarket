package resources;

public class Candle {

	long timestamp;
	double open;
	double close;
	double high;
	double low;
	
	public long getTimestamp() {
		return timestamp;
	}
	public double getOpen() {
		return open;
	}
	public double getClose() {
		return close;
	}
	public double getHigh() {
		return high;
	}
	public double getLow() {
		return low;
	}

	public Candle(long timestamp, double open, double close, double high, double low) {
		this.timestamp = timestamp;
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
	}
	
	@Override
	public String toString() {
		return "[timestamp: " + timestamp + ", open: " + open + ", close: " + close + ", high: " + high + ", low: " + low + "]";
	}
	
	
}
