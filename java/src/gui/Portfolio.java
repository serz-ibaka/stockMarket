package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.TransactionDB;
import db.UserDB;
import resources.Transaction;
import resources.User;

public class Portfolio extends Panel {

	Session owner;
	private User user;
	ArrayList<Transaction> transactions;
	List transactionList = new List(10);
	private Button sellButton = new Button("Sell");
	private Button searchButton = new Button("Search");
	private Label moneyLabel = new Label();
	
	public Portfolio(Session owner) {
		this.owner = owner;
		
		user = this.owner.owner.owner.getUser();
		try {
			transactions = TransactionDB.getTransactionsByUser(user, owner.owner.owner.getConnection());
		} catch (SQLException e) {}
		
		for(Transaction transaction : transactions) {
			transactionList.add(transaction.toString());
		}	
		
		setLayout(new BorderLayout());
		
		add(transactionList, BorderLayout.NORTH);
		
		Panel buttonsPanel = new Panel(new GridLayout(2, 1));
		buttonsPanel.add(sellButton);
		buttonsPanel.add(searchButton);
		Panel southPanel = new Panel(new BorderLayout());
		southPanel.add(buttonsPanel, BorderLayout.EAST);
		
		Panel userPanel = new Panel(new GridLayout(2, 1));
		Panel usernamePanel = new Panel();
		usernamePanel.add(new Label("Username: "));
		usernamePanel.add(new Label(user.getUsername()));
		userPanel.add(usernamePanel, BorderLayout.NORTH);
		Panel moneyPanel = new Panel();
		moneyPanel.add(new Label("Money balance"));
		moneyLabel.setText("" + user.getMoneyBalance());
		moneyPanel.add(moneyLabel);
		userPanel.add(moneyPanel, BorderLayout.SOUTH);
		southPanel.add(userPanel, BorderLayout.WEST);		
		
		add(southPanel, BorderLayout.SOUTH);
		
		addButtonListeners();
		
	}
	
	private void addButtonListeners() {
		
		sellButton.addActionListener(ae -> {
			String transaction = transactionList.getSelectedItem();
			if(transaction == null) return;
			
			Pattern pattern;
			Matcher matcher;
			String regex = "^([0-9]+)\\. ([0-9]+) ([a-z]+) stocks at a price of ([0-9\\.]+)\\. Current price: (.+)$";
			// return id + ". " + count + " " + stock.getStockName() + " stocks at a price of " + price + ". Timestamp: " + timestamp;
			//id + ". " + count + " " + stock.getStockName() + " stocks at a price of " + price + ". Current price: " + currentPrice
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(transaction);
			matcher.find();
			int id = Integer.parseInt(matcher.group(1));
			try {
				TransactionDB.removeTransaction(id, owner.owner.owner.getConnection());
			} catch (SQLException e) {}
			
			double price = Double.parseDouble(matcher.group(4));
			int count = Integer.parseInt(matcher.group(2));
			double money = price * count;
			try {
				UserDB.addMoney(money, user, owner.owner.owner.getConnection());
			} catch (SQLException e) {}
			
			moneyLabel.setText("" + (user.getMoneyBalance() + money));
			
			transactionList.remove(transaction);
		});
		
		searchButton.addActionListener(ae -> {
			owner.portfolioToSearch();
		});
		
		
	}
	
	
}
