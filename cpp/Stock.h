#ifndef _STOCK_H_
#define _STOCK_H_

#define CURL_STATICLIB

#include <iomanip>
#include <iostream>
#include <map>
#include <string>
#include <vector>

#include "Candle.h"
#include "curl/include/curl/curl.h"
#include "sqlite/sqlite3.h"

using namespace std;

class Stock {

public:
	Stock(string symbol, time_t start, time_t end, sqlite3* db);
	int getId(sqlite3* db);
	friend ostream& operator<<(ostream& os, Stock& stock);
	double MA(int n);
	double EMA(int n);
	double getCurrentPrice();
	string getSymbol() { return symbol; }
	bool exists();

	static void showStock(sqlite3* db);

private:
	string symbol;
	time_t start;
	time_t end;
	vector<Candle> candles;
};

#endif