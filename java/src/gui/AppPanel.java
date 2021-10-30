package gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import resources.Stock;

public class AppPanel extends Panel {

	InfoPanel infoPanel;
	StockCanvas stockCanvas;
	Stock stock;
	
	Session owner;
	
	
	public AppPanel(String stockName, Session owner) {
		this.owner = owner;
		
		stock = new Stock(stockName);
		stockCanvas = new StockCanvas(this);
		infoPanel = new InfoPanel(this);
		
		
		setLayout(new BorderLayout());
		add(infoPanel, BorderLayout.EAST);
		add(stockCanvas, BorderLayout.CENTER);
		
		stockCanvas.repaint();
		
	}
	
	/*public static void main(String[] args) {
		Frame frame = new Frame();
		
		frame.setSize(1100, 700);
		
		
		frame.add(new AppPanel("aapl"));
		frame.setVisible(true);
	}*/
	
}
