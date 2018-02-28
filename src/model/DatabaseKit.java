package model;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DatabaseKit {
	private Connection c;
	private File dbFile;
	
	private int currAdmin;
	private int currUser;
	private ObservableList<CsAdmin> admins;
	private ObservableList<User> allUsers;
	private ObservableList<Transaction> allTrans;
	
	public DatabaseKit() {
		init("data.db");
	}
	
	public DatabaseKit(String fileName) {
		init(fileName);
	}
	
	
/*--- DATABASE ---------------------------------------------------------------------------*/
	
	public void init(String fileName) {
        try {
	    	 //Load this class from the build path
			Class.forName("org.sqlite.JDBC").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
        
        setupFile(fileName);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> closeConnection()));
	}
	
	private void setupFile(String fileName) {
		String dbLocation = Settings.getSetting("db-location").getStringValue();
		
        if (!dbLocation.endsWith("/")) dbLocation += "/";
        
        this.dbFile = new File(dbLocation + fileName);
        
        if (dbFile.exists() && Settings.getSetting("db-encrypted").getBooleanValue())
	       		 Security.decrypt(getCurrAdmin().getPassword(), dbFile);
        else {
        	try {
				dbFile.createNewFile();
			} catch (IOException e) {
				if (e.getMessage().endsWith("Access is denied")) {
		    		  Errors.showError("The directory specified for your database file is not writeable by this program. \n"
		    				  + "Try running this program as an administrator.\n\nA new database file will now be created in AxiFi's installation directory.\n"
		    				  + "If you want to use your old database file, please use the Database location setting to move it to a writeable directory (Settings > Database location).", "Access Error");
		    		  Settings.getSetting("db-location").setStringValue(".");
		    		  init(fileName);
		    	  } else if (e.getMessage().endsWith("The system cannot find the file path specified")) {
		    		  Errors.showError("Your database file could not be found.", "Could not Locate Database File");
		    	  }
				e.printStackTrace();
			}
        }
        openConnection();
	}
	
	private void openConnection() {
		try {
			//Link a new connection to the database or create a new one if one is not already there
			c = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
			c.setReadOnly(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		buildSchema();
		loadObjects();
	}
	
	private void closeConnection() {
		try {
			c.close();
			if (Settings.getSetting("db-encrypted").getBooleanValue())
				Security.encrypt(getCurrAdmin().getPassword(), dbFile);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void buildSchema() {
		//SQL statements
		String mkUsrTable = "CREATE TABLE IF NOT EXISTS USER(" +
								"USERNAME CHAR(50) NOT NULL PRIMARY KEY," +
								"PASSWORD CHAR(50) NOT NULL," +
								"FIRSTNAME TEXT NOT NULL," +
								"LASTNAME TEXT NOT NULL," +
								"BALANCE DECIMAL(8,2)" +
							");";
		
		String mkAdmTable = "CREATE TABLE IF NOT EXISTS ADMIN(" +
								"USERNAME CHAR(50) NOT NULL PRIMARY KEY," +
								"PASSWORD CHAR(50) NOT NULL," +
								"FIRSTNAME TEXT NOT NULL," +
								"LASTNAME TEXT NOT NULL" +
							");";
		
		String mkTransTable = "CREATE TABLE IF NOT EXISTS TRANS(" +
								  "OWNERUSERNAME CHAR(50) NOT NULL," +
								  "NAME CHAR(50) NOT NULL PRIMARY KEY," +
								  "DESCRIPTION TEXT," +
								  "TIME DATE," +
								  "AMOUNT DECIMAL(8,2) NOT NULL," +
								  "FEE REAL NOT NULL" +
							  ");";
		
		try (
			Statement buildTable = c.createStatement();		
		){
			buildTable.execute(mkUsrTable);
			buildTable.execute(mkAdmTable);
			buildTable.execute(mkTransTable);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
/*--- LISTENERS (might want to collapse these)  ---------------------------------------------------------------------------*/
	
	//TRANSACTION
	private final ChangeListener<String> TRANS_OWNERNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((Transaction)observable).getName();
			updateTransaction(name, "OWNERNAME", newValue);
			loadTransactions();
		}
	};
	private final ChangeListener<String> TRANS_NAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((Transaction)observable).getName();
			updateTransaction(name, "NAME", newValue);
			loadTransactions();
		}
	};
	private final ChangeListener<String> TRANS_DESCRIPTION_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((Transaction)observable).getName();
			updateTransaction(name, "DESCRIPTION", newValue);
			loadTransactions();
		}
	};
	private final ChangeListener<LocalDate> TRANS_TIME_LISTENER = new ChangeListener<LocalDate>() {
		@Override
		public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
				LocalDate newValue) {
			String name = ((Transaction)observable).getName();
			updateTransaction(name, "TIME", newValue);
			loadTransactions();
		}
	};
	private final ChangeListener<Number> TRANS_AMOUNT_LISTENER = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue,
				Number newValue) {
			String name = ((Transaction)observable).getName();
			updateTransaction(name, "AMOUNT", newValue);
			loadTransactions();
		}
	};
	private final ChangeListener<Number> TRANS_FEE_LISTENER = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue,
				Number newValue) {
			String name = ((Transaction)observable).getName();
			updateTransaction(name, "FEE", newValue);
			loadTransactions();
		}
	};
	//USER
	private final ChangeListener<String> USER_USERNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((User)observable).getUsername();
			updateUser(name, "USERNAME", newValue);
			loadUsers();
		}
	};
	private final ChangeListener<String> USER_PASSWORD_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((User)observable).getUsername();
			updateUser(name, "PASSWORD", newValue);
			loadUsers();
		}
	};
	private final ChangeListener<String> USER_FIRSTNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((User)observable).getUsername();
			updateUser(name, "FIRSTNAME", newValue);
			loadUsers();
		}
	};
	private final ChangeListener<String> USER_LASTNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((User)observable).getUsername();
			updateUser(name, "LASTNAME", newValue);
			loadUsers();
		}
	};
	private final ChangeListener<Number> USER_BALANCE_LISTENER = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue,
				Number newValue) {
			String name = ((User)observable).getUsername();
			updateUser(name, "BALANCE", newValue);
			loadUsers();
		}
	};
	//ADMIN
	private final ChangeListener<String> ADMIN_USERNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((CsAdmin)observable).getUsername();
			updateUser(name, "USERNAME", newValue);
			loadAdmin();
		}
	};
	private final ChangeListener<String> ADMIN_PASSWORD_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((CsAdmin)observable).getUsername();
			updateUser(name, "PASSWORD", newValue);
			loadAdmin();
		}
	};
	private final ChangeListener<String> ADMIN_FIRSTNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((CsAdmin)observable).getUsername();
			updateUser(name, "FIRSTNAME", newValue);
			loadAdmin();
		}
	};
	private final ChangeListener<String> ADMIN_LASTNAME_LISTENER = new ChangeListener<String>() {
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue,
				String newValue) {
			String name = ((CsAdmin)observable).getUsername();
			updateUser(name, "LASTNAME", newValue);
			loadAdmin();
		}
	};
	
	private final ListChangeListener<User> USERS_LISTENER = new ListChangeListener<User>() {
		@Override
		public void onChanged(Change<? extends User> c) {
			while (c.next()) {
				if (c.wasRemoved())
					deleteUsers((List<User>) c.getRemoved());
				if (c.wasAdded())
					insertUsers((List<User>) c.getAddedSubList());
				loadUsers();
			}
		}
	};
	
	private final ListChangeListener<Transaction> TRANS_LISTENER = new ListChangeListener<Transaction>() {
		@Override
		public void onChanged(Change<? extends Transaction> c) {
			while (c.next()) {
				if (c.wasRemoved()) 
					deleteTransactions((List<Transaction>) c.getRemoved());
				if (c.wasAdded())
					insertTransactions((List<Transaction>) c.getAddedSubList());
				loadTransactions();
			}
		}
	};
	
/*--- LOAD  ---------------------------------------------------------------------------*/
	
	private void loadObjects() {
		loadAdmin();
		loadUsers();
		loadTransactions();
	}
	
	private void loadAdmin() {
		this.currAdmin = 0;
		List<CsAdmin> adms = new ArrayList<CsAdmin>();
		adms.add(getCurrAdmin());
		admins = FXCollections.observableList(adms);
		
		CsAdmin admin = admins.get(0);
		
		admin.usernameProperty().removeListener(ADMIN_USERNAME_LISTENER);
		admin.passwordProperty().removeListener(ADMIN_PASSWORD_LISTENER);
		admin.firstNameProperty().removeListener(ADMIN_FIRSTNAME_LISTENER);
		admin.lastNameProperty().removeListener(ADMIN_LASTNAME_LISTENER);
		
		admin.usernameProperty().addListener(ADMIN_USERNAME_LISTENER);
		admin.passwordProperty().addListener(ADMIN_PASSWORD_LISTENER);
		admin.firstNameProperty().addListener(ADMIN_FIRSTNAME_LISTENER);
		admin.lastNameProperty().addListener(ADMIN_LASTNAME_LISTENER);
	}
	
	private void loadUsers() {
		allUsers = queryUsers();
		
		if (currUser > allUsers.size()-1 || currUser < 0 || allUsers.size() == 0)
			currUser = 0;
		
		if (allUsers != null) {
			allUsers.forEach(user -> {
				user.usernameProperty().removeListener(USER_USERNAME_LISTENER);
				user.passwordProperty().removeListener(USER_PASSWORD_LISTENER);
				user.firstNameProperty().removeListener(USER_FIRSTNAME_LISTENER);
				user.lastNameProperty().removeListener(USER_LASTNAME_LISTENER);
				user.balanceProperty().removeListener(USER_BALANCE_LISTENER);
				
				user.usernameProperty().addListener(USER_USERNAME_LISTENER);
				user.passwordProperty().addListener(USER_PASSWORD_LISTENER);
				user.firstNameProperty().addListener(USER_FIRSTNAME_LISTENER);
				user.lastNameProperty().addListener(USER_LASTNAME_LISTENER);
				user.balanceProperty().addListener(USER_BALANCE_LISTENER);
			});
		}
		else allUsers = FXCollections.observableArrayList();
		allUsers.addListener(USERS_LISTENER);
	}
	
	private void loadTransactions() {
		allTrans = queryTransactions();
		
		if (allTrans != null)
			allTrans.forEach(t -> {
				t.ownerNameProperty().addListener(TRANS_OWNERNAME_LISTENER);
				t.nameProperty().addListener(TRANS_NAME_LISTENER);
				t.descriptionProperty().addListener(TRANS_DESCRIPTION_LISTENER);
				t.timeProperty().addListener(TRANS_TIME_LISTENER);
				t.amountProperty().addListener(TRANS_AMOUNT_LISTENER);
				t.feeProperty().addListener(TRANS_FEE_LISTENER);
			});
		else allTrans = FXCollections.observableArrayList();
		allTrans.addListener(TRANS_LISTENER);
	}
	
/*--- PUBLIC ---------------------------------------------------------------------------*/
	
	public CsAdmin getCurrAdmin() {
		return admins.get(currAdmin);
	}
	
	public User getCurrentUser() {
		return allUsers.get(currUser);
	}
	
	public ObservableList<User> getAllUsers() {
		return allUsers;
	}
	
	public ObservableList<Transaction> getAllTransactions() {
		return allTrans;
	}
	
	public ObservableList<Transaction> getCurrentUserTransactions() {
		ObservableList<Transaction> trans = queryTransactionsFor(getCurrentUser().getUsername());
		trans.removeListener(TRANS_LISTENER);
		trans.addListener(TRANS_LISTENER);
		return trans;
	}
	
	public void switchUser(String username, String password) {
		currUser = allUsers.indexOf(allUsers.stream()
				.filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
				.findFirst().orElse(null));
	}
	
	
/*--- USER ---------------------------------------------------------------------------*/
	
	//QUERIES
	public User queryUserBy(String username, String password) {
		final String SELECT = "SELECT * FROM USER WHERE USERNAME=?,PASSWORD=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(SELECT);
		){
			s.setString(1, username);
			s.setString(2, password);
			ResultSet results = s.executeQuery();
			
			while (results.next()) {
				String targetUsername = results.getString("USERNAME");
				String targetPassword = results.getString("PASSWORD");
				String firstName = results.getString("FIRSTNAME");
				String lastName = results.getString("LASTNAME");
				double balance = results.getDouble("BALANCE");
				return new User(targetUsername, targetPassword, firstName, lastName, balance);
			} return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ObservableList<User> queryUsers() {
		List<User> targetList = new ArrayList<User>();
		final String SELECT = "SELECT * FROM USER;";
		
		try (
			PreparedStatement s = c.prepareStatement(SELECT);
			ResultSet results = s.executeQuery();		
		){
			while(results.next()) {
				String username = results.getString("USERNAME");
				String password = results.getString("PASSWORD");
				String firstName = results.getString("FIRSTNAME");
				String lastName = results.getString("LASTNAME");
				double balance = results.getDouble("BALANCE");
				targetList.add(new User(username, password, firstName, lastName, balance));
			}
			return FXCollections.observableList(targetList);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//INSERTIONS
	private boolean insertUser(User user) {
		final String INSERT = "INSERT INTO USER(USERNAME,PASSWORD,FIRSTNAME,LASTNAME,BALANCE)" +
							"VALUES (?,?,?,?,?);";
		
		try (
			PreparedStatement s = c.prepareStatement(INSERT);	
		) {
			s.setString(1, user.getUsername());
			s.setString(2, user.getPassword());
			s.setString(3, user.getFirstName());
			s.setString(4, user.getLastName());
			s.setDouble(5, user.getBalance());
			return s.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean insertUsers(List<User> profiles) {
		boolean success = true;
		for (User p: profiles)
			success = insertUser(p);
		return success;
	}
	
	private boolean deleteUser(User profile) {
		boolean success = false;
		final String DELETE = "DELETE FROM USER WHERE USERNAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(DELETE);
		) {
			s.setString(1, profile.getUsername());
			success = s.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	//UPDATES
	private boolean updateUser(String username, String attribute, String newValue) {
		boolean success = false;
		final String UPDATE = "UPDATE USER SET ?=? WHERE USERNAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(UPDATE);		
		){
			s.setString(1, attribute);
			s.setString(2, newValue);
			s.setString(3, username);
			success = s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}
	
	private boolean updateUser(String username, String attribute, Number newValue) {
		boolean success = false;
		final String UPDATE = "UPDATE USER SET ?=? WHERE USERNAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(UPDATE);		
		){
			s.setString(1, attribute);
			s.setDouble(2, newValue.doubleValue());
			s.setString(3, username);
			success = s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}
	
	//DELETIONS
	private boolean deleteUsers(List<User> profiles) {
		boolean success = false;
		for (User p: profiles)
			success = deleteUser(p);
		return success;
	}
	
	
/*--- TRANSACTION ---------------------------------------------------------------------------*/
	
	// QUERIES
	public ObservableList<Transaction> queryTransactions() {
		List<Transaction> targetList = new ArrayList<Transaction>();
		final String SELECT = "SELECT * FROM TRANS;";
		
		try (
				ResultSet results = c.prepareStatement(SELECT).executeQuery();
		){
			while(results.next()) {
				String ownerUsername = results.getString("OWNERUSERNAME");
				String name = results.getString("NAME");
				String description = results.getString("DESCRIPTION");
				LocalDate time = results.getDate("TIME").toLocalDate();
				double amount = results.getDouble("AMOUNT");
				double fees = results.getDouble("FEE");
				Transaction t = new Transaction(ownerUsername, name, description, time, amount, fees);
				targetList.add(t);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return FXCollections.observableList(targetList);
	}
	
	private ObservableList<Transaction> queryTransactionsFor(String ownerUsername) {
		List<Transaction> transactionSet = new ArrayList<Transaction>();
		final String SELECT = "SELECT * FROM TRANS WHERE OWNERUSERNAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(SELECT);		
		){
			s.setString(1, ownerUsername);
			ResultSet results = s.executeQuery();
			
			while(results.next()) {
				String ownerName = results.getString("OWNERUSERNAME");
				String name = results.getString("NAME");
				String description = results.getString("DESCRIPTION");
				LocalDate time = results.getDate("TIME").toLocalDate();
				double amount = results.getDouble("AMOUNT");
				double fees = results.getDouble("FEE");
				Transaction t = new Transaction(ownerName, name, description, time, amount, fees);
				transactionSet.add(t);
			}
			results.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return FXCollections.observableList(transactionSet);
		
	}
	
	// INSERTS
	
	private boolean insertTransaction(Transaction t) {
		boolean success = false;
		final String INSERT = "INSERT INTO TRANS(OWNERUSERNAME,NAME,TIME,DESCRIPTION,AMOUNT,FEE)"+
								"VALUES(?,?,?,?,?,?);";
		
		try (
			PreparedStatement s = c.prepareStatement(INSERT);		
		){
			s.setString(1, t.getOwnerName());
			s.setString(2, t.getName());
			s.setDate(3, Date.valueOf(t.getTime()));
			s.setString(4, t.getDescription());
			s.setDouble(5, t.getAmount());
			s.setDouble(6, t.getFee());
			success = s.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	private boolean insertTransactions(List<Transaction> transactions) {
		boolean success = true;
		for (Transaction t: transactions)
			success = insertTransaction(t);
		return success;
	}
	
	// UPDATES
	private boolean updateTransaction(String name, String attribute, String newValue) {
		boolean success = false;
		final String UPDATE = "UPDATE TRANS SET ?=? WHERE NAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(UPDATE);		
		){
			s.setString(1, attribute);
			s.setString(2, newValue);
			s.setString(3, name);
			success = s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}
	
	private boolean updateTransaction(String name, String attribute, Number newValue) {
		boolean success = false;
		final String UPDATE = "UPDATE TRANS SET ?=? WHERE NAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(UPDATE);		
		){
			s.setString(1, attribute);
			s.setDouble(2, newValue.doubleValue());
			s.setString(3, name);
			success = s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}
	
	private boolean updateTransaction(String name, String attribute, LocalDate newValue) {
		boolean success = false;
		final String UPDATE = "UPDATE TRANS SET ?=? WHERE NAME=?;";
		try (
			PreparedStatement s = c.prepareStatement(UPDATE);		
		){
			s.setString(1, attribute);
			s.setDate(2, Date.valueOf(newValue));
			s.setString(3, name);
			success = s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}
	
	// DELETIONS
	
	private boolean deleteTransaction(Transaction t) {
		boolean success = false;
		final String DELETE = "DELETE FROM TRANS WHERE NAME=?;";
		
		try (
			PreparedStatement s = c.prepareStatement(DELETE);
		) {
			s.setString(1, t.getName());
			success = s.execute();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return success;
	}
	
	private boolean deleteTransactions(List<Transaction> ts) {
		boolean success = true;
		for (Transaction t: ts)
			success = deleteTransaction(t);
		return success;
	}
	
	
/*--- ADMIN ---------------------------------------------------------------------------*/
	
	public boolean insertAdmin(CsAdmin admin) {
		final String INSERT = "INSERT INTO ADMIN(USERNAME, PASSWORD, FIRSTNAME, LASTNAME)"+
						  "VALUES(?,?,?,?);";
		
		try (
			PreparedStatement s = c.prepareStatement(INSERT);		
		) {
			s.setString(1, admin.getUsername());
			s.setString(2, admin.getPassword());
			s.setString(3, admin.getFirstName());
			s.setString(4, admin.getLastName());
			return s.execute();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public CsAdmin queryAdmin() {
		return new CsAdmin("csadmin", "csci323", "Robyn", "Berg");
	}
	
	private boolean updateAdmin(CsAdmin oldAdmin, CsAdmin newAdmin) {
		throw new UnsupportedOperationException("Updating the admin is not currently implemented");
	}
	
	
/*--- FATAL ZONE ---------------------------------------------------------------------------*/
	/**
	 * Drop all tables.
	 * @param areYouSure - must be "YES I AM SURE!" to perform action.
	 */
	public void deleteALL(String areYouSure) {
		if (!areYouSure.equals("YES I AM SURE!")) return;
		closeConnection();
		dbFile.delete();
	}
	
/*--- UNIT TESTING  ---------------------------------------------------------------------------*/
	public static void main( String args[] ) {
		Settings.init();
		DatabaseKit db = new DatabaseKit("test.db");
		ObservableList<User> users = db.getAllUsers();
		ObservableList<Transaction> trans = db.getAllTransactions();
		
		users.add(new User("testuser", "password", "mr", "mctest", 0));
		users.get(0).setFirstName("test1");
		
		
		trans.add(new Transaction("testuser", "test", "test", LocalDate.now(), 0, 0));
		trans.get(0).setDescription("test2");
		
		db.deleteALL("YES I AM SURE!");
		
		/*Settings.init();
		DatabaseKit db = new DatabaseKit("test.db");
		
		User p0 = new User("Trish", "Duce", 200.0),
				p1 = new User("Michael", "Cassens", 200.0),
				p2 = new User("Michael", "Cassens", 200.0),
				p3 = new User("Oliver", "Serang", 200.0),
				p4 = new User("Rob", "Smith", 200.0);
		db.insertProfiles(p0, p1, p2, p3, p4);
		
		List<Transaction> ts = new ArrayList<Transaction>();
		
		ts.add(new Transaction(LocalDate.now(), "Party hats", -1000.00, .12));
		ts.add(new Transaction(LocalDate.now(), "T-shirt Cannon", -100.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Donation", 15.00, .12));
		ts.clear();
		
		ts.add(new Transaction(LocalDate.now(), "Fireworks", -100.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Bouncy castle", -1000.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Donation", 50.00, .12));
		ts.clear();
		
		ts.add(new Transaction(LocalDate.now(), "Lots of erasors", -100.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Ghost masks", -20.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Donation", 2000.00, .12));
		ts.clear();
		
		ts.add(new Transaction(LocalDate.now(), "Penguin statue", -200.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Confetti", -20.00, .12));
		ts.add(new Transaction(LocalDate.now(), "Donation", 30.00, .12));
		ts.clear();
		
		CsAdmin admin = db.getAdmin();
		assert admin != null: admin;
		assert admin.getUsers() != null: admin.getUsers();
		assert !admin.getUsers().isEmpty(): admin.getUsers();
		assert admin.getUsers().stream().anyMatch(usr -> usr.getTransactions() == null): 
			admin.getUsers().stream().filter(usr -> usr.getTransactions() == null);
		assert admin.getUsers().stream().anyMatch(usr -> usr.getTransactions().isEmpty()): 
			admin.getUsers().stream().filter(usr -> usr.getTransactions().isEmpty());
		db.deleteALL("YES I AM SURE!");*/
	  }

}