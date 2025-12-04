/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplicacionNavegacion;

import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.ChronoUnit.YEARS;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.converter.LocalDateStringConverter;

public class FXMLRegisterController implements Initializable {
//holaaa he editado este
    @FXML
    private Label emailError;
    @FXML
    private TextField emailField;
 
    //properties to control valid fields values. 
    private BooleanProperty validEmail;
    private BooleanProperty validPassword;
    private BooleanProperty validPassword2;
    private BooleanProperty validDate;

 
    
    // listener to register on textProperty() or valueProperty()
    private ChangeListener<String> listenerEmail;
    private ChangeListener<String> listenerPassword;
    private ChangeListener<String> listenerPassword2;
    private ChangeListener<String> listenerDate;

     @FXML
    private PasswordField passwordField;
    @FXML
    private Button bAccept;
    @FXML
    private TextField passField;
    @FXML
    private TextField userField;
    @FXML
    private Label passwordError;
    @FXML
    private Label nicknameError;
    @FXML
    private Label dateError;
    @FXML
    private Button bCancel;
    @FXML
    private PasswordField passwordField2;
      @FXML
    private Label passwordError2;
    @FXML
    private DatePicker dateField;



    
    private void checkEmail() {
        String email = emailField.getText();
//        boolean isValid = email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        boolean isValid = email.matches("^[\\w!#$%&'*+/=?⁠ {|}~^-]+(?:\\.[\\w!#$%&'*+/=? ⁠{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        validEmail.set(isValid); //actualiza la property asociada
        showError(isValid, emailField, emailError); //muestra o esconde el mensaje de error
    }

    private void showError(boolean isValid, Node field, Node errorMessage) {
        errorMessage.setVisible(!isValid);
        field.setStyle(((isValid) ? "" : "-fx-background-color: #FCE5E0"));
    }

    //=========================================================
    // you must initialize here all related with the object 
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        validEmail = new SimpleBooleanProperty(false);

        //When the field loses focus, the field is validated. 
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // el foco se pierde, el usuario sale del campo 
                checkEmail(); // la funcion define si el email es correcto o no y cambia validEmail a true o false
                if (!validEmail.get()) { // si validEmail sigue en false
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
                    if (listenerEmail == null) {
                        listenerEmail = (a, b, c) -> checkEmail();
                        emailField.textProperty().addListener(listenerEmail);
                    }
                }
            }
        });

        validPassword = new SimpleBooleanProperty(false);

        //When the field loses focus, the field is validated. 
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkPassword();
                if (!validPassword.get()) {
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
                    if (listenerPassword == null) {
                        listenerPassword = (a, b, c) -> checkPassword();
                        passwordField.textProperty().addListener(listenerPassword);
                    }
                }
            }
        });

        validPassword2 = new SimpleBooleanProperty(false);

        //When the field loses focus, the field is validated. 
        passwordField2.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkPassword2();
                if (!validPassword2.get()) {
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
                    if (listenerPassword2 == null) {
                        listenerPassword2 = (a, b, c) -> checkPassword2();
                        passwordField2.textProperty().addListener(listenerPassword2);
                    }
                }
            }
        });

        validDate = new SimpleBooleanProperty(false);

        //When the field loses focus, the field is validated. 
        dateField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // el foco se pierde, el usuario sale del campo 
                checkDate(); // la funcion define si el email es correcto o no y cambia validEmail a true o false
                if (!validDate.get()) { // si validEmail sigue en false
                    //If it is not correct, a listener is added to the text or value 
                    //so that the field is validated while it is being edited.
                    if (listenerDate == null) {
                        listenerDate = (a, b, c) -> checkDate();
                        dateField.getEditor().textProperty().addListener(listenerDate);
                    }
                }
            }
        });

        LocalDateStringConverter localDateStringConverter = new LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String value) {
                try {
                    return super.fromString(value);
                } catch (Exception e) {
                    System.out.println("Exception in fromString");
                    return LocalDate.now();
                }
            }

            @Override
            public String toString(LocalDate value) {
                return super.toString(value);
            }
        };
        dateField.setConverter(localDateStringConverter);

        
        
        BooleanBinding validFields = Bindings.and(validEmail, validPassword)
                .and(validPassword2)
                .and(validDate);
        
        bAccept.disableProperty().bind(
                Bindings.not(validFields)
        );
        
        
        
        bCancel.setOnAction((event) -> {
            bCancel.getScene().getWindow().hide();
        });
    }

    private void checkPassword() {
        String password = passwordField.getText();
        boolean isValid = password.matches("^(?=.[0-9])(?=.[a-zA-Z]).{8,15}$");
        validPassword.set(isValid); //actualiza la property asociada
        showError(isValid, passwordField, passwordError); //muestra o esconde el mensaje de error
    }

    private void checkPassword2() {
        boolean match = passwordField.getText().equals(passwordField2.getText());
        validPassword2.set(match);
        showError(match, passwordField2, passwordError2);

    }

    private void checkDate() {
        LocalDate value = dateField.getValue();
        boolean isValid = value.isBefore(LocalDate.now().minus(16, YEARS));
        validDate.set(isValid);
        showError(isValid, dateField, dateError);

    }

    @FXML
    private void handleBAcceptOnAction(ActionEvent event) {
        emailField.clear();
        passwordField.clear();
        passwordField2.clear();
        dateField.setValue(null);

        validEmail.setValue(Boolean.FALSE);
        validPassword.setValue(Boolean.FALSE);
        validPassword2.setValue(Boolean.FALSE);
        validDate.setValue(Boolean.FALSE);
    }

}