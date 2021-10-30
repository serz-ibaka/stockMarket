#include "User.h"
#include "Stock.h"

int main() {
	/*Stock stock("aapl", 1616072670, 1617531870);
	cout << stock << endl;
	double ma = stock.MA(10);
	double ema = stock.EMA(10);
	cout << "MA: " << ma << endl << "EMA: " << ema;*/

	sqlite3* db;
	sqlite3_open("database.db", &db);

	User user;
	user.setUser(db);

	while (true) {
		cout << endl << "Meni: " << endl
			<< "1. Prikazite informacije o akciji" << endl
			<< "2. Kupovina akcije" << endl
			<< "3. Prikaz portfolia" << endl
			<< "Unesite opciju: ";
		int option;
		cin >> option;
		cout << endl;
		switch (option) {
		case 1:
			Stock::showStock(db);
			break;
		case 2:
			user.buy(db);
			break;
		case 3:
			cout << user << endl;
			cout << "1. Prodaja akcije" << endl
				<< "2. Uplatite novac" << endl
				<< "0. Izlaz" << endl
				<< "Unesite opciju: ";
			cin >> option;
			switch (option) {
			case 1:
				user.sell(db);
				break;
			case 2:
				user.payment(db);
				break;
			}
			break;
		default:
			continue;
		}

		cout << "Pritisnite ENTER sta za nastavak . . .";
		getchar(); getchar();
		cout << endl << endl;
	}

	sqlite3_close(db);
}