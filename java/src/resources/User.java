package resources;

public class User {

	private String username;
	private String password;
	private double moneyBalance;
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public double getMoneyBalance() {
		return moneyBalance;
	}
	public void setMoneyBalance(double moneyBalance) {
		this.moneyBalance = moneyBalance;
	}

	public User(String username, String password, double moneyBalance) {
		this.username = username;
		this.password = password;
		this.moneyBalance = moneyBalance;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof User))
			return false;
		User user = (User) obj;
		return user.username.equals(username) && user.password.equals(password);
	}
	
	
	
	
}
