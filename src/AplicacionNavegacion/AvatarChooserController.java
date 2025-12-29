package AplicacionNavegacion;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

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
    @FXML private Button eliminarAvatar;
    @FXML private Button renombrarAvatar;

    private final ArrayList<Image> avatarImages = new ArrayList<>();
    private final ArrayList<File> avatarFiles = new ArrayList<>();

    private Image selectedImage;

    // Padres posibles
    private VentanaRegistroController parentRegistro;
    private VentanaVerPerfilController parentModificar;

    private final File customAvatarFolder = new File("user_data/custom_avatars");

    // =========================
    // SETTERS DE PADRE
    // =========================
    public void setParentController(VentanaRegistroController c) {
        this.parentRegistro = c;
    }

    public void setParentController2(VentanaVerPerfilController c) {
        this.parentModificar = c;
    }

    // =========================
    // INIT
    // =========================
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (!customAvatarFolder.exists()) {
            customAvatarFolder.mkdirs();
        }

        loadDefaultAvatar("/resources/default.png", "Avatar por defecto", null);
        loadDefaultAvatar("/resources/avatarHombre.jpg", "Hombre", null);
        loadDefaultAvatar("/resources/avatarMujer.jpg", "Mujer", null);

        loadCustomAvatars();

        listaAvatares.getSelectionModel().selectedIndexProperty().addListener((obs, o, n) -> {
            if (n != null && n.intValue() >= 0) {
                selectedImage = avatarImages.get(n.intValue());
                previewImage.setImage(selectedImage);
            }
        });

        previewImage.setFitWidth(180);
        previewImage.setFitHeight(180);
        previewImage.setPreserveRatio(true);
    }

    // =========================
    // CARGA AVATARES
    // =========================
    private void loadDefaultAvatar(String path, String name, File file) {
        try {
            avatarImages.add(new Image(getClass().getResourceAsStream(path)));
            avatarFiles.add(file);
            listaAvatares.getItems().add(name);
        } catch (Exception e) {
            System.out.println("No se pudo cargar " + path);
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

    // =========================
    // CARGAR IMAGEN
    // =========================
    @FXML
    private void handleLoadImage() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar imagen");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);
        if (file == null) return;

        try {
            File dest = new File(customAvatarFolder, "avatar_" + System.currentTimeMillis() + ".png");
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Image img = new Image(dest.toURI().toString());

            avatarImages.add(img);
            avatarFiles.add(dest);
            listaAvatares.getItems().add(dest.getName().replace(".png", ""));

            listaAvatares.getSelectionModel().selectLast();
            previewImage.setImage(img);
            selectedImage = img;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // RENOMBRAR
    // =========================
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

        if (!result.isPresent() || result.get().trim().isEmpty()) return;

        File newFile = new File(customAvatarFolder, result.get().trim() + ".png");

        try {
            Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            avatarFiles.set(index, newFile);
            avatarImages.set(index, new Image(newFile.toURI().toString()));
            listaAvatares.getItems().set(index, result.get().trim());
            previewImage.setImage(avatarImages.get(index));
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("No se pudo renombrar el avatar.");
        }
    }

    // =========================
    // ELIMINAR
    // =========================
    @FXML
    private void handleDelete() {

        int index = listaAvatares.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        File file = avatarFiles.get(index);
        if (file == null) {
            showAlert("Este avatar no puede eliminarse.");
            return;
        }

        try {
            Files.delete(file.toPath());
            avatarFiles.remove(index);
            avatarImages.remove(index);
            listaAvatares.getItems().remove(index);
            previewImage.setImage(null);
            selectedImage = null;

            if (parentModificar != null) {
                parentModificar.clearAvatarIfDeleted(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error eliminando avatar.");
        }
    }

    // =========================
    // ACEPTAR / CANCELAR
    // =========================
    @FXML
    private void handleAccept() {

        int idx = listaAvatares.getSelectionModel().getSelectedIndex();
        if (selectedImage == null || idx < 0) {
            closeWindow();
            return;
        }

        if (parentRegistro != null) {
            parentRegistro.setChosenAvatar(selectedImage, avatarFiles.get(idx));
        }

        if (parentModificar != null) {
            parentModificar.setChosenAvatar(selectedImage, avatarFiles.get(idx));
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
