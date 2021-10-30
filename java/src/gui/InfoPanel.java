package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import db.TransactionDB;
import db.UserDB;
import exceptions.NotEnoughMoneyException;
import indicators.EMA;
import indicators.MA;
import parsing.StockParser;
import resources.Candle;
import resources.Stock;
import resources.Transaction;

public class InfoPanel extends Panel {
	
	private Label stockLabel = new Label();
	private Label timestampLabel = new Label();
	private Label realtimeLabel = new Label();
	private Label openLabel = new Label();
	private Label closeLabel = new Label();
	private Label highLabel = new Label();
	private Label lowLabel = new Label();
	
	AppPanel owner;
	
	private void populate() {
		setLayout(new BorderLayout());
		
		
		stockLabel.setFont(new Font("Grandview", Font.BOLD, 20));
		
		Label timestamp = new Label("Timestamp");
		timestamp.setFont(new Font("Arial", Font.BOLD, 16));
		Label realtime = new Label("Realtime");
		realtime.setFont(new Font("Arial", Font.BOLD, 16));
		Label open = new Label("Open");
		open.setFont(new Font("Arial", Font.BOLD, 16));
		Label close = new Label("Close");
		close.setFont(new Font("Arial", Font.BOLD, 16));
		Label high = new Label("High");
		high.setFont(new Font("Arial", Font.BOLD, 16));
		Label low = new Label("Low");
		low.setFont(new Font("Arial", Font.BOLD, 16));
		Label trash = new Label("__________________________");
		
		Panel gridPanel = new Panel(new GridLayout(17, 1));
		gridPanel.add(stockLabel);
		gridPanel.add(timestamp);
		gridPanel.add(timestampLabel);
		gridPanel.add(realtime);
		gridPanel.add(realtimeLabel);
		gridPanel.add(open);
		gridPanel.add(openLabel);
		gridPanel.add(close);
		gridPanel.add(closeLabel);
		gridPanel.add(high);
		gridPanel.add(highLabel);
		gridPanel.add(low);
		gridPanel.add(lowLabel);
		gridPanel.add(trash);
		
		Panel days = new Panel();
		days.add(new Label("Days"));
		TextField daysField = new TextField(5);
		days.add(daysField);
		
		Panel ma = new Panel();
		ma.add(new Label("MA(n)"));
		Label maLabel = new Label();
		ma.add(maLabel);
		
		Panel ema = new Panel();
		ema.add(new Label("EMA(n)"));
		Label emaLabel = new Label();
		ema.add(emaLabel);
		
		gridPanel.add(days);
		gridPanel.add(ma);
		gridPanel.add(ema);
		
		
		daysField.addTextListener(te -> {
			try {
				maLabel.setText("" + new MA().average(Integer.parseInt(daysField.getText()), owner.stock));
				emaLabel.setText("" + new EMA().average(Integer.parseInt(daysField.getText()), owner.stock));
				maLabel.revalidate();
				emaLabel.revalidate();
			} catch(Exception e) {
				maLabel.setText("");
				emaLabel.setText("");
				maLabel.revalidate();
				emaLabel.revalidate();
			}
		});
		
		
		add(gridPanel, BorderLayout.NORTH);
		
		Panel southGrid = new Panel(new GridLayout(4, 1, 0, 5));
		Button portfolioButton = new Button("Portfolio");
		Button buyButton = new Button("Buy");
		Button searchButton = new Button("Search");
		Panel count = new Panel();
		Label countLabel = new Label("Count: ");
		TextField countTextField = new TextField(5);
		count.add(countLabel);
		count.add(countTextField);
		
		southGrid.add(count);
		southGrid.add(buyButton);
		southGrid.add(portfolioButton);
		southGrid.add(searchButton);
		
		Panel userGrid = new Panel(new GridLayout(2, 2));
		String username = owner.owner.owner.owner.getUser().getUsername();
		double moneyBalance = owner.owner.owner.owner.getUser().getMoneyBalance();
		Label usernameLabel = new Label(username);
		usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
		Label moneyLabel = new Label("" + moneyBalance);
		userGrid.add(usernameLabel);
		userGrid.add(new Label(), 1);
		userGrid.add(new Label("Balance: "), 2);
		userGrid.add(moneyLabel, 3);
		
		Panel southPanel = new Panel(new BorderLayout());
		southPanel.add(userGrid, BorderLayout.NORTH);
		southPanel.add(southGrid, BorderLayout.SOUTH);
		add(southPanel, BorderLayout.SOUTH);
		
		
		portfolioButton.addActionListener(ae -> {
			owner.owner.appPanelToPortfolio();
		});
		
		buyButton.addActionListener(ae -> {
			try {
				int cnt = Integer.parseInt(countTextField.getText());
				double price = StockParser.getCurrentPrice(stockLabel.getText());
				if(cnt * price > InfoPanel.this.owner.owner.owner.owner.getUser().getMoneyBalance()) throw new NotEnoughMoneyException();
				
				Transaction transaction = new Transaction(UserDB.getIdByUser(InfoPanel.this.owner.owner.owner.owner.getUser(), InfoPanel.this.owner.owner.owner.owner.getConnection()), 
								 						InfoPanel.this.owner.stock, InfoPanel.this.owner.owner.owner.owner.getUser(), cnt, price, System.currentTimeMillis() / 1000);
				
				InfoPanel.this.owner.owner.portfolio.transactions.add(transaction);
				InfoPanel.this.owner.owner.portfolio.transactionList.add(transaction.toString());
				
				TransactionDB.addTransaction(transaction, InfoPanel.this.owner.owner.owner.owner.getConnection());
				UserDB.removeMoney(cnt * price, InfoPanel.this.owner.owner.owner.owner.getUser(), InfoPanel.this.owner.owner.owner.owner.getConnection());
				
				moneyLabel.setText("" + InfoPanel.this.owner.owner.owner.owner.getUser().getMoneyBalance());
				
				Dialog warning = new Dialog(InfoPanel.this.owner.owner.owner, ModalityType.APPLICATION_MODAL);
				warning.setTitle("Warning");
				Panel panel = new Panel(new GridLayout(2, 1));
				panel.add(new Label("Transaction made", Label.CENTER));
				warning.add(panel);
				warning.setBounds(750, 350, 300, 100);
				warning.setResizable(false);
				
				warning.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						warning.dispose();
					}
				});
				
				warning.setVisible(true);
				
			} catch(NumberFormatException | NotEnoughMoneyException e) {
				Dialog warning = new Dialog(InfoPanel.this.owner.owner.owner, ModalityType.APPLICATION_MODAL);
				warning.setTitle("Warning");
				warning.add(new Label(e.getMessage(), Label.CENTER));
				warning.setBounds(750, 350, 300, 100);
				warning.setResizable(false);
				
				warning.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						warning.dispose();
					}
				});
				
				warning.setVisible(true);
			} catch(SQLException e) {}
		});
		
		searchButton.addActionListener(ae -> {
			owner.owner.appPanelToSearch();
		});
		
		setBackground(new Color(Integer.parseInt("CCFFFF", 16)));
	}
	
	public InfoPanel(AppPanel owner) {
		this.owner = owner;
		stockLabel.setText(owner.stock.getStockName().toUpperCase());
		
		populate();
	}
	
	private String realtime(long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId()).toString();
	}
	
	public void setInfo(Candle candle) {
		timestampLabel.setText("" + candle.getTimestamp());
		realtimeLabel.setText(realtime(candle.getTimestamp()));
		openLabel.setText("" + candle.getOpen());
		closeLabel.setText("" + candle.getClose());
		highLabel.setText("" + candle.getHigh());
		lowLabel.setText("" + candle.getLow());
	}
	

}
