#ifndef _TRANSACTION_H_
#define _TRANSACTION_H_

#include "Stock.h"

class User;

class Transaction {
	
public:
	Transaction(Stock* stock, double price, time_t timestamp, int count, int id);
	
	Stock* getStock() { return stock; }
	int getCount() { return count; }
	int getId() { return id; }
	void setCount(int cnt) { count = cnt; }
	void insert(int sid, int uid, sqlite3* db);

	friend ostream& operator<<(ostream& os, Transaction& transaction);

private:
	Stock* stock;
	double price;
	time_t timestamp;
	int count;
	int id;
};

#endif