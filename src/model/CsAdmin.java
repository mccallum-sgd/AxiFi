package model;

import java.util.Observable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CsAdmin extends Observable {
	
	private StringProperty username; //primary key
	private StringProperty password;
	private StringProperty firstName;
	private StringProperty lastName;
	
	public CsAdmin(String username, String password, String firstName, String lastName) {
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
	}
	
	//GETTERS
	public String getUsername() {
		return username.getValue();
	}
	
	public String getFirstName() {
		return firstName.getValue();
	}
	
	public String getPassword() {
		return password.getValue();
	}
	
	public String getLastName() {
		return lastName.getValue();
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
		
}
