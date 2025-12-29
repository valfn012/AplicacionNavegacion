/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplicacionNavegacion;

import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author LERI
 */
public class AplicacionNavegacionApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/AplicacionNavegacion/FXMLiniSesion.fxml"));

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        Scene scene = new Scene(root);

        stage.setTitle("Aplicación navegación");

        stage.setScene(scene);
        stage.show();
//        stage.setOnCloseRequest(event -> {
//        // Crear el diálogo de confirmación
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirmar salida");
//        alert.setHeaderText(null);
//        alert.setContentText("¿Seguro que quieres cerrar la aplicación?");
//
//        // Mostrarlo y esperar respuesta
//        Optional<ButtonType> result = alert.showAndWait();
//
//        if (result.isPresent() && result.get() == ButtonType.CANCEL) {
//            event.consume(); // Cancela el cierre
//        }
//    
//        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
