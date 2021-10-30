package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import exceptions.WrongStockSymbolException;
import parsing.StockParser;

public class Search extends Panel {
	
	Session owner;
	
	public Search(Session owner) {
		this.owner = owner;
		
		Label label = new Label("Search stock: ");
		TextField textField = new TextField(4);
		Panel panelSearch = new Panel(new GridLayout(1, 2));
		panelSearch.add(label);
		panelSearch.add(textField);
		
		Button button = new Button("Search");
		Panel buttonPanel = new Panel(new BorderLayout());
		buttonPanel.add(button, BorderLayout.EAST);
		
		Label infoLabel = new Label("");
		infoLabel.setForeground(Color.RED);
		
		Panel panel = new Panel(new GridLayout(3, 1));
		panel.add(panelSearch);
		panel.add(buttonPanel);
		panel.add(infoLabel);
		
		setLayout(new BorderLayout());
		add(panel, BorderLayout.NORTH);
		
		button.addActionListener(ae -> {
			
			try {
				StockParser stockParser = new StockParser();
				String buffer = stockParser.getStock(textField.getText());
				
				
				if(buffer == null || buffer.length() < 3000) {
					textField.setText("");				
					throw new WrongStockSymbolException();
				}
				owner.searchToAppPanel(textField.getText());
				textField.setText("");
			} catch(WrongStockSymbolException e) {
				Dialog warning = new Dialog(Search.this.owner.owner, ModalityType.APPLICATION_MODAL);
				warning.setTitle("Warning");
				warning.add(new Label(e.getMessage(), Label.CENTER));
				warning.setBounds(750, 350, 300, 100);
				warning.setResizable(false);
				
				warning.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						warning.dispose();
					}
				});;
				
				warning.setVisible(true);
			}
		});
		
		
	}

}
