package control;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import application.Manager.Stages;
import application.Manager.Views;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import model.Autocomplete;
import model.Term;
import model.Transaction;
import model.User;

public class MainController extends Controller {
	
	@FXML private BorderPane root;
    @FXML private TableView<Transaction> table;
    
    @FXML private TableColumn<Transaction, String> userCol, dateCol, amountCol, 
    									descripCol, feeCol;

    @FXML private Label balanceLbl, feesLbl;
    
    @FXML private MenuButton accMenuBtn;
    @FXML private MenuItem codes, print, exit, userGuide, allItm;
    @FXML private Button newAccBtn, editAccBtn, delAccBtn, logoutBtn, newTransBtn, editTransBtn;
    @FXML private TextField searchFld;
    
    private Autocomplete ac;
    
    private ListChangeListener<User> usersListener;
    private ListChangeListener<Transaction> tableListener;
/*--- SETUP ---------------------------------------------------------------------------*/	
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	// setup keyboard commands
    	table.setOnKeyPressed(keyEvt -> {
    		switch (keyEvt.getCode()) {
	    		case ENTER:
	    			if (!editTransBtn.isDisabled())
	    				editTransactions();
	    			break;
    			case DELETE:
	    			db.getAllTransactions().removeAll(table.getSelectionModel().getSelectedItems());
    				break;
    			default:
    				break;
    		}
    	});
    	setupFadeIn(root);
    	setupOnShow(root, (obs, oldScene, newScene) -> {
    		if (newScene != null) refresh();
    	});
    	usersListener = new ListChangeListener<User>() {
			@Override
			public void onChanged(Change<? extends User> c) {
				while (c.next()) {
					if (c.wasRemoved()) {
						List<User> removed = (List<User>) c.getRemoved();
						for (User r: removed)
							accMenuBtn.getItems().removeIf(a -> a.getText().equals(r.getFullName()));
						switchAccounts(null);
						refresh();
					}
					if (c.wasAdded()) {
						addAccounts(false, (List<User>) c.getAddedSubList());
						refresh();
					}
				}
			}
		};
		tableListener = new ListChangeListener<Transaction> () {
			@Override
			public void onChanged(Change<? extends Transaction> c) {
				while (c.next())
					if (c.wasAdded() || c.wasRemoved())
						refresh();
			}
		};
	}
    
    /**
     * Requires only one object of one of the following types:
     * Transaction.
     * 
     * @throws IllegalArgumentException
     * - if the parameters do not conform to the requirements
     * above
     */
	@Override
	public void receiveData(Object... data) {
		Class<?> type = checkData(data);
		if (type.equals(Transaction.class)) {
			table.getItems().add((Transaction) data[0]);
		} else
			throw new IllegalArgumentException("Requires only one object of one of the following types: Transaction.");
	}
	
/*--- HELPERS ---------------------------------------------------------------------------*/	
	
    // Validates data coming in
    private Class<?> checkData(Object[] data) {
		if (data.length == 1 && data[0] instanceof Transaction)
			return Transaction.class;
		else
			throw new IllegalArgumentException("Requires only one object of one of the following types: Transaction.");
	}
    
    public void refresh() {
		showAccounts();
		table.refresh();
    }
	
    // Add accounts to menu button and setup changelisteners
	private void showAccounts() {
		db.get.removeListener(usersListener);
		admin.queryUsers().addListener(usersListener);
		if (!admin.queryUsers().isEmpty())
			accMenuBtn.getItems().clear();
		addAccounts(true, admin.queryUsers());
		if (currAcc == null) {
			accMenuBtn.setText(allItm.getText());
			switchAccounts(null);
		} else switchAccounts(currAcc);
	}
	
	// Switch data & UI
	private void switchAccounts(User user) {
		if (user != null) {
			enable(true, delAccBtn, editAccBtn, newTransBtn, editTransBtn);
	    	accMenuBtn.setText(user.getFullName());
	    	currAcc = user;
	    	showTransactions();
		} else {
			enable(false, delAccBtn, editAccBtn, newTransBtn, editTransBtn);
			currAcc = null;
			accMenuBtn.setText(allItm.getText());
			showTransactions();
		}
    }
	
	// Add buttons
	private void addAccounts(boolean clear, List<User> accs) {
		if (clear) accMenuBtn.getItems().clear();
		if (!accMenuBtn.getItems().contains(allItm)) {
			allItm.setOnAction(e -> switchAccounts(null));
			accMenuBtn.getItems().add(allItm);
		}
		// Add accounts
		List<MenuItem> items = new ArrayList<MenuItem>();
		accs.forEach(user -> {
			MenuItem item = new MenuItem();
			item.setText(user.getFullName());
			item.setOnAction(e -> switchAccounts(user));
			items.add(item);
		});
		accMenuBtn.getItems().addAll(items);
	}
	
	// Set table items
	private void showTransactions() {
		if (currAcc != null) {
			table.setItems(currAcc.queryTransactions());
			userCol.setVisible(false);
		} else {
			table.setItems(db.queryTransactions()); //Overview
			userCol.setVisible(true);
			userCol.setCellValueFactory(cellData -> new SimpleStringProperty(db.getOwner(cellData.getValue()).getFullName()));
		}
		table.getItems().removeListener(tableListener);
		table.getItems().addListener(tableListener);
		dateCol.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        amountCol.setCellValueFactory(cellData -> cellData.getValue().formattedAmountProperty());
        descripCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        feeCol.setCellValueFactory(cellData -> cellData.getValue().getFormattedFeesProperty());
        buildAutocomplete();
	}
	
	//TODO - search and filter table items while typing in search field
	private void buildAutocomplete() {
		List<String> temp = new ArrayList<String>();
		for (Transaction t: table.getItems())
			temp.add(t.getDescription());
		
		Term[] descs = new Term[temp.size()];
		for (int i = 0; i < descs.length; i++)
			descs[i] = new Term(temp.get(i), 0);
		ac = new Autocomplete(descs);
	}
	
/*--- FXML ---------------------------------------------------------------------------*/	
	
	@FXML
	private void codes(ActionEvent e) {
		manager.show(Stages.CODES, Views.CODES);
	}
    
    @FXML
    private void createAccount(ActionEvent e) {
    	if (admin != null)
    		manager.showSendData(Stages.NEW_ACC, Views.NEW_ACC, admin);
    }
    
    @FXML
    private void editAccount(ActionEvent e) {
    	if (admin != null && currAcc != null)
    		manager.showSendData(Stages.NEW_ACC, Views.NEW_ACC, admin, currAcc);
    }
    
    @FXML
    private void deleteAccount(ActionEvent e) {
    	if (admin != null && currAcc != null)
    		manager.showSendData(Stages.DEL_ACC, Views.DEL_ACC, admin, currAcc);
    }
    
    @FXML
    private void showCodes(ActionEvent e) {
    	manager.show(Stages.CODES, Views.CODES);
    }
    
    @FXML
    private void newTransaction(ActionEvent e) {
    	if (currAcc != null)
    		manager.showSendData(Stages.TRANS, Views.TRANS, currAcc);
    }
    
    @FXML
    private void editTransactions() {
    	if (table.getSelectionModel() != null && !table.getSelectionModel().isEmpty())
    		manager.showSendData(Stages.TRANS, Views.TRANS, table.getSelectionModel(), currAcc);
    }
    
    @FXML
    private void logout(ActionEvent e) {
    	manager.showClose(Stages.LOGIN, Views.LOGIN, Stages.MAIN);
    }
    
    @FXML
	private void exit(ActionEvent e) {
		System.exit(0);
	}
    
    @FXML
    private void search(ActionEvent e) {
    	if (searchFld.getText() != "") {
    		Term[] matches = ac.allMatches(searchFld.getText());
    		if (matches.length > 0) {
    			Transaction t = table.getItems().stream().filter(trans -> trans.getDescription().equals(matches[0].getQuery())).findFirst().orElse(null);
    			table.getSelectionModel().clearSelection();
    			table.getSelectionModel().select(t);
    		} else JOptionPane.showMessageDialog(null, "No transaction found. \n(case-sensitive, prefix matching only)");
    	}
    }
    
    /**
     *  @throws IllegalStateException, SecurityException 
     */
    @FXML
	public void print() {
		Printer printer = Printer.getDefaultPrinter();
		PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,
				Printer.MarginType.HARDWARE_MINIMUM);
		PrinterJob job = PrinterJob.createPrinterJob();
		SwingUtilities.invokeLater(() -> {
			if (job != null && job.showPrintDialog(table.getScene().getWindow()) && job.printPage(pageLayout, table))
				job.endJob();
		});
		
	}

}