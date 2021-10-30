#include "User.h"

void User::setUser(sqlite3* db) {
	char option;
	while (true) {
		cout << "Registration/Login/Exit?[r/l/e] ";
		cin >> option;
		switch (option) {
		case 'r': case 'R':
			registration(db);
			if (username == "") continue;
			return;
		case 'l': case 'L':
			login(db);
			if (username == "") continue;
			getTransactions(db);
			return;
		case 'e': case 'E':
			return;
		default:
			cout << "Pogresna opcija! " << endl;
		}
	}
}

void User::buy(sqlite3* db) {
	cout << "Unesite simbol akcije: ";
	string symbol;
	cin >> symbol;
	Stock* stock = new Stock(symbol, time(nullptr) - 360000, time(nullptr), db);
	if (!stock->exists()) {
		cout << "Akcija sa ovim simbolom ne postoji. " << endl;
		return;
	}
	double price = stock->getCurrentPrice();
	int cnt;
	char option;
	while (true) {
		cout << "Unesite broj akcija koje zelite da kupite: ";
		cin >> cnt;
		if (cnt * price <= moneyBalance) {
			moneyBalance -= cnt * price;

			int uid = getId(db), sid = stock->getId(db);
			
			updateMoney(db);

			Transaction t(stock, price, time(nullptr), cnt, -1);
			t.insert(sid, uid, db);

			cout << "Kupili ste " << cnt << " deonica akcije " << symbol << ". " << endl
				<< "Preostalo stanje na racunu je " << moneyBalance << ". " << endl;
			transactions.push_back(t);
			return;
		}
		cout << "Nemate dovoljno novca na racunu, vase trenutno stanje je " << moneyBalance << endl
			<< "Da li zelite da nastavite sa kupovinom?[y/n] ";
		cin >> option;
		if (!(option == 'y' || option == 'Y')) return;
	}
}

void User::sell(sqlite3* db) {
	int id;
	cout << "Unesite id transakcije koju zelite da prodate: ";
	cin >> id;
	int i = -1;
	for (auto it = transactions.begin(); it != transactions.end(); it++) {
		if (id == it->getId()) {
			i = it - transactions.begin();
			break;
		}
	}
	if (i < 0) {
		cout << "Ne postoji akcija sa unetim identifikatorom! " << endl;
		return;
	}
	double currentPrice = transactions[i].getStock()->getCurrentPrice();
	int count = transactions[i].getCount();
	
	moneyBalance += currentPrice * count;
	updateMoney(db);

	cout << "Prodali ste " << count << " deonica akcije " << transactions[i].getStock()->getSymbol() << ". " << endl
		<< "Novo stanje na racunu je " << moneyBalance << ". " << endl;
	transactions.erase(transactions.begin() + i);

	string query = "DELETE FROM transactions WHERE transaction_id=" + to_string(id) + ";";
	sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr);
}

void User::payment(sqlite3* db) {
	cout << "Unesite kolicinu novca koju zelite da uplatite: ";
	double money;
	cin >> money;
	if (money <= 0) return;
	moneyBalance += money;
	string query = "UPDATE users SET money_balance=" + to_string(moneyBalance) + " WHERE username='" + username + "';";
	sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr);
	cout << "Novo stanje na racunu je " << moneyBalance << endl;
}

void User::registration(sqlite3* db) {
	char option;
	string username;
	while (true) {
		try {
			cout << "Username: ";
			cin >> username;
			if (usernameExists(username, db)) {
				throw ErrorUsernameExists();
			}
			break;
		}
		catch (exception& e) {
			cout << e.what() << endl << "Nastavite registraciju?[y/n] ";
			cin >> option;
			if (option == 'n' || option == 'N') break;
		}
	}
	this->username = username;
	cout << "Password: ";
	this->password = passwordInput();
	
	double moneyBalance;
	cout << "Pocetni kapital: ";
	cin >> moneyBalance;
	if (moneyBalance <= 0) moneyBalance = 1000;
	this->moneyBalance = moneyBalance;
	
	std::string query = "INSERT INTO users(username, password, money_balance) VALUES ('"
		+ username + "', '" + password + "', " + std::to_string(moneyBalance) + ");";
	sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr);
}

void User::login(sqlite3* db) {
	char option;
	while (true) {
		try {
			string username;
			cout << "Username: ";
			cin >> username;

			string query = "SELECT * FROM users WHERE username='" + username + "';";

			string password;
			double moneyBalance;

			sqlite3_stmt* stmt;
			sqlite3_prepare(db, query.c_str(), -1, &stmt, nullptr);

			if (sqlite3_step(stmt) == SQLITE_ROW) {
				password = reinterpret_cast<const char*>(sqlite3_column_text(stmt, 2));
				moneyBalance = sqlite3_column_double(stmt, 3);
				while (true) {
					try {
						cout << "Password: ";
						string pw = passwordInput();
						if (pw == password) break;
						else throw ErrorWrongPassword();
					}
					catch (exception& e) {
						cout << e.what() << endl << "Nastavite registraciju?[y/n] ";
						cin >> option;
						if (option == 'n' || option == 'N') return;
					}
				}
				this->username = username;
				this->password = password;
				this->moneyBalance = moneyBalance;
				break;
			}
			else throw ErrorUsernameNotExists();
		}
		catch (exception& e) {
			cout << e.what() << endl << "Nastavite registraciju?[y/n] ";
			cin >> option;
			if (option == 'n' || option == 'N') break;
		}
	}
}

bool User::usernameExists(string username, sqlite3* db) {
	bool b = false;

	std::string query = "SELECT * FROM users WHERE username='" + username + "';";

	sqlite3_stmt* stmt;
	sqlite3_prepare(db, query.c_str(), -1, &stmt, nullptr);

	if (sqlite3_step(stmt) == SQLITE_ROW) b = true;

	sqlite3_finalize(stmt);
	return b;
}

string User::passwordInput() {
	int bs = 8, cr = 13;
	string password = "";
	char c;

	while (true) {
		c = _getch();
		if (c == cr) {
			cout << std::endl;
			return password;
		}
		else if (c == bs && password.length() != 0) {
			password.pop_back();
			cout << "\b \b";
			continue;
		}
		else if (c == bs && password.length() == 0) continue;

		password.push_back(c);
		cout << '*';
	}
}

void User::updateMoney(sqlite3* db) {
	string query = "UPDATE users SET money_balance=" + to_string(moneyBalance) + " WHERE username='" + username + "';";
	sqlite3_exec(db, query.c_str(), nullptr, nullptr, nullptr); 
}

int User::getId(sqlite3* db) {
	sqlite3_stmt* stmt;
	string query = "SELECT user_id FROM users WHERE username='" + username + "';";
	sqlite3_prepare(db, query.c_str(), 255, &stmt, nullptr);
	sqlite3_step(stmt);
	int id = sqlite3_column_int(stmt, 0);
	sqlite3_finalize(stmt);
	return id;
}

void User::getTransactions(sqlite3* db) {
	int uid = getId(db);
	string query = "SELECT * FROM transactions WHERE user_id=" + to_string(uid) + ";";
	sqlite3_stmt* stmt, * stmt1 = nullptr;
	sqlite3_prepare(db, query.c_str(), -1, &stmt, nullptr);
	while (sqlite3_step(stmt) == SQLITE_ROW) {
		int tid = sqlite3_column_int(stmt, 0);
		int sid = sqlite3_column_int(stmt, 2);
		double price = sqlite3_column_double(stmt, 3);
		int count = sqlite3_column_int(stmt, 4);
		time_t timestamp = atol(reinterpret_cast<const char*>(sqlite3_column_text(stmt, 5)));

		
		query = "SELECT stock_symbol FROM stocks WHERE stock_id=" + to_string(sid) + ";";
		sqlite3_prepare(db, query.c_str(), -1, &stmt1, nullptr);
		sqlite3_step(stmt1);
		
		string symbol = reinterpret_cast<const char*>(sqlite3_column_text(stmt1, 0));
		Stock* stock = new Stock(symbol, timestamp, time(nullptr), db);
		transactions.push_back(Transaction(stock, price, timestamp, count, tid));
	}
	sqlite3_finalize(stmt);
	if(stmt1) sqlite3_finalize(stmt1);
}

ostream& operator<<(ostream& os, User& user) {
	cout << "User: " << user.username << endl
		<< "Current money balance: " << user.moneyBalance << endl << endl;
	cout << "Transactions: " << endl << endl;

	cout << setw(4) << "Id" << setw(8) << "Akcija" << setw(15) << "Timestamp" << setw(6) << "Broj"
		<< setw(15) << "Cena kupovine" << setw(15) << "Trenutna cena" << setw(9) << "Profit" << endl;

	for (auto transaction : user.transactions) {
		cout << transaction << endl;
	}
	return os;
}
