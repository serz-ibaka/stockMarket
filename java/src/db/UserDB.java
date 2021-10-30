package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import resources.User;

public class UserDB {
	
	public static User getUserByUsername(String username, Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username='" + username + "';");
		if(resultSet.next()) {
			return new User(resultSet.getString(2), resultSet.getString(3), resultSet.getDouble(4));
		}
		
		return null;
	}
	
	public static int getIdByUser(User user, Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username='" + user.getUsername() + "';");
		return resultSet.getInt(1);
	}
	
	public static void insertUser(User user, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO users(username, password, money_balance) VALUES (?, ?, ?);");
		statement.setString(1, user.getUsername());
		statement.setString(2, user.getPassword());
		statement.setDouble(3, user.getMoneyBalance());
		statement.execute();		
	}
	
	public static void addMoney(double money, User user, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"UPDATE users SET money_balance=" + (user.getMoneyBalance() + money) + " WHERE username='" + user.getUsername() + "';");
		statement.execute();
		user.setMoneyBalance(user.getMoneyBalance() + money);
	}
	
	public static void removeMoney(double money, User user, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"UPDATE users SET money_balance=" + (user.getMoneyBalance() - money) + " WHERE username='" + user.getUsername() + "';");
		statement.execute();	
		user.setMoneyBalance(user.getMoneyBalance() - money);	
	}
	
}
