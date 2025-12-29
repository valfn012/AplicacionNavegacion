package AplicacionNavegacion;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.YEARS;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import model.NavDAOException;
import model.Navigation;
import model.User;

import javafx.scene.Scene;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;

import javafx.event.ActionEvent;

public class VentanaRegistroController implements Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private DatePicker dateField;

    @FXML
    private Button bAccept;
    @FXML
    private Button bCancel;
    @FXML
    private Button elegirAvatar;

    @FXML
    private ImageView avatarImage;

    private Image chosenAvatar;

    private File chosenAvatarFile;

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String msg) {
        showAlert("Errores en el formulario", msg, Alert.AlertType.ERROR);
    }

    private String validateForm() {
        StringBuilder errors = new StringBuilder();

        // limpiar estilos previos
        clearInvalid(userField);
        clearInvalid(emailField);
        clearInvalid(passwordField);
        clearInvalid(passwordField2);
        clearInvalid(dateField);

        // nickname
        String nick = userField.getText();
        if (nick.isEmpty() || !User.checkNickName(nick)) {
            errors.append("• Nickname inválido.\n");
            markAsInvalid(userField);
        } else {
            try {
                if (Navigation.getInstance().exitsNickName(nick)) {
                    errors.append("• El nickname ya existe.\n");
                    markAsInvalid(userField);
                }
            } catch (NavDAOException e) {
                errors.append("• Error al comprobar nickname.\n");
                markAsInvalid(userField);
            }
        }

        // email
        String email = emailField.getText();
        if (email.isEmpty() || !User.checkEmail(email)) {
            errors.append("• Email inválido.\n");
            markAsInvalid(emailField);
        }

        // contraseña 1
        String pass1 = passwordField.getText();
        if (pass1.isEmpty() || !User.checkPassword(pass1)) {
            errors.append("• Contraseña inválida.\n");
            markAsInvalid(passwordField);
        }

        // contraseña 2
        String pass2 = passwordField2.getText();
        if (pass2.isEmpty() || !pass1.equals(pass2)) {
            errors.append("• Las contraseñas no coinciden.\n");
            markAsInvalid(passwordField2);
        }

        // fecha nacimiento
        LocalDate birth = dateField.getValue();
        if (birth == null || !birth.isBefore(LocalDate.now().minusYears(16))) {
            errors.append("• Debes tener al menos 16 años.\n");
            markAsInvalid(dateField);
        }

        return errors.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // otros ajustes que ya tengas...
        userField.textProperty().addListener((o, ov, nv) -> clearInvalid(userField));
        emailField.textProperty().addListener((o, ov, nv) -> clearInvalid(emailField));
        passwordField.textProperty().addListener((o, ov, nv) -> clearInvalid(passwordField));
        passwordField2.textProperty().addListener((o, ov, nv) -> clearInvalid(passwordField2));
        dateField.valueProperty().addListener((o, ov, nv) -> clearInvalid(dateField));
    }

    @FXML
    private void handleBAcceptOnAction(ActionEvent event) {

        String errors = validateForm();

        if (!errors.isEmpty()) {
            showError(errors);
            return;
        }

        try {
            Navigation nav = Navigation.getInstance();
            nav.registerUser(
                    userField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    chosenAvatar,
                    dateField.getValue()
            );

            showAlert("¡Registro completado!", "Usuario registrado correctamente.", Alert.AlertType.INFORMATION);

            goToLogin(event);

        } catch (Exception e) {
            showError("Error al registrar el usuario.");
        }
    }

    private void goToLogin(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChooseAvatar(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaAvatarFXML.fxml"));
            Parent root = loader.load();

            AvatarChooserController controller = loader.getController();
            controller.setParentController(this);

            Stage modal = new Stage();
            modal.setTitle("Elegir avatar");
            modal.setScene(new Scene(root));
            modal.setResizable(false);

            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setChosenAvatar(Image img, File file) {
        chosenAvatar = img;
        chosenAvatarFile = file;
        avatarImage.setImage(img);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setResizable(false);
            stage.setMaximized(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR: No se pudo cargar FXMLinisesion.fxml");
        }
    }

    public void clearAvatarIfDeleted(File deletedFile) {

        if (chosenAvatarFile == null) {
            return;
        }

        if (chosenAvatarFile.equals(deletedFile)) {

            chosenAvatarFile = null;
            chosenAvatar = new Image(getClass().getResourceAsStream("/resources/default.png"));

            avatarImage.setImage(chosenAvatar);
        }
    }

    private void markAsInvalid(PasswordField pf) {
        pf.setStyle("-fx-control-inner-background: red; -fx-background-insets: 0;");
    }

    private void clearInvalid(PasswordField pf) {
        pf.setStyle("");
    }

    private void markAsInvalid(DatePicker dp) {
        dp.getEditor().setStyle("-fx-control-inner-background: red; -fx-background-insets: 0;");
    }

    private void clearInvalid(DatePicker dp) {
        dp.getEditor().setStyle("");
    }

    private void markAsInvalid(TextField tf) {
        tf.setStyle("-fx-background-color: #ffb3b3; -fx-background-insets: 0;");
    }

    private void clearInvalid(TextField tf) {
        tf.setStyle("");
    }
}
