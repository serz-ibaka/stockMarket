#include "Stock.h"

static size_t WriteCallback(void* contents, size_t size, size_t nmemb, void* userp) {
	((std::string*)userp)->append((char*)contents, size * nmemb);
	return size * nmemb;
}

Stock::Stock(string symbol, time_t start, time_t end, sqlite3* db)
	: symbol(symbol), start(start), end(end) {
	int id = getId(db);
	start = start / 3600 * 3600;
	end = (end / 3600 + 1) * 3600;
	string url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?period1=" + std::to_string(start)
		+ "&period2=" + std::to_string(end) + "&interval=1h";
	CURL* curl;
	CURLcode res;
	std::string readBuffer;
	curl = curl_easy_init();
	if (curl) {
		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);
		res = curl_easy_perform(curl);
		curl_easy_cleanup(curl);
	}
	map<string, vector<double>> values;
	vector<time_t> timestamps;
	string key;
	for (auto it = readBuffer.begin(); it != readBuffer.end(); it++) {
		int i = it - readBuffer.begin();
		if ((key = readBuffer.substr(i, 4)) == "open" || (key = readBuffer.substr(i, 5)) == "close" ||
			(key = readBuffer.substr(i, 4)) == "high" || (key = readBuffer.substr(i, 3)) == "low" ||
			(key = readBuffer.substr(i, 9)) == "timestamp") {

			string value = "";
			for (std::string::iterator jt = it + 3 + key.length(); *jt != ']'; jt++) {
				if (*jt == ',') {
					if (key == "timestamp") timestamps.push_back(atol(value.c_str()));
					else values[key].push_back(atof(value.c_str()));
					value = ""; 
					continue;
				}
				value += *jt;
			}
		}
	}

	for (auto it = timestamps.begin(); it != timestamps.end(); it++) {
		int i = it - timestamps.begin();

		Candle candle(this, values["open"][i], values["low"][i], values["high"][i], values["close"][i], *it);
		if (!candle.inDatabase(id, db)) {
			string query = "INSERT INTO candles (stock_id, beginning_time, ending_time, open, close, low, high) VALUES(" +
				std::to_string(id) + ",'" + to_string(candle.start) + "','" + to_string(candle.start + 3600) + "'," +
				to_string(candle.open) + "," + to_string(candle.close) + "," + to_string(candle.low) + "," + to_string(candle.high) + ");";
			sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr);
		}
		candles.push_back(candle);
	}
}

int Stock::getId(sqlite3* db) {
	sqlite3_stmt* stmt;
	string query = "SELECT stock_id FROM stocks WHERE stock_symbol='" + symbol + "';";
	sqlite3_prepare(db, query.c_str(), -1, &stmt, nullptr);
	if (sqlite3_step(stmt) != SQLITE_ROW) {
		query = "INSERT INTO stocks(stock_symbol) VALUES('" + symbol + "')";
		sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr);
	}
	query = "SELECT stock_id FROM stocks WHERE stock_symbol='" + symbol + "';";
	sqlite3_prepare(db, query.c_str(), -1, &stmt, nullptr);
	sqlite3_step(stmt);
	int id = sqlite3_column_int(stmt, 0);
	sqlite3_finalize(stmt);
	return id;
}

double Stock::MA(int n) {
	int len = candles.size();
	int i = (len - n >= 0) ? len - n : 0; 
	double ma = 0;
	for (int j = i; j < len; j++) {
		ma += candles[j].close;
	}
	ma = ma / (len - i);
	return ma;
}

double Stock::EMA(int n) {
	int len = candles.size();
	int i = (len - n >= 0) ? len - n : 0;
	double ema = 0;
	for (int j = i; j < len; j++) {
		if (j == i) ema = candles[j].close;
		else ema = 2 / (n + 1) * candles[j].close + ema * (1 - 2 / (n + 1));
	}
	return ema;
}

double Stock::getCurrentPrice() {
	time_t now = time(nullptr);
	string url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol + "?period1=" + std::to_string(now - 3600 * 41)
		+ "&period2=" + std::to_string(now) + "&interval=1h";
	CURL* curl;
	CURLcode res;
	std::string readBuffer;
	curl = curl_easy_init();
	if (curl) {
		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
		curl_easy_setopt(curl, CURLOPT_WRITEDATA, &readBuffer);
		res = curl_easy_perform(curl);
		curl_easy_cleanup(curl);
	}
	string key;
	for (auto it = readBuffer.begin(); it != readBuffer.end(); it++) {
		int i = it - readBuffer.begin();
		if ((key = readBuffer.substr(i, 5)) == "close") {
			string value = "";
			for (std::string::iterator jt = it + 3 + key.length(); *jt != ']'; jt++) {
				if (*jt == ',') value = "";
				else value += *jt;
			}
			return atof(value.c_str());
		}
	}
	return -1;
}

bool Stock::exists() {
	if (candles.size() == 0) return false;
	return true;
}

void Stock::showStock(sqlite3* db) {
	string symbol;
	time_t timestamp1, timestamp2;
	cout << "Prikaz informacije o akciji: " << endl;
	cout << "Unesite simbol akcije: ";
	cin >> symbol;
	cout << "Unesite pocetni timestamp: ";
	cin >> timestamp1;
	cout << "Unesite krajnji timestamp: ";
	cin >> timestamp2;
	Stock stock(symbol, timestamp1, timestamp2, db);
	cout << stock;

	cout << "Dodatne informacije? " << endl
		<< "1. Moving average" << endl
		<< "2. Exponential moving average" << endl
		<< "Unesite opciju: ";
	int option, n;
	cin >> option;
	switch (option) {
	case 1:
		cout << "Unesite broj dana: ";
		cin >> n;
		cout << "MA(" << n << ") = " << stock.MA(n) << endl;
		break;
	case 2:
		cout << "Unesite broj dana: ";
		cin >> n;
		cout << "EMA(" << n << ") = " << stock.EMA(n) << endl;
		break;
		break;
	default:
		break;
	}
}

ostream& operator<<(ostream& os, Stock& stock) {
	if (stock.candles.size() == 0) {
		cout << "Nema informacija o ovoj akciji u zadatom periodu. " << endl;
		return os;
	}

	cout << "Akcija " << stock.symbol << " u periodu " << stock.start << " - " << stock.end << endl;
	cout << setw(15) << "Pocetak" << setw(15) << "Kraj" << setw(12) << "Open"
		<< setw(12) << "Low" << setw(12) << "High" << setw(12) << "Close" << endl;

	for (auto candle = stock.candles.begin(); candle != stock.candles.end(); candle++) {
		cout << *candle << endl;
	}
	return os;
}
