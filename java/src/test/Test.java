package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import gui.Authentication;
import gui.StockMarket;
import resources.User;

public class Test {

	Authentication authentication;
	StockMarket stockMarket;
	
	User user;
	Connection connection;
	
	public Connection getConnection() {
		return connection;
	}

	public Test() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:src/db/database.db");
		} catch (SQLException e) {
			e.printStackTrace();
			connection = null;
			System.out.println("Nece baza");
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(connection != null) connection.close();
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}

	public static void main(String[] args) {
		Test test = new Test();
		if(test.connection == null) return;
		
		test.authentication = new Authentication(test);
		synchronized (test) {
			while(test.user == null) {
				try {
					test.wait();
				} catch (InterruptedException e) {}
			}	
		}
		
		test.authentication.dispose();
		
		System.out.println(test.user.getUsername() + " " + test.user.getPassword());
		
		test.stockMarket = new StockMarket(test);
		
		
	}

}
