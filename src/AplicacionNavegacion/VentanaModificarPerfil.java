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
    private Image chosenAvatar; // imagen elegida en la ventana modal

    private File chosenAvatarFile; 


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
    // VALIDACIÓN CON MENSAJES ESPECÍFICOS
    private String validateForm() {

        StringBuilder errors = new StringBuilder();

        // NICKNAME
        String nick = userField.getText();
        if (nick.isEmpty()) {
            errors.append("• Debes introducir un nickname.\n");
        } else if (!User.checkNickName(nick)) {
            errors.append("• El nickname debe tener entre 6 y 15 caracteres y usar letras, números, '_' o '-'.\n");
        } else {
            try {
                if (Navigation.getInstance().exitsNickName(nick)) {
                    errors.append("• Este nickname ya existe. Elige otro.\n");
                }
            } catch (NavDAOException e) {
                errors.append("• Error al comprobar el nickname en la base de datos.\n");
            }
        }

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

        // Imagen por defecto
        chosenAvatar = new Image(getClass().getResourceAsStream("/resources/default.png"));
        avatarImage.setImage(chosenAvatar);
        avatarImage.setFitWidth(200);
        avatarImage.setFitHeight(200);
        avatarImage.setPreserveRatio(true);

        // Botón cancelar vuelve al login
        bCancel.setOnAction(this::goToLogin);
    }


    // =========================================================
    // REGISTRAR
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


    // =========================================================
    // IR A LOGIN
    private void goToLogin(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
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
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
        Parent root = loader.load();

        // Obtener la ventana actual
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Cambiar escena
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Ajustes de ventana
        stage.setResizable(false);
        stage.setMaximized(false);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("ERROR: No se pudo cargar FXMLinisesion.fxml");
    }
    }

    public void clearAvatarIfDeleted(File deletedFile) {

    // Si no hay avatar asignado, no hacer nada
    if (chosenAvatarFile == null) return;

    // Comprobar si el archivo eliminado es el que estaba usando el usuario
    if (chosenAvatarFile.equals(deletedFile)) {

        // Resetear avatar
        chosenAvatarFile = null;
        chosenAvatar = new Image(getClass().getResourceAsStream("/resources/default.png"));

        avatarImage.setImage(chosenAvatar);
    }
}

}
