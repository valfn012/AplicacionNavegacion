/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package AplicacionNavegacion;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author esther
 */
public class AvatarChooserController implements Initializable {

    @FXML
    private Button bAccept;
    @FXML
    private Button bCancel;
    @FXML
    private ImageView previewImage;
    @FXML
    private Button bLoadImage;
    
    private Image selectedAvatar;
    @FXML
    private ListView<Image> listaAvatares;


    /**
     * Initializes the controller class.
     */
    //Avatares por defecto
     private final List<String> avatDef = Arrays.asList(
            "Libraries/IPC2025.jar/avatars/default.png",
            "Libraries/IPC2025.jar/avatars/default.png",
            "Libraries/IPC2025.jar/avatars/default.png"
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        for (int i = 0; i < avatDef.size(); i++) {
    String path = avatDef.get(i);
    InputStream is = getClass().getResourceAsStream(path);

    if (is != null) {
        Image img = new Image(is, 60, 60, true, true); 
        listaAvatares.getItems().add(img);
    }
}

        
         if (!listaAvatares.getItems().isEmpty()) {
            selectedAvatar = listaAvatares.getItems().get(0);
            previewImage.setImage(selectedAvatar);
        }
        
        listaAvatares.getSelectionModel().selectedItemProperty().addListener((obs, oldImg, newImg) -> {
            if (newImg != null) {
                selectedAvatar = newImg;
                previewImage.setImage(newImg);
            }
        });
        
        
    }    

    @FXML
    private void aceptar(ActionEvent event) {
        Stage stage = (Stage) bAccept.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Diálogo de confirmación");
        alert.setHeaderText("Salir de elegir avatar");
        alert.setContentText("¿Seguro que quieres cancelar?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
        System.out.println("OK");
    } else {
            System.out.println("CANCEL");
}
    }

    @FXML
    private void loadIm(ActionEvent event) {
         FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona una imagen");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                Image img = new Image(new FileInputStream(file));
                selectedAvatar = img;
                previewImage.setImage(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
     public Image getSelectedAvatar() {
        return selectedAvatar;
    }
    
}
