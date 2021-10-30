package indicators;

import resources.Stock;

public interface Indicator {
	double average(int days, Stock stock);
}
