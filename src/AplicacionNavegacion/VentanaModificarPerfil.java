package AplicacionNavegacion;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.YEARS;
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
import javafx.stage.Stage;

import model.User;

public class VentanaModificarPerfil implements Initializable {

    // ---------- FXML ----------
    @FXML private TextField emailField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordField2;
    @FXML private DatePicker dateField;

    @FXML private Button bAccept;
    @FXML private Button bCancel;
    @FXML private Button elegirAvatar;

    @FXML
    private ImageView avatarImage;

    // ---------- AVATAR ----------
    private Image chosenAvatar;
    private File chosenAvatarFile;

    // ---------- USUARIO ACTIVO ----------
    private User activeUser;

    // =========================================================
    // ALERTAS
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

    // =========================================================
    // MÉTODO QUE LLAMA VentanaMapaController
    public void setUser(User user) {
        this.activeUser = user;

        if (user == null) return;

        // Nick (no editable)
        userField.setText(user.getNickName());
        userField.setDisable(true);

        // Email
        emailField.setText(user.getEmail());

        // Fecha de nacimiento
        dateField.setValue(user.getBirthdate());

        // Contraseña
        passwordField.setText(user.getPassword());
        passwordField2.setText(user.getPassword());

        // Avatar
        Image avatar = user.getAvatar();
        if (avatar != null) {
            chosenAvatar = avatar;
            avatarImage.setImage(avatar);
        }
    }

    // =========================================================
    // VALIDACIÓN
    private String validateForm() {

        StringBuilder errors = new StringBuilder();

        // (NO validamos nickname porque no se puede cambiar)

        // EMAIL
        String email = emailField.getText();
        if (email.isEmpty()) {
            errors.append("• Debes introducir un email.\n");
        } else if (!User.checkEmail(email)) {
            errors.append("• El email debe tener un formato válido (ejemplo: usuario@correo.com).\n");
        }

        // CONTRASEÑA
        String pass1 = passwordField.getText();
        if (pass1.isEmpty()) {
            errors.append("• Debes introducir una contraseña.\n");
        } else if (!User.checkPassword(pass1)) {
            errors.append("• La contraseña debe tener 8-20 caracteres, incluir mayúsculas, minúsculas, números y símbolos.\n");
        }

        // REPETIR CONTRASEÑA
        String pass2 = passwordField2.getText();
        if (pass2.isEmpty()) {
            errors.append("• Debes repetir la contraseña.\n");
        } else if (!pass1.equals(pass2)) {
            errors.append("• Las contraseñas no coinciden.\n");
        }

        // FECHA NAC
        LocalDate birth = dateField.getValue();
        if (birth == null) {
            errors.append("• Debes seleccionar tu fecha de nacimiento.\n");
        } else if (!birth.isBefore(LocalDate.now().minus(16, YEARS))) {
            errors.append("• Debes tener al menos 16 años.\n");
        }

        return errors.toString();
    }

    // =========================================================
    // INITIALIZE
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Nick no editable
        userField.setDisable(true);
        userField.setEditable(false);

        // Imagen por defecto (si el usuario no tiene avatar)
        chosenAvatar = new Image(getClass().getResourceAsStream("/resources/default.png"));
        avatarImage.setImage(chosenAvatar);
        avatarImage.setFitWidth(200);
        avatarImage.setFitHeight(200);
        avatarImage.setPreserveRatio(true);

        // Botón cancelar vuelve al mapa
        bCancel.setOnAction(this::goToMap);
    }

    // =========================================================
    // ACEPTAR: MODIFICA EL PERFIL DEL USUARIO
    @FXML
    private void handleBAcceptOnAction(ActionEvent event) {

        String errors = validateForm();

        if (!errors.isEmpty()) {
            showError(errors);
            return;
        }

        if (activeUser == null) {
            showError("No hay ningún usuario cargado.");
            return;
        }

        try {
            // Estos setters YA guardan en BD internamente (llaman a save())
            activeUser.setEmail(emailField.getText());
            activeUser.setPassword(passwordField.getText());
            activeUser.setBirthdate(dateField.getValue());

            if (chosenAvatar != null) {
                activeUser.setAvatar(chosenAvatar);
            }

            showAlert("Cambio completado", "Usuario modificado correctamente.",
                      Alert.AlertType.INFORMATION);

            goToMap(event);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error al modificar el usuario.");
        }
    }

    // =========================================================
    // IR A MAPA
    private void goToMap(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLVentanaMapa.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // ABRIR VENTANA "ELEGIR AVATAR"
    @FXML
    private void handleChooseAvatar(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaAvatarFXML.fxml"));
            Parent root = loader.load();

            AvatarChooserController controller = loader.getController();
            controller.setParentController(this); // conexión

            Stage modal = new Stage();
            modal.setTitle("Elegir avatar");
            modal.setScene(new Scene(root));
            modal.setResizable(false);

            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // MÉTODO LLAMADO DESDE LA VENTANA DE AVATAR
    public void setChosenAvatar(Image img, File file) {
        chosenAvatar = img;
        chosenAvatarFile = file;
        avatarImage.setImage(img);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        goToMap(event);
    }

    public void clearAvatarIfDeleted(File deletedFile) {

        if (chosenAvatarFile == null) return;

        if (chosenAvatarFile.equals(deletedFile)) {
            chosenAvatarFile = null;
            chosenAvatar = new Image(getClass().getResourceAsStream("/resources/default.png"));
            avatarImage.setImage(chosenAvatar);
        }
    }
}
