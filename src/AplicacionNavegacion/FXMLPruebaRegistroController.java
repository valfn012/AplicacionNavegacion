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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import model.NavDAOException;
import model.Navigation;
import model.User;


public class FXMLPruebaRegistroController implements Initializable {

    // ---------- FXML ----------
    @FXML private TextField emailField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordField2;
    @FXML private DatePicker dateField;

    @FXML private Button bAccept;
    @FXML private Button bCancel;

    @FXML private Button bLeft;
    @FXML private Button bRight;
    @FXML private Button subirAvatar;

    @FXML private ImageView avatarImage;


    // ---------- AVATARES ----------
    private final ArrayList<Image> avatarList = new ArrayList<>();
    private int avatarIndex = 0;


    // =========================================================
    // ALERTAS
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        showAlert("Errores en el formulario", message, Alert.AlertType.ERROR);
    }


    // =========================================================
    // VALIDACIÓN GLOBAL (errores conjuntos)
    private String getAllErrors() {

        StringBuilder errors = new StringBuilder();

        // --- NICKNAME ---
        String nick = userField.getText();

        if (!User.checkNickName(nick)) {
            errors.append("• El nickname debe tener 6-15 caracteres y solo letras, números, '_' o '-'.\n");
        } else {
            try {
                Navigation nav = Navigation.getInstance();
                if (nav.exitsNickName(nick)) {
                    errors.append("• El nickname ya está registrado.\n");
                }
            } catch (NavDAOException e) {
                errors.append("• Error al acceder a la BD al validar el nickname.\n");
            }
        }

        // --- EMAIL ---
        if (!User.checkEmail(emailField.getText())) {
            errors.append("• El email no tiene un formato válido.\n");
        }

        // --- PASSWORD ---
        if (!User.checkPassword(passwordField.getText())) {
            errors.append("• La contraseña debe incluir mayúsculas, minúsculas, números y símbolos, y tener 8-20 caracteres.\n");
        }

        // --- CONFIRM PASSWORD ---
        if (!passwordField.getText().equals(passwordField2.getText())) {
            errors.append("• Las contraseñas no coinciden.\n");
        }

        // --- FECHA NACIMIENTO ---
        LocalDate birth = dateField.getValue();
        if (birth == null || !birth.isBefore(LocalDate.now().minus(16, YEARS))) {
            errors.append("• Debes tener 16 años o más.\n");
        }

        return errors.toString();
    }


    // =========================================================
    // INITIALIZE
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // --- Cargar avatares por defecto ---
        avatarList.add(new Image(getClass().getResourceAsStream("/resources/avatarHombre.jpg")));
        avatarList.add(new Image(getClass().getResourceAsStream("/resources/avatarMujer.jpg")));

        avatarImage.setImage(avatarList.get(0));
        avatarImage.setFitHeight(120);
        avatarImage.setPreserveRatio(true);

        bCancel.setOnAction(this::goToLogin);
    }


    // =========================================================
    // REGISTRO
    @FXML
    private void handleBAcceptOnAction(ActionEvent event) {

        String errors = getAllErrors();

        if (!errors.isEmpty()) {
            showErrorAlert(errors);
            return;
        }

        String nick = userField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        LocalDate birth = dateField.getValue();
        Image avatar = avatarList.get(avatarIndex);

        try {
            Navigation nav = Navigation.getInstance();
            nav.registerUser(nick, email, pass, avatar, birth);

            showAlert("Registro completado", "Usuario registrado correctamente.", Alert.AlertType.INFORMATION);

            goToLogin(event);  // REDIRIGIR AUTOMÁTICAMENTE AL LOGIN

        } catch (NavDAOException e) {
            showErrorAlert("Hubo un error al registrar el usuario.");
        }
    }


    // =========================================================
    // NAVEGAR DE VUELTA AL LOGIN
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) bCancel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =========================================================
    // MANEJO DE AVATARES
    @FXML
    private void handleAvatarLeft(ActionEvent event) {
        avatarIndex = (avatarIndex - 1 + avatarList.size()) % avatarList.size();
        avatarImage.setImage(avatarList.get(avatarIndex));
    }

    @FXML
    private void handleAvatarRight(ActionEvent event) {
        avatarIndex = (avatarIndex + 1) % avatarList.size();
        avatarImage.setImage(avatarList.get(avatarIndex));
    }

    @FXML
    private void handleUploadAvatar(ActionEvent event) {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            Image newAvatar = new Image(file.toURI().toString());
            avatarList.add(newAvatar);
            avatarIndex = avatarList.size() - 1;
            avatarImage.setImage(newAvatar);
        }
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
        stage.setMinWidth(stage.getWidth()); 
        stage.setMaxWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        stage.setMaxHeight(stage.getHeight());

        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}
