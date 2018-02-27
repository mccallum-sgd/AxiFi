package control;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import model.Settings.Setting;

public class SettingsController extends Controller {
	
	@FXML private BorderPane root;
	@FXML private AnchorPane sidePane;
	@FXML private StackPane mainPane;
	@FXML private ListView<Setting> settingsList;
	
	private List<AnchorPane> settingPanes;
	
	
/*--- SETUP ---------------------------------------------------------------------------*/	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@Override
	public void receiveData(Object... data) {
		// TODO Auto-generated method stub
		
	}
	
/*--- HELPERS ---------------------------------------------------------------------------*/	
	
/*--- FXML ---------------------------------------------------------------------------*/	

	

}
