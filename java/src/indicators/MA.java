package indicators;

import resources.Stock;

public class MA implements Indicator {

	@Override
	public double average(int days, Stock stock) {
		days = days > stock.getCandles().size() ? stock.getCandles().size() : days;
		double ma = 0;
		for(int i = stock.getCandles().size() - 1; i >= stock.getCandles().size() - days; i--) {
			ma += stock.getCandles().get(i).getClose();		
		}
		return ma / days;
	}

}
