package model;

import java.text.DecimalFormat;
import java.util.Observable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User extends Observable {
	private StringProperty username, //PRIMARY KEY
				password,
				firstName, 
				lastName;
	
	private DoubleProperty balance;
	
	public User(String username, String password, String firstName, String lastName, double balance) {
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.balance = new SimpleDoubleProperty(balance);
	}
	
	//GETTERS
	public String getUsername() {
		return username.getValue();
	}
	
	public String getPassword() {
		return password.getValue();
	}

	public String getFirstName() {
		return firstName.getValue();
	}
	
	public String getLastName() {
		return lastName.getValue();
	}
	
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	public double getBalance() {
		return balance.doubleValue();
	}
	
	public String getFormattedBalance() {
		return DecimalFormat.getCurrencyInstance().format(getBalance());
	}
	
	//PROPERTIES
	public StringProperty usernameProperty() {
		return username;
	}
	
	public StringProperty passwordProperty() {
		return password;
	}
	
	public StringProperty firstNameProperty() {
		return firstName;
	}
	
	public StringProperty lastNameProperty() {
		return lastName;
	}
	
	public DoubleProperty balanceProperty() {
		return balance;
	}
	
	//SETTERS
	public void setUsername(String username) {
		this.username.setValue(username);
	}
	
	public void setPassword(String password) {
		this.password.setValue(password);
	}
	
	public void setFirstName(String firstName) {
		this.firstName.setValue(firstName);
	}
	
	public void setLastName(String lastName) {
		this.lastName.setValue(lastName);
	}
	
	public void setBalance(double balance) {
		this.balance.setValue(balance);
	}
	
}
