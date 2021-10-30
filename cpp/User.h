#ifndef _USER_H_
#define _USER_H_

#include <conio.h>
#include <iostream>
#include <string>

#include "Exceptions.h."
#include "Transaction.h"
#include "sqlite/sqlite3.h"

using namespace std;

class User {

public:

	User() {}

	void setUser(sqlite3* db);

	void buy(sqlite3* db);
	void sell(sqlite3* db);

	friend ostream& operator<<(ostream& os, User& user);

	void payment(sqlite3* db);

private:
	string username;
	string password;
	double moneyBalance = 0;
	vector<Transaction> transactions;

	void registration(sqlite3* db);
	void login(sqlite3* db);

	static bool usernameExists(std::string username, sqlite3* db);
	static string passwordInput();

	void updateMoney(sqlite3* db);

	int getId(sqlite3* db);

	void getTransactions(sqlite3* db);

};

#endif