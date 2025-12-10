/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplicacionNavegacion;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import AplicacionNavegacion.Poi;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import model.Navigation;
import model.User;

/**
 *
 * @author jsoler
 */
public class VentanaMapaController implements Initializable {

    //=======================================
    // hashmap para guardar los puntos de interes POI
    private final HashMap<String, Poi> hm = new HashMap<>();
    private ObservableList<Poi> data;
    // ======================================
    // la variable zoomGroup se utiliza para dar soporte al zoom
    // el escalado se realiza sobre este nodo, al escalar el Group no mueve sus nodos
    private Group zoomGroup;

    @FXML
    private ListView<Poi> map_listview;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private MenuButton map_pin;
    @FXML
    private MenuItem pin_info;
    
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label mousePosition;
    @FXML
    private Button regla;
    @FXML
    private Button compas;
    @FXML
    private Button transportador;
    @FXML
    private MenuItem historial;
    
    private User activeUser;
    @FXML
    private VBox rootPane;
    @FXML
    private MenuItem modificarPerfil;
    @FXML
    private MenuItem cerrarSesion;
    @FXML
    private Button goma;
    @FXML
    private Button papelera;
    @FXML
    private Button menosG;
    @FXML
    private Button masG;
    @FXML
    private Button texto;
    @FXML
    private Button lineas;
    @FXML
    private Button arco;
    @FXML
    private Button bAleatorio;
    @FXML
    private Button corregir;
    @FXML
    private Button bSalir;
    @FXML
    private Region espaciado;
    
    public void setUser(User u) {
    this.activeUser = u;
}
    
    private Stage stagePrincipal;

    public void setStage(Stage stage) {
        this.stagePrincipal = stage;
    }

    
    

    


    private void eraseAt(Point2D p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void insertTextAt(Point2D p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

  @FXML
private void abrirModificarPerfil(ActionEvent event) {
    try {
        // Cargar el FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaModificarPerfil.fxml"));
        Parent root = loader.load();

        // Obtener controlador
        VentanaModificarPerfil controller = loader.getController();
        controller.setUser(activeUser);

        // Crear modal
        Stage modal = new Stage();
        modal.setTitle("Modificar perfil");
        modal.setScene(new Scene(root));
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setResizable(false);

        // Pasar el Stage al controlador
        controller.setStage(modal);

        // Detectar la X de cierre
        modal.setOnCloseRequest(e -> {
            e.consume();               // evita cierre automático
            controller.confirmarCierre();
        });

        // Hacer que la ventana principal sea la dueña
        Stage owner = (Stage) rootPane.getScene().getWindow();
        modal.initOwner(owner);
        modal.centerOnScreen();

        // Mostrar modal
        modal.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();
    }
}



    




    
    // ----------------------------------------
// HERRAMIENTAS DISPONIBLES
// ----------------------------------------
private enum Tool {
    NONE, POINT, LINE, ARC, TEXT, ERASER, CLEAR, RULER, COMPASS, PROTRACTOR
}

private Tool activeTool = Tool.NONE;

// Color y grosor actuales
private javafx.scene.paint.Color currentColor = javafx.scene.paint.Color.RED;
private double currentStrokeWidth = 2.0;

// Para líneas y arcos
private Point2D firstPoint = null;


private boolean draggingTransportador = false;
private double offsetX, offsetY;

// Capa donde se dibujan los elementos del usuario
private javafx.scene.layout.Pane overlayPane = new javafx.scene.layout.Pane();

    

    @FXML
    void zoomIn(ActionEvent event) {
        //================================================
        // el incremento del zoom dependerá de los parametros del 
        // slider y del resultado esperado
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.1);
    }

    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.1);
    }
    
    // esta funcion es invocada al cambiar el value del slider zoom_slider
    private void zoom(double scaleValue) {
        //===================================================
        //guardamos los valores del scroll antes del escalado
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        //===================================================
        // escalamos el zoomGroup en X e Y con el valor de entrada
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        //===================================================
        // recuperamos el valor del scroll antes del escalado
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    @FXML
    void listClicked(MouseEvent event) {
        Poi itemSelected = map_listview.getSelectionModel().getSelectedItem();

        // Animación del scroll hasta la mousePosistion del item seleccionado
        double mapWidth = zoomGroup.getBoundsInLocal().getWidth();
        double mapHeight = zoomGroup.getBoundsInLocal().getHeight();
        double scrollH = itemSelected.getPosition().getX() / mapWidth;
        double scrollV = itemSelected.getPosition().getY() / mapHeight;
        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        // movemos el objto map_pin hasta la mousePosistion del POI
//        double pinW = map_pin.getBoundsInLocal().getWidth();
//        double pinH = map_pin.getBoundsInLocal().getHeight();
        map_pin.setLayoutX(itemSelected.getPosition().getX());
        map_pin.setLayoutY(itemSelected.getPosition().getY());
        pin_info.setText(itemSelected.getDescription());
        map_pin.setVisible(true);
    }

    private void initData() {
        data=map_listview.getItems();
        data.add(new Poi("1F", "Edificion del DSIC", 275, 250));
        data.add( new Poi("Agora", "Agora", 575, 350));
        data.add( new Poi("Pista", "Pista de atletismo y campo de futbol", 950, 350));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
                initData();

        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));

        // Crear grupos para permitir zoom
        Group contentGroup = new Group();
        zoomGroup = new Group();

        // El scrollpane ya contenía el mapa (ImageView)
        Node mapContent = map_scrollpane.getContent();

        zoomGroup.getChildren().add(mapContent);

        // ⬇️ AÑADIMOS UNA CAPA SUPERIOR PARA DIBUJAR
        overlayPane.setPickOnBounds(false);
        zoomGroup.getChildren().add(overlayPane);

        contentGroup.getChildren().add(zoomGroup);
        map_scrollpane.setContent(contentGroup);

        // Registramos eventos de ratón para dibujar
        

        
        HBox.setHgrow(espaciado, Priority.ALWAYS);
    }

    @FXML
    private void showPosition(MouseEvent event) {
        mousePosition.setText("sceneX: " + (int) event.getSceneX() + ", sceneY: " + (int) event.getSceneY() + "\n"
                + "         X: " + (int) event.getX() + ",          Y: " + (int) event.getY());
    }

    private void closeApp(ActionEvent event) {
        ((Stage) zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
        // Acceder al Stage del Dialog y cambiar el icono
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2025");
        mensaje.showAndWait();
    }

    @FXML
    private void addPoi(MouseEvent event) {

        if (event.isControlDown()) {
            Dialog<Poi> poiDialog = new Dialog<>();
            poiDialog.setTitle("Nuevo POI");
            poiDialog.setHeaderText("Introduce un nuevo POI");
            // Acceder al Stage del Dialog y cambiar el icono
            Stage dialogStage = (Stage) poiDialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));

            ButtonType okButton = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            poiDialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

            TextField nameField = new TextField();
            nameField.setPromptText("Nombre del POI");

            TextArea descArea = new TextArea();
            descArea.setPromptText("Descripción...");
            descArea.setWrapText(true);
            descArea.setPrefRowCount(5);

            VBox vbox = new VBox(10, new Label("Nombre:"), nameField, new Label("Descripción:"), descArea);
            poiDialog.getDialogPane().setContent(vbox);

            poiDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButton) {
                    return new Poi(nameField.getText().trim(), descArea.getText().trim(), 0, 0);
                }
                return null;
            });
            Optional<Poi> result = poiDialog.showAndWait();

            if(result.isPresent()) {
                Point2D localPoint = zoomGroup.sceneToLocal(event.getSceneX(), event.getSceneY());
                Poi poi=result.get();
                poi.setPosition(localPoint);
                map_listview.getItems().add(poi);
            }
        }
    }

    @FXML
    private void usarRegla(ActionEvent event) {
        activeTool = Tool.RULER;
    firstPoint = null;
    }

    @FXML
    private void usarCompas(ActionEvent event) {
        activeTool = Tool.COMPASS;
    firstPoint = null;
    }

    @FXML
    private void usarTransportador(ActionEvent event) {
        activeTool = Tool.PROTRACTOR;
    }

    @FXML
private void abrirHistorial(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLVentanaHistorial.fxml"));
        Parent root = loader.load();

        VentanaHistorialController controller = loader.getController();
        controller.setUser(activeUser);

        Stage stage = (Stage) rootPane.getScene().getWindow();

        
        stage.setMaximized(false);
        stage.sizeToScene();

        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    @FXML
    private void salirSesion(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
        Parent root = loader.load();

        // Obtener el stage desde el menú (MenuItem NO es Node, así que se hace así)
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setResizable(false);
        stage.setMaximized(false);
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("ERROR: No se pudo cargar la ventana de inicio de sesión.");
    }
}

    @FXML
    private void usarGoma(ActionEvent event) {
        activeTool = Tool.ERASER;
    }

    @FXML
    private void usarPapelera(ActionEvent event) {
        activeTool = Tool.CLEAR;
    overlayPane.getChildren().clear();
    }

    @FXML
    private void menosGrosor(ActionEvent event) {
        currentStrokeWidth = Math.max(1, currentStrokeWidth - 1);
    }

    @FXML
    private void masGrosor(ActionEvent event) {
        currentStrokeWidth += 1;
    }

    @FXML
    private void insertarTexto(ActionEvent event) {
        activeTool = Tool.TEXT;
    }

    @FXML
    private void trazarLineas(ActionEvent event) {
        activeTool = Tool.LINE;
        firstPoint = null;
    }

    @FXML
    private void trazarArco(ActionEvent event) {
        activeTool = Tool.ARC;
    firstPoint = null;
    }

    @FXML
    private void usarAleatorio(ActionEvent event) {
        
    }

    @FXML
    private void corregirEj(ActionEvent event) {
    }

    @FXML
    private void salirMapa(ActionEvent event) {
    }

private void drawPoint(Point2D p) {
    javafx.scene.shape.Circle c = new javafx.scene.shape.Circle(p.getX(), p.getY(), 4);
    c.setFill(currentColor);
    c.setStroke(currentColor);

    c.setOnMouseClicked(e -> {
        if (e.isShiftDown()) {
            c.setFill(currentColor);
            c.setStroke(currentColor);
        }
    });

    overlayPane.getChildren().add(c);
}


}
