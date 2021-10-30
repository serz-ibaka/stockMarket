#include "Transaction.h"

Transaction::Transaction(Stock* stock, double price, time_t timestamp, int count, int id)
	: stock(stock), price(price), timestamp(timestamp), count(count), id(id) {}

void Transaction::insert(int sid, int uid, sqlite3* db) {
	string query = "INSERT INTO transactions(user_id,stock_id,price,count,timestamp) VALUES(" + to_string(uid) + "," + to_string(sid)
		+ "," + to_string(price) + "," + to_string(count) + ",'" + to_string(time(nullptr)) + "');";
	sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr);
	id = sqlite3_last_insert_rowid(db);
}

ostream& operator<<(ostream& os, Transaction& transaction) {
	double currentPrice = transaction.stock->getCurrentPrice();
	string color = (currentPrice > transaction.price) ? _GREEN : _RED;
	double difference = abs(currentPrice - transaction.price);
	string profit = (color == _GREEN) ? "+" : "-";
	profit += to_string(difference);

	cout << setw(4) << transaction.id << setw(8) << transaction.stock->getSymbol() << setw(15) << transaction.timestamp 
		<< setw(6) << transaction.count << setw(15) << setprecision(6) << transaction.price << setw(15) << 
		setprecision(6) << currentPrice << color << setw(12) << profit << _WHITE;

	return os;
}
