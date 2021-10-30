package indicators;

import resources.Stock;

public class EMA implements Indicator {

	@Override
	public double average(int days, Stock stock) {
		days = days > stock.getCandles().size() ? stock.getCandles().size() : days;
		double ema = 0;
		for(int i = stock.getCandles().size() - 1; i >= stock.getCandles().size() - days; i--) {
			if(ema == 0) ema = stock.getCandles().get(i).getClose();
			else ema = ema * (1 - 2/(days + 1.0)) + 2 / (days + 1.0) * stock.getCandles().get(i).getClose();
		}
		return ema;
	}

}
