/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package javafxmlapplication;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.converter.LocalDateStringConverter;

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
    @FXML
    private TextField passField;
    @FXML
    private Label passwordError;
    @FXML
    private TextField passwordField1;
    @FXML
    private Label passwordError2;
    @FXML
    private ImageView ImageView;
    @FXML
    private Button elegiravatar;
    
    private Image selectedAvatar;

    
   
       // PROPIEDADES DE VALIDACIÓN
    private BooleanProperty validEmail;
    private BooleanProperty validNickname;
    private BooleanProperty validPassword;
    private BooleanProperty validPassword2;
    private BooleanProperty validDate;

    // LISTENERS
    private ChangeListener<String> listenerNickname;
    private ChangeListener<String> listenerEmail;
    private ChangeListener<String> listenerPassword;
    private ChangeListener<String> listenerPassword2;
    private ChangeListener<String> listenerDate;



    // =========================================================
    // VALIDACIONES
      

    @FXML
    private void eleccAv(ActionEvent event) {
        List<String> options = List.of("Elegir imagen propia", "Elegir avatar del sistema", "Cancelar");

    ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
    dialog.setTitle("Seleccionar avatar");
    dialog.setHeaderText("Escoge una opción:");

    String result = dialog.showAndWait().orElse("Cancelar");

    if (result.equals("Elegir imagen propia")) {
        chooseUserAvatar();
    } 
    else if (result.equals("Elegir avatar del sistema")) {
        choosePredefinedAvatar();
    }
    }
    
    private void chooseUserAvatar() {
    FileChooser chooser = new FileChooser();
    chooser.setTitle("Seleccionar imagen");
    chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
    );

    File file = chooser.showOpenDialog(null);

    if (file != null) {
        try {
            Image img = new Image(new FileInputStream(file));
            selectedAvatar = img;
            ImageView.setImage(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    private void choosePredefinedAvatar() {

    List<String> avatars = List.of(
            "/avatars/avatar1.png",
            "/avatars/avatar2.png",
            "/avatars/avatar3.png"
    );

    ChoiceDialog<String> dialog = new ChoiceDialog<>(avatars.get(0), avatars);
    dialog.setTitle("Avatares del sistema");
    dialog.setHeaderText("Selecciona tu avatar:");

    String chosen = dialog.showAndWait().orElse(null);

    if (chosen != null) {
        Image img = new Image(getClass().getResourceAsStream(chosen));
        selectedAvatar = img;
        ImageView.setImage(img);
    }
}


    private void checkNickname() {
        String nick = userField.getText();

        boolean isValid = User.checkNickName(nick);

        Navigation nav = Navigation.getSingletonNavigation();

        if (!isValid) {
            nicknameError.setText("Nickname no válido.");
        } 
        else if (nav.existsNickName(nick)) {
            isValid = false;
            nicknameError.setText("El nombre de usuario ya existe.");
        } 
        else {
            nicknameError.setText("");
        }

        validNickname.set(isValid);
        showError(isValid, userField, nicknameError);
    }

    private void checkEmail() {
        boolean isValid = User.checkEmail(emailField.getText());

        validEmail.set(isValid);
        showError(isValid, emailField, emailError);
    }

    private void checkPassword() {
        boolean isValid = User.checkPassword(passField.getText());

        validPassword.set(isValid);
        showError(isValid, passField, passwordError);
    }

    private void checkPassword2() {
        boolean match = passField.getText().equals(passwordField1.getText());

        validPassword2.set(match);
        showError(match, passwordField1, passwordError2);
    }

    private void checkDate() {
        LocalDate value = dateField.getValue();
        boolean isValid = false;

        if (value != null) {
            isValid = value.isBefore(LocalDate.now().minus(16, YEARS));
        }

        validDate.set(isValid);
        showError(isValid, dateField, birthError);
    }

    private void showError(boolean isValid, Node field, Node errorMessage) {
        errorMessage.setVisible(!isValid);
        field.setStyle(isValid ? "" : "-fx-background-color: #FCE5E0");
    }



    // =========================================================
    // INITIALIZE
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        validNickname = new SimpleBooleanProperty(false);
        validEmail = new SimpleBooleanProperty(false);
        validPassword = new SimpleBooleanProperty(false);
        validPassword2 = new SimpleBooleanProperty(false);
        validDate = new SimpleBooleanProperty(false);
        
        selectedAvatar = new Image(getClass().getResourceAsStream("/avatars/default.png"));
        ImageView.setImage(selectedAvatar);


        // ---------------- NICKNAME ----------------
        userField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkNickname();
                if (!validNickname.get() && listenerNickname == null) {
                    listenerNickname = (a, b, c) -> checkNickname();
                    userField.textProperty().addListener(listenerNickname);
                }
            }
        });


        // ---------------- EMAIL ----------------
        emailField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkEmail();
                if (!validEmail.get() && listenerEmail == null) {
                    listenerEmail = (a, b, c) -> checkEmail();
                    emailField.textProperty().addListener(listenerEmail);
                }
            }
        });


        // ---------------- PASSWORD 1 ----------------
        passField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkPassword();
                if (!validPassword.get() && listenerPassword == null) {
                    listenerPassword = (a, b, c) -> checkPassword();
                    passField.textProperty().addListener(listenerPassword);
                }
            }
        });


        // ---------------- PASSWORD 2 ----------------
        passwordField1.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkPassword2();
                if (!validPassword2.get() && listenerPassword2 == null) {
                    listenerPassword2 = (a, b, c) -> checkPassword2();
                    passwordField1.textProperty().addListener(listenerPassword2);
                }
            }
        });


        // ---------------- DATE PICKER ----------------
        dateField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkDate();
                if (!validDate.get() && listenerDate == null) {
                    listenerDate = (a, b, c) -> checkDate();
                    dateField.getEditor().textProperty().addListener(listenerDate);
                }
            }
        });

        // Converter para DatePicker
        dateField.setConverter(new LocalDateStringConverter());


        // BOTÓN ACEPTAR — habilitado solo si todo es válido
        BooleanBinding validFields =
                validNickname
                .and(validEmail)
                .and(validPassword)
                .and(validPassword2)
                .and(validDate);

        bAccept.disableProperty().bind(Bindings.not(validFields));


        // BOTÓN CANCELAR
        bCancel.setOnAction(event -> bCancel.getScene().getWindow().hide());
    }



    // =========================================================
    // ACEPTAR → registrar usuario
    @FXML
    private void handleBAcceptOnAction(ActionEvent event) throws NavDAOException {

        String nick = userField.getText();
        String email = emailField.getText();
        String pass = passField.getText();
        LocalDate birth = dateField.getValue();

        // Avatar por defecto
        Image avatar = selectedAvatar;

        Navigation nav = Navigation.getSingletonNavigation();
        User newUser = nav.registerUser(nick, email, pass, avatar, birth);

        System.out.println("Usuario registrado: " + newUser);

        // Resetear formulario
        userField.clear();
        emailField.clear();
        passField.clear();
        passwordField1.clear();
        dateField.setValue(null);

        validNickname.set(false);
        validEmail.set(false);
        validPassword.set(false);
        validPassword2.set(false);
        validDate.set(false);
    }
}


