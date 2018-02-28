package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Observable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Transaction extends Observable {
	private StringProperty ownerName;
	private StringProperty name; //PRIMARY KEY
	private StringProperty description;
	private ObjectProperty<LocalDate> time; 
	private DoubleProperty amount;
	private DoubleProperty fee;
	
	public final SimpleDateFormat dFormat = new SimpleDateFormat("");
	
	public Transaction(String ownerUsername, String name, String description, LocalDate time, double amount, double fees) {
		this.ownerName = new SimpleStringProperty(ownerUsername);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.time = new SimpleObjectProperty<LocalDate>(time);
		this.amount = new SimpleDoubleProperty(amount);
		this.fee = new SimpleDoubleProperty(fees);
	}

	//GETTERS
	public String getOwnerName() {
		return ownerName.getValue();
	}
	
	public String getName() {
		return name.getValue();
	}
	
	public String getDescription() {
		return description.getValue();
	}
	
	public LocalDate getTime() {
		return time.getValue();
	}
	
	public double getAmount() {
		return amount.doubleValue();
	}
	
	public double getFee() {
		return fee.doubleValue();
	}
	
	public double getFeeAmount() {
		return fee.doubleValue() * amount.doubleValue();
	}
	
	//PROPERTIES
	public StringProperty ownerNameProperty() {
		return ownerName;
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	
	public StringProperty descriptionProperty() {
		return description;
	}
	
	public ObjectProperty<LocalDate> timeProperty() {
		return time;
	}

	public DoubleProperty amountProperty() {
		return amount;
	}
	
	public DoubleProperty feeProperty() {
		return fee;
	}
	
	//SETTERS
	public void setOwnerName(String ownerName) {
		this.ownerName.setValue(ownerName);
	}
	
	public void setName(String name) {
		this.name.setValue(name);
	}
	
	public void setDescription(String description) {
		this.description.setValue(description);
	}
	
	public void setTime(LocalDate time) {
		try {
			this.time.setValue(dFormat.parse(time.toString()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		} catch (ParseException e) {
			throw new IllegalArgumentException("LocalDate is not of the required format.");
		}
	}

	public void setAmount(double amount) {
		this.amount.setValue(amount);
	}
	
	public void setFee(double fee) {
		this.fee.setValue(fee);
	}
	
}
