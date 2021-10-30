package gui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import db.UserDB;
import exceptions.UserNotExistsException;
import exceptions.UsernameExistsException;
import exceptions.WrongPasswordException;
import resources.User;
import test.Test;


public class Authentication extends Frame {
	
	private enum Mode { LOGIN, REGISTRATION };
	private Mode mode = Mode.REGISTRATION;
	
	private Test owner;
	
	CheckboxGroup authenticationGroup = new CheckboxGroup();
	Checkbox registrationCheckbox = new Checkbox("Registration", true, authenticationGroup);
	Checkbox loginCheckbox = new Checkbox("Login", false, authenticationGroup);
	
	
	Button finishButton = new Button("Sign Up");
	Panel buttonPanel = new Panel();
	
	
	Label usernameLabel = new Label("Username: ");
	TextField usernameTextField = new TextField(20);
	Panel usernamePanel = new Panel();
	
	Label passwordLabel = new Label("Password: ");
	TextField passwordTextField = new TextField(20);
	Panel passwordPanel = new Panel();
	
	Label balanceLabel = new Label("Starting balance: ");
	TextField balanceTextField = new TextField(10);
	Panel balancePanel = new Panel();
	
	Panel inputPanel = new Panel(new GridLayout(3, 1));
	
	
	
	private void changedSelection() {
		registrationCheckbox.addItemListener(il -> {
			finishButton.setLabel("Sign Up");
			finishButton.revalidate();
			inputPanel.add(balancePanel, 2);
			mode = Mode.REGISTRATION;
		});
		loginCheckbox.addItemListener(il -> {
			finishButton.setLabel("Login");
			finishButton.revalidate();
			inputPanel.remove(balancePanel);
			mode = Mode.LOGIN;
		});
	}
	
	private void buttonPressed() {
		finishButton.addActionListener(al -> {
			String username = usernameTextField.getText();
			try {
				User user = UserDB.getUserByUsername(username, owner.getConnection());
				if(mode == Mode.REGISTRATION) {
					if(user != null) throw new UsernameExistsException();
		
					String password = passwordTextField.getText();
					double moneyBalance = Double.parseDouble(balanceTextField.getText());
					
					user = new User(username, password, moneyBalance);
					UserDB.insertUser(user, owner.getConnection());
					
					owner.setUser(user);
					synchronized (owner) {
						owner.notify();	
					}
					
				}
				else if(mode == Mode.LOGIN) {
					if(user == null) throw new UserNotExistsException();
					String password = passwordTextField.getText();
					User loggedUser = new User(username, password, 0);
					if(!user.equals(loggedUser)) throw new WrongPasswordException();

					owner.setUser(user);
					synchronized (owner) {
						owner.notify();						
					}
					
				}
			} catch(Exception e) { 
				Dialog warning = new Dialog(this, ModalityType.APPLICATION_MODAL);
				warning.setTitle("Warning");
				warning.add(new Label(e.getMessage(), Label.CENTER));
				warning.setBounds(750, 350, 300, 100);
				warning.setResizable(false);
				
				warning.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						warning.dispose();
						usernameTextField.setText("");
						passwordTextField.setText("");
						balanceTextField.setText("");
					}
				});;
				
				warning.setVisible(true);
			}			
		});
	}
	
	private void populate() {
		setLayout(new GridLayout(2, 1));
		
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		passwordPanel.add(passwordLabel);
		passwordTextField.setEchoChar('*');
		passwordPanel.add(passwordTextField);
		balancePanel.add(balanceLabel);
		balancePanel.add(balanceTextField);
		inputPanel.add(usernamePanel);
		inputPanel.add(passwordPanel);
		inputPanel.add(balancePanel);
		add(inputPanel);
		
		Panel radioGrid = new Panel(new GridLayout(2, 1));
		Panel radioPanel1 = new Panel();
		Panel radioPanel2 = new Panel();
		radioPanel1.add(registrationCheckbox);
		radioPanel2.add(loginCheckbox);
		radioGrid.add(radioPanel1);
		radioGrid.add(radioPanel2);
		changedSelection();
		
		Panel downGrid = new Panel(new GridLayout(2, 1));
		downGrid.add(radioGrid);
		buttonPanel.add(finishButton);
		buttonPressed();
		downGrid.add(buttonPanel);
		add(downGrid);
	}
	
	public Authentication(Test owner) {
		this.owner = owner;
		
		setBackground(Color.LIGHT_GRAY);
		setBounds(700, 200, 400, 300);
		setTitle("Authentication");
		setResizable(false);

		populate();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		this.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		// new Authentication();
	}
	
	
}
