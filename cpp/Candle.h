#ifndef _CANDLE_H_
#define _CANDLE_H_

#include <ctime>
#include <iomanip>
#include <iostream>
#include <string>

#include "Colors.h"
#include "sqlite/sqlite3.h"

using namespace std;

class Stock;

class Candle {
	friend Stock;

public:
	Candle(Stock* stock, double open, double low, double high, double close, time_t start);
	friend ostream& operator<<(ostream& os, Candle& candle);
	bool inDatabase(int id, sqlite3* db);

private:
	Stock* stock;
	double open, close;
	double low, high;
	time_t start;
};

#endif