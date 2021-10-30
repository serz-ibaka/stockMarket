package gui;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import test.Test;

public class StockMarket extends Frame {

	Test owner;
	Session session;
	
	public StockMarket(Test owner) {
		this.owner = owner;
		
		session = new Session(this);

		setSize(540, 260);
		add(session);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		this.setVisible(true);
		
	}
	
	
}
