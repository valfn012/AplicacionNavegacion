

package AplicacionNavegacion;

import java.net.URL;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.YEARS;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.util.converter.LocalDateStringConverter;
import model.NavDAOException;

import model.Navigation;
import model.User;

public class FXMLRegisterController implements Initializable {

    @FXML private Label emailError;
    @FXML private TextField emailField;

    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordField2;

    @FXML private TextField userField;

    @FXML private Label passwordError;
    @FXML private Label passwordError2;
    @FXML private Label nicknameError;
    @FXML private Label dateError;

    @FXML private Button bAccept;
    @FXML private Button bCancel;

    @FXML private DatePicker dateField;


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
    private void checkNickname() {
        String nick = userField.getText();

        boolean isValid = User.checkNickName(nick);

        Navigation nav = Navigation.

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
        boolean isValid = User.checkPassword(passwordField.getText());

        validPassword.set(isValid);
        showError(isValid, passwordField, passwordError);
    }

    private void checkPassword2() {
        boolean match = passwordField.getText().equals(passwordField2.getText());

        validPassword2.set(match);
        showError(match, passwordField2, passwordError2);
    }

    private void checkDate() {
        LocalDate value = dateField.getValue();
        boolean isValid = false;

        if (value != null) {
            isValid = value.isBefore(LocalDate.now().minus(16, YEARS));
        }

        validDate.set(isValid);
        showError(isValid, dateField, dateError);
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
        passwordField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkPassword();
                if (!validPassword.get() && listenerPassword == null) {
                    listenerPassword = (a, b, c) -> checkPassword();
                    passwordField.textProperty().addListener(listenerPassword);
                }
            }
        });


        // ---------------- PASSWORD 2 ----------------
        passwordField2.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                checkPassword2();
                if (!validPassword2.get() && listenerPassword2 == null) {
                    listenerPassword2 = (a, b, c) -> checkPassword2();
                    passwordField2.textProperty().addListener(listenerPassword2);
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
        String pass = passwordField.getText();
        LocalDate birth = dateField.getValue();

        // Avatar por defecto
        Image avatar = new Image(
                getClass().getResourceAsStream("/avatars/default.png")
        );

        Navigation nav = Navigation.getSingletonNavigation();
        User newUser = nav.registerUser(nick, email, pass, avatar, birth);

        System.out.println("Usuario registrado: " + newUser);

        // Resetear formulario
        userField.clear();
        emailField.clear();
        passwordField.clear();
        passwordField2.clear();
        dateField.setValue(null);

        validNickname.set(false);
        validEmail.set(false);
        validPassword.set(false);
        validPassword2.set(false);
        validDate.set(false);
    }
}
