package control;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.Manager.Stages;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Settings;
import model.Settings.Setting;
import model.Settings.SettingCategory;

public class SettingsController extends Controller {
	
	@FXML private BorderPane root;
	@FXML private AnchorPane sidePane;
	@FXML private StackPane mainPane;
	@FXML private ListView<SettingCategory> settingsList;
	
	private AnchorPane currentPane; // For easy access during population
	
	
/*--- SETUP ---------------------------------------------------------------------------*/	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		populateSettingsList(Settings.getSettingCategories());
	}
	
	@Override
	public void receiveData(Object... data) {
		// TODO Auto-generated method stub
		
	}
	
/*--- HELPERS ---------------------------------------------------------------------------*/	
	
	private void populateSettingsList(List<SettingCategory> categories) {
		settingsList.setItems(FXCollections.observableList(categories));
		settingsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null)
				populateSettingCategoryView(newValue);
			else clearSettingCategoryView();
		});
	}
	
	private void populateSettingCategoryView(SettingCategory category) {
		// pane
		AnchorPane pane = new AnchorPane();
		pane.setUserData(category.getName()); // for finding later
		mainPane.getChildren().add(pane);
		currentPane = pane;
		// header
		Label headerLbl = new Label(category.getName());
		headerLbl.getStyleClass().add("label-header");
		pane.getChildren().add(headerLbl);
		// layout pane
		for (Setting setting: category.getSettings()) {
			String originalValue = Formatter.capitalize(setting.getStringValue());
			Label lbl = new Label(Formatter.capitalize(setting.getName()));
			switch (setting.getType()) {
				case FIELD:
					TextField field = new TextField();
					field.setOnAction(a -> {
						String input = field.getText().trim();
						if (new Validation<TextField>(field, fld -> input.isEmpty(), 
								fld -> field.setText(originalValue)).run())
							setting.setStringValue(input);
					});
					addPair(lbl, field);
					break;
				case PATH:
					TextField pathFld = new TextField();
					pathFld.setPromptText("Path to file...");
					
					break;
					
			}
		}
	}
	
	private VBox addPair(Node... ns) {
		VBox box = new VBox();
		box.getChildren().addAll(ns);
		currentPane.getChildren().add(box);
		return box;
	}
	
	private HBox addRow(Node... ns) {
		HBox box = new HBox();
		box.getChildren().addAll(ns);
		currentPane.getChildren().add(box);
		return box;
	}
	
	private void clearSettingCategoryView() {
		mainPane.getChildren().clear();
	}
	
/*--- FXML ---------------------------------------------------------------------------*/	

	@FXML
	private void exit() {
		manager.close(Stages.SETTINGS);
	}
	

}
