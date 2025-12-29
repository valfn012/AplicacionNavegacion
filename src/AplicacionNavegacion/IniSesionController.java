package AplicacionNavegacion;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import model.NavDAOException;
import model.Navigation;
import model.User;

public class IniSesionController implements Initializable {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button bAceptar;
    @FXML private Label bRegister;

    private User currentUser;
    @FXML
    private ImageView iconoError;
    @FXML
    private ImageView iconoError2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nada especial aquí
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error en inicio de sesión");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void onAceptar(ActionEvent event) {

        String nick = emailField.getText().trim();
        String pass = passwordField.getText().trim();

        if (nick.isEmpty()) {
            showError("Debes introducir tu nombre de usuario (nickname).");
            return;
        }

        if (pass.isEmpty()) {
            showError("Debes introducir una contraseña.");
            return;
        }

        try {
            Navigation nav = Navigation.getInstance();

            if (!nav.exitsNickName(nick)) {
                showError("No existe ninguna cuenta con ese nickname.");
                return;
            }

            User u = nav.authenticate(nick, pass);
            if (u == null) {
                showError("La contraseña es incorrecta.");
                return;
            }

            // 🔑 AQUÍ ESTABA EL ERROR
            currentUser = u;

            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("FXMLVentanaMapa.fxml"));
            Parent root = loader.load();

            VentanaMapaController controller = loader.getController();
            controller.setUser(currentUser); // ✅ usuario REAL

            Stage stage = (Stage) bAceptar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

        } catch (NavDAOException e) {
            e.printStackTrace();
            showError("Error accediendo a la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error cargando la interfaz.");
        }
    }

    @FXML
    private void onRegister(MouseEvent event) {
        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("VentanaRegistroFXML.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) bRegister.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("No se pudo abrir la ventana de registro.");
        }
    }
}
