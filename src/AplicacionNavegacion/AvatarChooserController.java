package AplicacionNavegacion;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
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
    private final ArrayList<File> avatarFiles = new ArrayList<>();   

    private Image selectedImage;
    private VentanaRegistroController parent;

    private final File customAvatarFolder = new File("user_data/custom_avatars");
    @FXML
    private Button eliminarAvatar;
    @FXML
    private Button renombrarAvatar;

    public void setParentController(VentanaRegistroController c) {
        parent = c;
    }
    private VentanaVerPerfilController parent2;
    public void setParentController2(VentanaVerPerfilController parent) {
        parent2 = parent;
}

   


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (!customAvatarFolder.exists())
            customAvatarFolder.mkdirs();

       
        loadDefaultAvatar("/resources/default.png", "Avatar por defecto", null);
        loadDefaultAvatar("/resources/avatarHombre.jpg", "Hombre", null);
        loadDefaultAvatar("/resources/avatarMujer.jpg", "Mujer", null);

       
        loadCustomAvatars();

        
        listaAvatares.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && newV.intValue() >= 0) {
                selectedImage = avatarImages.get(newV.intValue());
                previewImage.setImage(selectedImage);
            }
        });

        previewImage.setFitWidth(180);
        previewImage.setFitHeight(180);
        previewImage.setPreserveRatio(true);
    }


    private void loadDefaultAvatar(String path, String name, File file) {
        try {
            avatarImages.add(new Image(getClass().getResourceAsStream(path)));
            listaAvatares.getItems().add(name);
            avatarFiles.add(file); 
        } catch (Exception e) {
            System.out.println("NO se pudo cargar " + path);
        }
    }

    private void loadCustomAvatars() {
        File[] files = customAvatarFolder.listFiles();
        if (files == null) return;

        for (File f : files) {
            try {
                avatarImages.add(new Image(f.toURI().toString()));

                avatarFiles.add(f);
                listaAvatares.getItems().add(f.getName().replace(".png", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    private void handleLoadImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                String newFileName = "avatar_" + System.currentTimeMillis() + ".png";
                File dest = new File(customAvatarFolder, newFileName);

                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image img = new Image(dest.toURI().toString());

                avatarImages.add(img);
                avatarFiles.add(dest);
                listaAvatares.getItems().add(newFileName.replace(".png", ""));

                listaAvatares.getSelectionModel().selectLast();
                previewImage.setImage(img);
                selectedImage = img;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



   @FXML
private void handleRename() {

    int index = listaAvatares.getSelectionModel().getSelectedIndex();
    if (index < 0) return;

    File oldFile = avatarFiles.get(index);

    if (oldFile == null) {
        showAlert("No se puede renombrar un avatar predeterminado.");
        return;
    }

    TextInputDialog dialog = new TextInputDialog(listaAvatares.getItems().get(index));
    dialog.setHeaderText("Nuevo nombre del avatar:");
    Optional<String> result = dialog.showAndWait();

    if (!result.isPresent()) return;

    String newName = result.get().trim();
    if (newName.isEmpty()) return;

    File newFile = new File(customAvatarFolder, newName + ".png");

    try {

        Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        avatarFiles.set(index, newFile);
        avatarImages.set(index, new Image(newFile.toURI().toString()));

        listaAvatares.getItems().set(index, newName);

        previewImage.setImage(avatarImages.get(index));

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("No se pudo renombrar el avatar.");
    }
}



   

    @FXML
private void handleDelete() {

    int index = listaAvatares.getSelectionModel().getSelectedIndex();
    if (index < 0) return;

    File file = avatarFiles.get(index);

    if (file == null) {
        showAlert("Este avatar no puede eliminarse (es predeterminado)");
        return;
    }

    try {

        previewImage.setImage(null);
avatarImages.set(index, null);
System.gc(); 

        Files.delete(file.toPath());

        
        avatarFiles.remove(index);
        avatarImages.remove(index);
        listaAvatares.getItems().remove(index);

        previewImage.setImage(null);
        selectedImage = null;
        
if (parent != null) {
    parent.clearAvatarIfDeleted(file);
}


    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error eliminando avatar.");
    }
}




   

    
    @FXML
private void handleAccept() {
    int idx = listaAvatares.getSelectionModel().getSelectedIndex();

    if (selectedImage != null && parent != null && idx >= 0) {

        parent.setChosenAvatar(
            selectedImage,
            avatarFiles.get(idx) 
        );
        
        if (parent2 != null) {
        parent2.setChosenAvatar(selectedImage, avatarFiles.get(idx));
    }
    }

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


    

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }


}
