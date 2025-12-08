package AplicacionNavegacion;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AvatarChooserController implements Initializable {

    @FXML private ListView<String> listaAvatares;
    @FXML private ImageView previewImage;

    @FXML private Button bAccept;
    @FXML private Button bCancel;
    @FXML private Button bLoadImage;

    private final ArrayList<Image> avatarImages = new ArrayList<>();
    private Image selectedImage;

    private FXMLPruebaRegistroController parent;


    public void setParentController(FXMLPruebaRegistroController c) {
        parent = c;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            avatarImages.add(new Image(getClass().getResourceAsStream("/resources/default.png")));
            avatarImages.add(new Image(getClass().getResourceAsStream("/resources/avatarHombre.jpg")));
            avatarImages.add(new Image(getClass().getResourceAsStream("/resources/avatarMujer.jpg")));
        } catch (Exception e) {
            System.out.println("Error cargando avatares");
        }

        listaAvatares.getItems().addAll("Avatar por defecto", "Hombre", "Mujer");

        listaAvatares.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {

            if (newV.intValue() >= 0) {
                selectedImage = avatarImages.get(newV.intValue());
                previewImage.setImage(selectedImage);
            }
        });

        previewImage.setFitWidth(180);
        previewImage.setFitHeight(180);
        previewImage.setPreserveRatio(true);
    }


    @FXML
    private void handleLoadImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            selectedImage = new Image(file.toURI().toString());
            previewImage.setImage(selectedImage);
        }
    }


    @FXML
    private void handleAccept() {
        if (selectedImage != null && parent != null)
            parent.setChosenAvatar(selectedImage);

        closeWindow();
    }


    @FXML
    private void handleCancel() {
        closeWindow();
    }


    private void closeWindow() {
        Stage stage = (Stage) listaAvatares.getScene().getWindow();
        stage.close();
    }
}
