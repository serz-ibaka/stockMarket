package gui;

import java.awt.CardLayout;
import java.awt.Panel;

import test.Test;

public class Session extends Panel {

	StockMarket owner;
	
	Portfolio portfolio;
	Search search;
	AppPanel appPanel;
	CardLayout layout = new CardLayout();
	
	
	public Session(StockMarket owner) {
		this.owner = owner;
		portfolio = new Portfolio(this);
		search = new Search(this);
		appPanel = new AppPanel("tsla", this);
		setLayout(layout);
		add(portfolio);
		add(search);
		add(appPanel);	
		owner.setLocation(600, 300);
	}

	public void portfolioToSearch() {
		layout.next(this);
		owner.setSize(300, 100);
		owner.setLocation(600, 300);
		
	}

	public void searchToAppPanel(String stock) {
		remove(appPanel);
		appPanel = new AppPanel(stock, this);
		add(appPanel);
		layout.next(this);
		owner.setSize(1100, 900);
		owner.setLocation(100, 100);
	}

	public void appPanelToPortfolio() {
		layout.next(this);
		owner.setSize(540, 260);
		owner.setLocation(300, 200);
		
	}

	public void appPanelToSearch() {
		layout.previous(this);
		owner.setSize(300, 100);
		owner.setLocation(600, 300);
	}
	
}
