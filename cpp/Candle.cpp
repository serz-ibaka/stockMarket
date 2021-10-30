#include "Candle.h"

Candle::Candle(Stock* stock, double open, double low, double high, double close, time_t start)
    : stock(stock), open(open), low(low), high(high), close(close), start(start) {}

bool Candle::inDatabase(int id, sqlite3* db) {
    bool ret = false;
    string query = "SELECT * FROM candles \
            WHERE stock_id=" + to_string(id) + " AND beginning_time='" + to_string(start) + "';";
    sqlite3_stmt* stmt;
    sqlite3_prepare(db, query.c_str(), -1, &stmt, nullptr);
    if (sqlite3_step(stmt) == SQLITE_ROW) ret = true;
    sqlite3_finalize(stmt);
    return ret;
}

ostream& operator<<(ostream& os, Candle& candle) {
    string color = (candle.open < candle.close) ? _GREEN : _RED;

    cout << setw(15) << candle.start << setw(15) << candle.start + 3600
        << color << setw(12) << setprecision(6) << candle.open
        << setw(12) << setprecision(9) << candle.low
        << setw(12) << setprecision(9) << candle.high
        << setw(12) << setprecision(9) << candle.close << _WHITE;

    return os;
}
