package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import resources.Stock;
import resources.Transaction;
import resources.User;

public class TransactionDB {

	public static ArrayList<Transaction> getTransactionsByUser(User user, Connection connection) throws SQLException {
		ArrayList<Transaction> transactions = new ArrayList<>();
		
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT user_id FROM users WHERE username='" + user.getUsername() + "';");
		int userId;
		if(resultSet.next()) {
			userId = resultSet.getInt(1);
		} else userId = -1;
		
		statement = connection.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM transactions WHERE user_id="+ userId + ";");
		while(resultSet.next()) {
			int id = resultSet.getInt(1);
			String stockName = resultSet.getString(3);
			double price = resultSet.getDouble(4);
			int count = resultSet.getInt(5);
			long timestamp = resultSet.getLong(6);
			Stock stock = new Stock(stockName);
			
			transactions.add(new Transaction(id, stock, user, count, price, timestamp));
		}
		return transactions;
	}

	public static void removeTransaction(int id, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"DELETE FROM transactions WHERE transaction_id=" + id + ";");
		statement.execute();
	}
	
	public static void addTransaction(Transaction transaction, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO transactions(user_id, stock_name, price, count, timestamp) VALUES (?, ?, ?, ?, ?);");
		statement.setInt(1, UserDB.getIdByUser(transaction.getUser(), connection));
		statement.setString(2, transaction.getStock().getStockName());
		statement.setDouble(3, transaction.getPrice());
		statement.setInt(4, transaction.getCount());
		statement.setLong(5, transaction.getTimestamp());
		statement.execute();
	}
	
}
