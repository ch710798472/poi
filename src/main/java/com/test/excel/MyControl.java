package com.test.excel;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author banmo
 * @create 2018-05-10
 **/
@FXMLController
public class MyControl implements Initializable {
    @FXML
    public Button downloadExecel;

    @FXML
    private Button myButton;

    @FXML
    private TextField myTextField;

    @FXML
    private Button uploadExecel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showDateTime(ActionEvent event) {
        System.out.println("Button Clicked!");

        Date now= new Date();

        DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");
        String dateTimeString = df.format(now);
        // Show in VIEW
        myTextField.setText(dateTimeString);

    }

    public void uploadingExe(ActionEvent event) {
        System.out.println("uploding button Clicked!");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        //File file = fileChooser.showOpenDialog(primaryStage);
        //System.out.println(file);
    }
}
