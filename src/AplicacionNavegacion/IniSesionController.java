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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import model.NavDAOException;
import model.Navigation;
import model.User;

public class IniSesionController implements Initializable {

    @FXML
    private TextField emailField;   // realmente es el nickname

    @FXML
    private TextField passwordField;

    @FXML
    private Button bAceptar;

    @FXML
    private Label bRegister;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // De momento no hace falta inicializar nada
    }

    // ---------------------------
    // MOSTRAR ERROR EN VENTANA
    // ---------------------------
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error en inicio de sesión");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // ---------------------------
    // BOTÓN ACEPTAR
    // ---------------------------
    @FXML
    private void onAceptar(ActionEvent event) {

        String nick = emailField.getText().trim();   // nickname
        String pass = passwordField.getText().trim();

        // Validar nickname
        if (nick.isEmpty()) {
            showError("Debes introducir tu nombre de usuario (nickname).");
            return;
        }

        // Validar contraseña
        if (pass.isEmpty()) {
            showError("Debes introducir una contraseña.");
            return;
        }

        try {
            Navigation nav = Navigation.getInstance();

            // Comprobar si existe el nickname
            if (!nav.exitsNickName(nick)) {
                showError("No existe ninguna cuenta con ese nickname.");
                return;
            }

            // Autenticar
            User u = nav.authenticate(nick, pass);
            if (u == null) {
                showError("La contraseña es incorrecta.");
                return;
            }

            // LOGIN CORRECTO → cargar mapa
            System.out.println("Login correcto: " + u.getNickName());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLVentanaMapa.fxml"));
            Parent root = loader.load();

            VentanaMapaController controller = loader.getController();

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

    // ---------------------------
    // TEXTO "¿Aún no te has registrado?"
    // ---------------------------
    
    @FXML

private void onRegister(MouseEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/AplicacionNavegacion/VentanaRegistroFXML.fxml")
        );

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
