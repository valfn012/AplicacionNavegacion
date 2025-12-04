/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author LERI
 */
public class FXMLRegisterController implements Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private Label emailError;
    @FXML
    private TextField passwordField;
    @FXML
    private Label passError;
    @FXML
    private DatePicker dateField;
    @FXML
    private Label birthError;
    @FXML
    private Button bAccept;
    @FXML
    private ImageView bCancel;
    @FXML
    private TextField userField;
    @FXML
    private Label nicknameError;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
