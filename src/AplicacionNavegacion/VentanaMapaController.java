/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplicacionNavegacion;

import java.net.URL;
import javafx.scene.shape.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.User;


public class VentanaMapaController implements Initializable {

    
    private final HashMap<String, Poi> hm = new HashMap<>();
    private ObservableList<Poi> data;
    private User activeUser;

   
    private Group zoomGroup;

    @FXML private ScrollPane map_scrollpane;
    @FXML private Slider zoom_slider;
    @FXML private SplitPane splitPane;

    
    @FXML private ToolBar barraHerr;
    @FXML private ToggleButton regla;
    @FXML private ToggleButton compas;
    @FXML private ToggleButton transportador;
    @FXML private ToggleButton goma;
    @FXML private ToggleButton lapiz;
    @FXML private ToggleButton trazoLinea;
    @FXML private ToggleButton trazoArco;
    @FXML private ToggleButton punto;
    @FXML private ToggleButton flecha;
    @FXML private ToggleButton modoColor;

    @FXML private Button texto;
    @FXML private Button papelera;
    @FXML private Button menosG;
    @FXML private Button masG;

    @FXML private Slider grosorSlider;
    @FXML private ColorPicker colorPicker;
    private Region espaciado;

    
    @FXML private MenuButton map_pin;
    @FXML private MenuItem pin_info;

    @FXML private VBox rootPane;
    @FXML private Button mostOcult;
    @FXML private MenuItem modificarPerfil;
    @FXML private MenuItem cerrarSesion;
    @FXML private MenuItem historial;
    @FXML
    private Button bProblema;
    @FXML
    private ImageView imagenAvatar;
   
    @FXML
    private MenuButton perfil;
    @FXML
    private ImageView mapImage;
    @FXML
    private StackPane stackRoot;
    
    @FXML
    private HBox pencilBox;
    @FXML
    private ToggleButton zoom;
    @FXML
    private HBox zoomBar;
    @FXML
    private Region toolbarSpacer;
    private Stage problemaStage;


public void setUser(User user) {
    this.activeUser = user;
    cargarAvatar();
    cargarPerfil();
}

private Stage stage;

public void setStage(Stage stage) {
    this.stage = stage;
}

    

    private void cargarAvatar() {
        if (activeUser == null) return;

    Image avatar = activeUser.getAvatar();

    if (avatar != null) {
        imagenAvatar.setImage(avatar);
    }
    }

    private void cargarPerfil() {
        if (activeUser == null) {
            return;
        }
        perfil.setText(activeUser.getNickName());
    }

    @FXML
private void desplegarZoom(ActionEvent event) {

    boolean mostrar = !zoomBar.isVisible();

    if (mostrar) {
        zoomBar.setVisible(true);
        zoomBar.setManaged(true);
        zoomBar.setDisable(false);

        zoomBar.toFront();


        

    } else {
        zoomBar.setVisible(false);
        zoomBar.setManaged(false);
        zoomBar.setDisable(true);

        zoomBar.toBack();
    }
}





private double dragOffsetX;
private double dragOffsetY;





   



    

    
    
    

   
    private enum Tool {
        NONE,
        FREEHAND,
        LINE,
        ARC,
        POINT,
        TEXT,
        RULER,
        PROTRACTOR,
        COMPASS,
        ERASER,
        COLOR,
        ARROW
    }

    private Tool activeTool = Tool.NONE;

    private Color currentColor = Color.BLACK;
    private double currentStrokeWidth = 2.0;

   
    private final Pane overlayPane = new Pane();

    
    private Polyline currentStroke = null;

   
    private Point2D firstPoint = null;

    
    private Point2D lastMousePosition = null;

   
    private Pane reglaView;
    private boolean bloqueoRotacionRegla = false;
    private Circle asaIzq, asaDer, pivote;
    private javafx.scene.transform.Rotate reglaRotate;
    private double mouseStartAngle, startDragAngle;


    private boolean drawingArrowGesture = false;
    private Point2D puntoInicioFlecha = null;
    private Line previewLine = null;
    
    private Pane transportadorView;
    private Rotate transportadorRotate;
    private Circle handleRotacion;
    private double mouseStartAngleTransportador;
    private double transportadorStartAngle;


    
    private Pane compasView;
    private Circle arcPreview = null;
    private Point2D arcCenter = null;
    
private Pane legRight, legLeftWrapper, legLeft;
private Circle rightTip, openHandle;
private Rotate compasRotate;
private Rotate rotateLeft;

private double compasMouseStartAngle, compasStartAngle;
private double startAngleOpen, startRotateOpen;
private double startAngleRotate, startRotateCompas;

private boolean faseApertura = true;
private Path arcoActual = null;


    
    private final EventHandler<MouseEvent> deleteHandler = e -> {
    if (activeTool != Tool.ERASER) return;

    Node n = (Node) e.getSource();

    
    if (n == reglaView || n == transportadorView || n == compasView) return;

    overlayPane.getChildren().remove(n);
    e.consume();
};


   
    

    
    @FXML
    private void mostOcult(ActionEvent event) {
        boolean visible = barraHerr.isVisible();
        barraHerr.setVisible(!visible);
        barraHerr.setManaged(!visible);
    }

    @FXML
    void zoomIn(ActionEvent event) {
        zoom_slider.setValue(zoom_slider.getValue() + 0.1);
    }

    @FXML
    void zoomOut(ActionEvent event) {
        zoom_slider.setValue(zoom_slider.getValue() - 0.1);
    }

    private void zoom(double scaleValue) {
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();

        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);

        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    
    @Override
public void initialize(URL url, ResourceBundle rb) {

    // Slider zoom
    zoom_slider.setMin(0.2);   
    zoom_slider.setMax(1.5);
    zoom_slider.setValue(1.0);
    zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom(newVal.doubleValue()));

    
    Group contentGroup = new Group();
    zoomGroup = new Group();

    Node mapContent = map_scrollpane.getContent(); 
    StackPane stack = new StackPane();
    stack.getChildren().addAll(mapContent, overlayPane);

    overlayPane.prefWidthProperty().bind(stack.widthProperty());
    overlayPane.prefHeightProperty().bind(stack.heightProperty());
    overlayPane.setPickOnBounds(true);
    overlayPane.toFront();

    zoomGroup.getChildren().add(stack);
    contentGroup.getChildren().add(zoomGroup);

   
    StackPane wrapper = new StackPane(contentGroup);
    map_scrollpane.setContent(wrapper);

    
    map_scrollpane.setFitToWidth(true);
    map_scrollpane.setFitToHeight(true);

   
    if (espaciado != null) {
        HBox.setHgrow(espaciado, Priority.ALWAYS);
    }

    if (colorPicker != null) {
        currentColor = colorPicker.getValue();
        colorPicker.valueProperty().addListener((obs, o, n) -> currentColor = n);
    }
    if (grosorSlider != null) {
        currentStrokeWidth = Math.max(1.0, grosorSlider.getValue());
        grosorSlider.valueProperty().addListener((obs, o, n) -> currentStrokeWidth = Math.max(1.0, n.doubleValue()));
    }

    ToggleGroup tools = new ToggleGroup();
    regla.setToggleGroup(tools);
    compas.setToggleGroup(tools);
    transportador.setToggleGroup(tools);
    goma.setToggleGroup(tools);
    lapiz.setToggleGroup(tools);
    trazoLinea.setToggleGroup(tools);
    trazoArco.setToggleGroup(tools);
    punto.setToggleGroup(tools);
    flecha.setToggleGroup(tools);
    modoColor.setToggleGroup(tools);

    initReglaView();
    initTransportadorView();
    initCompasView();

    trazoArco.setDisable(true);

    overlayPane.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
        lastMousePosition = new Point2D(e.getX(), e.getY());
    });

    registerDrawingHandlers();

    
    Platform.runLater(() -> {
        Bounds viewport = map_scrollpane.getViewportBounds();
        Bounds contentBounds = stack.getBoundsInLocal();

        if (contentBounds.getWidth() <= 0 || contentBounds.getHeight() <= 0) return;

        double scaleToFit = Math.min(
                viewport.getWidth() / contentBounds.getWidth(),
                viewport.getHeight() / contentBounds.getHeight()
        );

       
        scaleToFit = Math.max(zoom_slider.getMin(), Math.min(zoom_slider.getMax(), scaleToFit));

        zoom_slider.setValue(scaleToFit); // esto llama a zoom() por el listener
        map_scrollpane.setHvalue(0);
        map_scrollpane.setVvalue(0);
    });
    Platform.runLater(() -> {

    
    mapImage.setPreserveRatio(true);

    double viewportW = map_scrollpane.getViewportBounds().getWidth();
    double viewportH = map_scrollpane.getViewportBounds().getHeight();

    double imgW = mapImage.getImage().getWidth();
    double imgH = mapImage.getImage().getHeight();

    
    double scale = Math.min(
            viewportW / imgW,
            viewportH / imgH
    );

    
    zoom_slider.setValue(scale);


    map_scrollpane.setHvalue(0);
    map_scrollpane.setVvalue(0);
});

        zoomBar.setVisible(false);
        zoomBar.setManaged(false);
        zoomBar.setDisable(true);


        zoomBar.toBack();

    
    
    if (toolbarSpacer != null) {
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);
    }
    
    habilitarArrastreZoomBox();

    
  
    
    
    
    
    
    
    
    
    
}


    private void registerDrawingHandlers() {
        
        overlayPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            Point2D p = new Point2D(e.getX(), e.getY());

            
            if (activeTool == Tool.COLOR) {
                Node n = (Node) e.getTarget();
                if (n != null && n != overlayPane) {
                    actualizarColor(n, currentColor);
                }
                e.consume();
                return;
            }

            if (activeTool == Tool.POINT) {
                drawPoint(p);
                // el modo punto del doc 1 es “un solo uso”
                punto.setSelected(false);
                activeTool = Tool.NONE;
                e.consume();
                return;
            }

            if (activeTool == Tool.LINE) {
                handleLineClick(p);
                e.consume();
                return;
            }

            e.consume();
        });

       
        overlayPane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            
            if (activeTool == Tool.ARROW) {
                if (clickEnBordeSuperiorRegla(e)) {
                    drawingArrowGesture = true;
                    puntoInicioFlecha = new Point2D(e.getX(), e.getY());

                    previewLine = new Line(puntoInicioFlecha.getX(), puntoInicioFlecha.getY(), puntoInicioFlecha.getX(), puntoInicioFlecha.getY());
                    previewLine.setStrokeWidth(3);
                    previewLine.setStroke(Color.GREY);
                    previewLine.getStrokeDashArray().setAll(10.0, 0.0);
                    overlayPane.getChildren().add(previewLine);

                    bloqueoRotacionRegla = true;
                    e.consume();
                    return;
                } else {
                    
                    flecha.setSelected(false);
                    drawingArrowGesture = false;
                    bloqueoRotacionRegla = false;
                }
            }

            
            if (activeTool == Tool.ARC) {
                arcCenter = new Point2D(e.getX(), e.getY());
                arcPreview = new Circle(arcCenter.getX(), arcCenter.getY(), 1);
                arcPreview.setFill(Color.TRANSPARENT);
                arcPreview.setStroke(currentColor);
                arcPreview.setStrokeWidth(currentStrokeWidth);
                arcPreview.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
                overlayPane.getChildren().add(arcPreview);
                arcPreview.toFront();
                e.consume();
                return;
            }

           
            if (activeTool == Tool.FREEHAND) {
                currentStroke = new Polyline();
                currentStroke.setStroke(currentColor);
                currentStroke.setStrokeWidth(currentStrokeWidth);
                currentStroke.setFill(null);
                currentStroke.setStrokeLineCap(StrokeLineCap.ROUND);
                currentStroke.setStrokeLineJoin(StrokeLineJoin.ROUND);
                currentStroke.getPoints().addAll(e.getX(), e.getY());
                currentStroke.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
                overlayPane.getChildren().add(currentStroke);
                e.consume();
            }
        });

       
        overlayPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            if (drawingArrowGesture && previewLine != null) {
                previewLine.setEndX(e.getX());
                previewLine.setEndY(e.getY());
                e.consume();
                return;
            }

            if (activeTool == Tool.ARC && arcPreview != null && arcCenter != null) {
                double dx = e.getX() - arcCenter.getX();
                double dy = e.getY() - arcCenter.getY();
                arcPreview.setRadius(Math.hypot(dx, dy));
                e.consume();
                return;
            }

            if (activeTool == Tool.FREEHAND && currentStroke != null) {
                currentStroke.getPoints().addAll(e.getX(), e.getY());
                e.consume();
            }
        });

        
        overlayPane.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            if (drawingArrowGesture && puntoInicioFlecha != null) {
                Point2D fin = new Point2D(e.getX(), e.getY());

                if (previewLine != null) {
                    overlayPane.getChildren().remove(previewLine);
                    previewLine = null;
                }

                drawArrow(puntoInicioFlecha, fin);

                drawingArrowGesture = false;
                bloqueoRotacionRegla = false;
                puntoInicioFlecha = null;
                flecha.setSelected(false);
                activeTool = Tool.NONE;

                e.consume();
                return;
            }

            if (activeTool == Tool.ARC) {
                arcPreview = null;
                arcCenter = null;
               
                trazoArco.setSelected(false);
                activeTool = Tool.NONE;
                e.consume();
                return;
            }

            if (activeTool == Tool.FREEHAND) {
                currentStroke = null;
                e.consume();
            }
        });
    }

   

    @FXML
    private void usarRegla(ActionEvent event) {
        boolean selected = regla.isSelected();
        reglaView.setVisible(selected);
        if (selected) {
            activeTool = Tool.RULER;
            reglaView.toFront();
            
            flecha.setDisable(false);
        } else {
            if (activeTool == Tool.RULER) activeTool = Tool.NONE;
            flecha.setDisable(true);
            flecha.setSelected(false);
        }
    }
    
    
    
    
    
    

    @FXML
    private void usarTransportador(ActionEvent event) {
        boolean selected = transportador.isSelected();
        transportadorView.setVisible(selected);
        if (selected) {
            activeTool = Tool.PROTRACTOR;
            transportadorView.toFront();
        } else {
            if (activeTool == Tool.PROTRACTOR) activeTool = Tool.NONE;
        }
    }

    @FXML
    private void usarCompas(ActionEvent event) {
        boolean selected = compas.isSelected();
        compasView.setVisible(selected);
        if (selected) {
            activeTool = Tool.COMPASS;
            compasView.toFront();
            trazoArco.setDisable(false);
        } else {
            if (activeTool == Tool.COMPASS) activeTool = Tool.NONE;
            trazoArco.setDisable(true);
            trazoArco.setSelected(false);
        }
    }

    @FXML
    private void usarLapiz(ActionEvent event) {
        if (lapiz.isSelected()) {
            activeTool = Tool.FREEHAND;
            firstPoint = null;
        } else if (activeTool == Tool.FREEHAND) {
            activeTool = Tool.NONE;
        }
    }

    
    private void masOpLapiz(ActionEvent event) {
        
        boolean mostrar = !pencilBox.isVisible();

    if (mostrar) {
      
        pencilBox.setVisible(true);
        pencilBox.setManaged(true);
        pencilBox.setDisable(false);

        pencilBox.toFront();


        

    } else {
       
        pencilBox.setVisible(false);
        pencilBox.setManaged(false);
        pencilBox.setDisable(true);

        pencilBox.toBack();
    }
    }
    
    
    @FXML
    private void trazarLinea(ActionEvent event) {
        if (trazoLinea.isSelected()) {
            activeTool = Tool.LINE;
            firstPoint = null;
        } else if (activeTool == Tool.LINE) {
            activeTool = Tool.NONE;
            firstPoint = null;
        }
    }

    @FXML
    private void trazarArco(ActionEvent event) {
        if (trazoArco.isSelected()) {
            if (!compas.isSelected()) {
                trazoArco.setSelected(false);
                trazoArco.setDisable(true);
                return;
            }
            activeTool = Tool.ARC;
        } else if (activeTool == Tool.ARC) {
            activeTool = Tool.NONE;
        }
    }

    @FXML
    private void usarPunto(ActionEvent event) {
        if (punto.isSelected()) {
            activeTool = Tool.POINT;
        } else if (activeTool == Tool.POINT) {
            activeTool = Tool.NONE;
        }
    }

    @FXML
    private void usarFlecha(ActionEvent event) {
        if (flecha.isSelected()) {
            
            if (!regla.isSelected()) {
                flecha.setSelected(false);
                return;
            }
            activeTool = Tool.ARROW;
        } else if (activeTool == Tool.ARROW) {
            activeTool = Tool.NONE;
            bloqueoRotacionRegla = false;
        }
    }

    @FXML
    private void usarModoColor(ActionEvent event) {
        if (modoColor.isSelected()) {
            activeTool = Tool.COLOR;
        } else if (activeTool == Tool.COLOR) {
            activeTool = Tool.NONE;
        }
    }

    @FXML
    private void usarGoma(ActionEvent event) {
        boolean selected = goma.isSelected();
        if (selected) {
            activeTool = Tool.ERASER;
            
            setToolsDisabled(true);
        } else {
            activeTool = Tool.NONE;
            setToolsDisabled(false);
            
            flecha.setDisable(!regla.isSelected());
            trazoArco.setDisable(!compas.isSelected());
        }
    }

    private void setToolsDisabled(boolean disabled) {
        
        lapiz.setDisable(disabled);
        texto.setDisable(disabled);
        punto.setDisable(disabled);
        modoColor.setDisable(disabled);
        regla.setDisable(disabled);
        transportador.setDisable(disabled);
        compas.setDisable(disabled);
        trazoLinea.setDisable(disabled);
        trazoArco.setDisable(disabled);
        flecha.setDisable(disabled);
        papelera.setDisable(disabled);
        menosG.setDisable(disabled);
        masG.setDisable(disabled);
        if (colorPicker != null) colorPicker.setDisable(disabled);
        if (grosorSlider != null) grosorSlider.setDisable(disabled);
    }

    @FXML
    private void usarPapelera(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reestablecer carta");
        alert.setHeaderText("¿Está seguro de que desea reestablecer la carta?");
        alert.setContentText("Esta acción eliminará todos los elementos dibujados y no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            overlayPane.getChildren().clear();
            
            initReglaView();
            initTransportadorView();
            initCompasView();
        }
    }

    @FXML
    private void menosGrosor(ActionEvent event) {
        currentStrokeWidth = Math.max(1, currentStrokeWidth - 1);
        if (grosorSlider != null) grosorSlider.setValue(currentStrokeWidth);
    }

    @FXML
    private void masGrosor(ActionEvent event) {
        currentStrokeWidth = currentStrokeWidth + 1;
        if (grosorSlider != null) grosorSlider.setValue(currentStrokeWidth);
    }

    @FXML
    private void insertarTexto(ActionEvent event) {
        Point2D p = (lastMousePosition != null)
                ? lastMousePosition
                : new Point2D(overlayPane.getWidth() / 2.0, overlayPane.getHeight() / 2.0);

        
        crearTextFieldEditable(p);
    }

    
    private void drawPoint(Point2D p) {
        Circle c = new Circle(p.getX(), p.getY(), 5);
        c.setFill(currentColor);
        c.setStroke(currentColor);
        c.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
        makeDraggable(c);
        overlayPane.getChildren().add(c);
        c.toFront();
    }

    private void handleLineClick(Point2D p) {
        if (firstPoint == null) {
            firstPoint = p;
            return;
        }

        Line line = new Line(firstPoint.getX(), firstPoint.getY(), p.getX(), p.getY());
        line.setStroke(currentColor);
        line.setStrokeWidth(currentStrokeWidth);
        line.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
        overlayPane.getChildren().add(line);
        line.toFront();
        makeDraggable(line);

        firstPoint = null;
    }

    private void crearTextFieldEditable(Point2D p) {
        TextField tf = new TextField();
        tf.setPromptText("Escribe...");
        tf.setLayoutX(p.getX());
        tf.setLayoutY(p.getY());
        tf.setPrefColumnCount(10);

        
        javafx.scene.control.ContextMenu menu = new javafx.scene.control.ContextMenu();

        javafx.scene.control.Menu menuTam = new javafx.scene.control.Menu("Tamaño");
        for (int size : new int[]{12, 16, 20, 24, 28}) {
            MenuItem mi = new MenuItem(size + "px");
            mi.setOnAction(ev -> tf.setStyle("-fx-font-size: " + size + "px;"));
            menuTam.getItems().add(mi);
        }

        javafx.scene.control.Menu menuCol = new javafx.scene.control.Menu("Color");
        menuCol.getItems().addAll(
                colorItem("Negro", Color.BLACK, tf),
                colorItem("Rojo", Color.RED, tf),
                colorItem("Azul", Color.DODGERBLUE, tf),
                colorItem("Verde", Color.FORESTGREEN, tf)
        );

        menu.getItems().addAll(menuTam, menuCol);
        tf.setContextMenu(menu);

        overlayPane.getChildren().add(tf);
        tf.toFront();
        tf.requestFocus();

       
        tf.setOnAction(ev -> convertirTextFieldATexto(tf));

        
        tf.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV && overlayPane.getChildren().contains(tf)) {
                convertirTextFieldATexto(tf);
            }
        });

        
        tf.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ESCAPE) {
                overlayPane.getChildren().remove(tf);
                k.consume();
            }
        });
    }

    private MenuItem colorItem(String label, Color c, TextField tf) {
        MenuItem mi = new MenuItem(label);
        mi.setOnAction(ev -> {
            
            String existing = tf.getStyle() == null ? "" : tf.getStyle();
            tf.setStyle(existing + "; -fx-text-fill: " + toWeb(c) + ";");
            tf.setUserData(c);
        });
        return mi;
    }

    private void convertirTextFieldATexto(TextField tf) {
        String s = tf.getText() == null ? "" : tf.getText().trim();
        if (s.isEmpty()) {
            overlayPane.getChildren().remove(tf);
            return;
        }

        Text t = new Text(s);
        t.setLayoutX(tf.getLayoutX());
        t.setLayoutY(tf.getLayoutY() + 16); 

       
        String style = tf.getStyle() == null ? "" : tf.getStyle();
        if (!style.isEmpty()) {
            t.setStyle(style.replace("-fx-text-fill", "-fx-fill"));
        }

        
        Object ud = tf.getUserData();
        if (ud instanceof Color) {
            t.setFill((Color) ud);
        } else {
            t.setFill(currentColor);
        }

        t.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
        makeDraggable(t);

        overlayPane.getChildren().remove(tf);
        overlayPane.getChildren().add(t);
        t.toFront();
    }

   
    private void initReglaView() {
        if (reglaView != null) {
            overlayPane.getChildren().remove(reglaView);
        }
        reglaView = new Pane();
        reglaView.getStyleClass().add("regla");
        reglaView.setPrefSize(420, 90);
        reglaView.setVisible(regla != null && regla.isSelected());

        
        reglaView.setTranslateX(50);
        reglaView.setTranslateY(50);

        overlayPane.getChildren().add(reglaView);
        makeDraggable(reglaView);
        initReglaInteractiva();
    }

    private void initReglaInteractiva() {
    double ancho = 600;
    double alto = 20;

    reglaView.setPrefSize(ancho, alto + 20);

    asaIzq = new Circle(6);
    asaDer = new Circle(6);
    pivote = new Circle(5);

    asaIzq.getStyleClass().add("regla-handle");
    asaDer.getStyleClass().add("regla-handle");
    pivote.getStyleClass().add("regla-pivote");

    double yCentro = (alto + 20) / 2.0;

    asaIzq.setLayoutX(0);
    asaIzq.setLayoutY(yCentro);

    
    asaDer.setLayoutX(ancho);
    asaDer.setLayoutY(yCentro);

    reglaView.getChildren().setAll(asaIzq, asaDer, pivote);

    
    reglaRotate = new javafx.scene.transform.Rotate(0);
    reglaView.getTransforms().add(reglaRotate);

    Platform.runLater(() -> {
        double px = reglaView.getPrefWidth() / 2;
        double py = reglaView.getPrefHeight() / 2;

        pivote.setLayoutX(px);
        pivote.setLayoutY(py);

        reglaRotate.setPivotX(px);
        reglaRotate.setPivotY(py);
    });

    configurarRotacion(asaIzq);
    configurarRotacion(asaDer);
    configurarMovimientoPivote();
}
private void configurarRotacion(Circle asa) {
    asa.setOnMousePressed(e -> {
        if (bloqueoRotacionRegla) return;

        Point2D pivotScene = pivote.localToScene(
                pivote.getBoundsInLocal().getWidth() / 2,
                pivote.getBoundsInLocal().getHeight() / 2
        );
        Point2D mouseScene = new Point2D(e.getSceneX(), e.getSceneY());

        mouseStartAngle = Math.toDegrees(Math.atan2(
                mouseScene.getY() - pivotScene.getY(),
                mouseScene.getX() - pivotScene.getX()
        ));

        startDragAngle = reglaRotate.getAngle();
        e.consume();
    });

    asa.setOnMouseDragged(e -> {
        if (bloqueoRotacionRegla) return;

        Point2D pivotScene = pivote.localToScene(
                pivote.getBoundsInLocal().getWidth() / 2,
                pivote.getBoundsInLocal().getHeight() / 2
        );
        Point2D mouseScene = new Point2D(e.getSceneX(), e.getSceneY());

        double currentAngle = Math.toDegrees(Math.atan2(
                mouseScene.getY() - pivotScene.getY(),
                mouseScene.getX() - pivotScene.getX()
        ));

        double delta = currentAngle - mouseStartAngle;
        reglaRotate.setAngle(startDragAngle + delta);
        e.consume();
    });

    asa.setOnMouseReleased(e -> e.consume());
}

private void configurarMovimientoPivote() {
    pivote.setOnMousePressed(e -> e.consume());

    pivote.setOnMouseDragged(e -> {
        double currentAngle = reglaRotate.getAngle();
        reglaRotate.setAngle(0);

        Point2D mouse = reglaView.sceneToLocal(e.getSceneX(), e.getSceneY());

        double x = mouse.getX();
        double min = 0;
        double max = reglaView.getPrefWidth();
        x = Math.max(min, Math.min(x, max));

        pivote.setLayoutX(x);

        reglaRotate.setPivotX(x);
        reglaRotate.setPivotY(pivote.getLayoutY());

        reglaRotate.setAngle(currentAngle);
        e.consume();
    });

    pivote.setOnMouseReleased(e -> e.consume());
}


    private boolean clickEnBordeSuperiorRegla(MouseEvent e) {
        if (reglaView == null || !reglaView.isVisible()) return false;

        
        Point2D scene = overlayPane.localToScene(e.getX(), e.getY());
        Point2D local = reglaView.sceneToLocal(scene);
        
        return local.getX() >= 0 && local.getX() <= reglaView.getWidth() && local.getY() >= 0 && local.getY() <= 20;
    }

    private void drawArrow(Point2D start, Point2D end) {
        
        Line shaft = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        shaft.setStroke(currentColor);
        shaft.setStrokeWidth(Math.max(2, currentStrokeWidth));

        
        double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
        double headLen = 14 + currentStrokeWidth;
        double headAng = Math.toRadians(25);

        double x1 = end.getX() - headLen * Math.cos(angle - headAng);
        double y1 = end.getY() - headLen * Math.sin(angle - headAng);
        double x2 = end.getX() - headLen * Math.cos(angle + headAng);
        double y2 = end.getY() - headLen * Math.sin(angle + headAng);

        Polygon head = new Polygon(
                end.getX(), end.getY(),
                x1, y1,
                x2, y2
        );
        head.setFill(currentColor);

        Group g = new Group(shaft, head);
        g.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
        makeDraggable(g);

        overlayPane.getChildren().add(g);
        g.toFront();
    }

    
    private void initTransportadorView() {
    transportadorView = new Pane();
    transportadorView.getStyleClass().add("transportador");
    transportadorView.setPrefSize(260, 260); // el tamaño lo decides tú

    transportadorView.setVisible(false);
    overlayPane.getChildren().add(transportadorView);

    makeDraggable(transportadorView);
    initTransportador(); 
}
private void initTransportador() {

   
    transportadorView.getChildren().clear();

    
    transportadorRotate = new Rotate(0);
    transportadorView.getTransforms().clear();
    transportadorView.getTransforms().add(transportadorRotate);

    
    handleRotacion = new Circle(6);
    handleRotacion.getStyleClass().add("transportador-handle");
    transportadorView.getChildren().add(handleRotacion);

    
    Platform.runLater(() -> {

        Bounds b = transportadorView.getLayoutBounds();

       
        double cx = b.getMinX() + b.getWidth() / 2;
        double cy = b.getMinY() + b.getHeight() / 2;

        transportadorRotate.setPivotX(cx);
        transportadorRotate.setPivotY(cy);

        
        double r = handleRotacion.getRadius();
        handleRotacion.setLayoutX(b.getMaxX() - r);
        handleRotacion.setLayoutY(b.getMinY() + r);
    });

    
    configurarRotacionTransportador(handleRotacion);

    
}
private void configurarRotacionTransportador(Circle handle) {

    handle.setOnMousePressed(e -> {
        Point2D centerScene = transportadorView.localToScene(
                transportadorRotate.getPivotX(),
                transportadorRotate.getPivotY()
        );

        Point2D mouse = new Point2D(e.getSceneX(), e.getSceneY());

        mouseStartAngleTransportador = Math.toDegrees(Math.atan2(
                mouse.getY() - centerScene.getY(),
                mouse.getX() - centerScene.getX()
        ));

        transportadorStartAngle = transportadorRotate.getAngle();
        e.consume();
    });

    handle.setOnMouseDragged(e -> {
        Point2D centerScene = transportadorView.localToScene(
                transportadorRotate.getPivotX(),
                transportadorRotate.getPivotY()
        );

        Point2D mouse = new Point2D(e.getSceneX(), e.getSceneY());

        double angle = Math.toDegrees(Math.atan2(
                mouse.getY() - centerScene.getY(),
                mouse.getX() - centerScene.getX()
        ));

        double delta = angle - mouseStartAngleTransportador;
        transportadorRotate.setAngle(transportadorStartAngle + delta);

        e.consume();
    });

    handle.setOnMouseReleased(e -> e.consume());
}


    
   private void initCompasView() {

    if (compasView != null) {
        overlayPane.getChildren().remove(compasView);
    }

    trazoArco.setDisable(false);

    compasView = new Pane();
    compasView.getStyleClass().add("compass");

    
    Region pivot = new Region();
    pivot.getStyleClass().add("pivot");

    
    legRight = new Pane();
    legRight.getStyleClass().add("leg-right");

    rightTip = new Circle(4);
    rightTip.getStyleClass().add("compass-tip");
    rightTip.setLayoutX(0);
    rightTip.setLayoutY(140);
    legRight.getChildren().add(rightTip);

    
    legLeftWrapper = new Pane();
    legLeft = new Pane();
    legLeft.getStyleClass().add("leg-left");

    openHandle = new Circle(6);
    openHandle.getStyleClass().add("compass-handle");
    openHandle.setLayoutX(0);
    openHandle.setLayoutY(140);
    legLeft.getChildren().add(openHandle);

    legLeftWrapper.getChildren().add(legLeft);

    
    compasView.getChildren().addAll(legRight, legLeftWrapper, pivot);
    overlayPane.getChildren().add(compasView);

    compasView.setTranslateX(250);
    compasView.setTranslateY(180);
    compasView.setVisible(compas != null && compas.isSelected());

    
    compasRotate = new Rotate(0);
    compasView.getTransforms().add(compasRotate);

    Platform.runLater(() -> {

        double px = 100;
        double py = 20;

        pivot.setLayoutX(px);
        pivot.setLayoutY(py);

        legRight.setLayoutX(px);
        legRight.setLayoutY(py);

        legLeftWrapper.setLayoutX(px);
        legLeftWrapper.setLayoutY(py);

        legLeft.setLayoutX(0);
        legLeft.setLayoutY(0);

        Point2D centerScene = rightTip.localToScene(0, 0);
        Point2D centerInCompas = compasView.sceneToLocal(centerScene);
        compasRotate.setPivotX(centerInCompas.getX());
        compasRotate.setPivotY(centerInCompas.getY());
    });

    rotateLeft = new Rotate(30, 0, 0);
    legLeft.getTransforms().add(rotateLeft);

    configurarAperturaYArco();
    configurarDragCompas();
    configurarRotacionCompas();
}

   
private void configurarRotacionCompas() {

    legRight.setOnMousePressed(e -> {
        if (activeTool == Tool.ARC) return;

        Point2D centerScene = rightTip.localToScene(0, 0);
        Point2D mouseScene = new Point2D(e.getSceneX(), e.getSceneY());

        compasMouseStartAngle = Math.toDegrees(Math.atan2(
                mouseScene.getY() - centerScene.getY(),
                mouseScene.getX() - centerScene.getX()
        ));
        compasStartAngle = compasRotate.getAngle();
        e.consume();
    });

    legRight.setOnMouseDragged(e -> {
        if (activeTool == Tool.ARC) return;

        Point2D centerScene = rightTip.localToScene(0, 0);
        Point2D mouseScene = new Point2D(e.getSceneX(), e.getSceneY());

        double currentAngle = Math.toDegrees(Math.atan2(
                mouseScene.getY() - centerScene.getY(),
                mouseScene.getX() - centerScene.getX()
        ));

        compasRotate.setAngle(compasStartAngle + (currentAngle - compasMouseStartAngle));
        e.consume();
    });
}

private void configurarAperturaYArco() {

    openHandle.setOnMousePressed(e -> {

        Point2D mouse = new Point2D(e.getSceneX(), e.getSceneY());
        boolean modoArco = activeTool == Tool.ARC;

        if (!modoArco && faseApertura) {

            Point2D pivotScene = legLeft.localToScene(0, 0);
            startAngleOpen = Math.toDegrees(Math.atan2(
                    mouse.getY() - pivotScene.getY(),
                    mouse.getX() - pivotScene.getX()
            ));
            startRotateOpen = rotateLeft.getAngle();

        } else {

            Point2D centerScene = rightTip.localToScene(0, 0);
            startAngleRotate = Math.toDegrees(Math.atan2(
                    mouse.getY() - centerScene.getY(),
                    mouse.getX() - centerScene.getX()
            ));
            startRotateCompas = compasRotate.getAngle();

            if (modoArco) {
                arcoActual = new Path();
                arcoActual.setStroke(currentColor);
                arcoActual.setStrokeWidth(currentStrokeWidth);
                arcoActual.setFill(null);

                Point2D start = openHandle.localToScene(0, 0);
                Point2D local = overlayPane.sceneToLocal(start);
                arcoActual.getElements().add(new javafx.scene.shape.MoveTo(local.getX(), local.getY()));

                overlayPane.getChildren().add(arcoActual);
                arcoActual.addEventHandler(MouseEvent.MOUSE_CLICKED, deleteHandler);
                makeDraggable(arcoActual);
            }
        }
        e.consume();
    });

    openHandle.setOnMouseDragged(e -> {

        Point2D mouse = new Point2D(e.getSceneX(), e.getSceneY());
        boolean modoArco = activeTool == Tool.ARC;

        if (!modoArco && faseApertura) {

            Point2D pivotScene = legLeft.localToScene(0, 0);
            double angle = Math.toDegrees(Math.atan2(
                    mouse.getY() - pivotScene.getY(),
                    mouse.getX() - pivotScene.getX()
            ));
            rotateLeft.setAngle(startRotateOpen + (angle - startAngleOpen));

        } else {

            Point2D centerScene = rightTip.localToScene(0, 0);
            double angle = Math.toDegrees(Math.atan2(
                    mouse.getY() - centerScene.getY(),
                    mouse.getX() - centerScene.getX()
            ));
            compasRotate.setAngle(startRotateCompas + (angle - startAngleRotate));

            if (modoArco && arcoActual != null) {
                Point2D tip = openHandle.localToScene(0, 0);
                Point2D local = overlayPane.sceneToLocal(tip);
                arcoActual.getElements().add(new javafx.scene.shape.LineTo(local.getX(), local.getY()));
            }
        }
        e.consume();
    });

    openHandle.setOnMouseReleased(e -> {
        if (activeTool == Tool.ARC) arcoActual = null;
        else faseApertura = !faseApertura;

        trazoArco.setSelected(false);
        e.consume();
    });
}
private void configurarDragCompas() {

    final Point2D[] last = new Point2D[1];

    compasView.setOnMousePressed(e -> {
        if (activeTool == Tool.ARC || e.getTarget() == openHandle) return;
        last[0] = new Point2D(e.getSceneX(), e.getSceneY());
        e.consume();
    });

    compasView.setOnMouseDragged(e -> {
        if (last[0] == null || activeTool == Tool.ARC || e.getTarget() == openHandle) return;

        double dx = e.getSceneX() - last[0].getX();
        double dy = e.getSceneY() - last[0].getY();

        compasView.setTranslateX(compasView.getTranslateX() + dx);
        compasView.setTranslateY(compasView.getTranslateY() + dy);

        last[0] = new Point2D(e.getSceneX(), e.getSceneY());
        e.consume();
    });

    compasView.setOnMouseReleased(e -> last[0] = null);
}


    
    private void actualizarColor(Node n, Color newColor) {
        if (n == null) return;
        if (n == reglaView || n == transportadorView || n == compasView) return;

        if (n instanceof Circle) {
            Circle c = (Circle) n;
            c.setFill(newColor);
            c.setStroke(newColor);
        } else if (n instanceof Line) {
            Line l = (Line) n;
            l.setStroke(newColor);
        } else if (n instanceof Polyline) {
            Polyline pl = (Polyline) n;
            pl.setStroke(newColor);
        } else if (n instanceof Text) {
            ((Text) n).setFill(newColor);
        } else if (n instanceof Group) {
            
            for (Node child : ((Group) n).getChildren()) {
                actualizarColor(child, newColor);
            }
        } else if (n instanceof Region) {
           
        }
    }

    private static String toWeb(Color c) {
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
    
    @FXML private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
        
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2025");
        mensaje.showAndWait();
    }
    @FXML private void abrirModificarPerfil(ActionEvent event) {
    try {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaVerPerfil.fxml"));
        Parent root = loader.load();

        
        VentanaVerPerfilController controller = loader.getController();
        controller.setUser(activeUser);

        
        Stage modal = new Stage();
        modal.setTitle("Modificar perfil");
        modal.setScene(new Scene(root));
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setResizable(false);

        
        controller.setStage(modal);

        
        modal.setOnCloseRequest(e -> {
            e.consume();               
            controller.confirmarCierre();
        });

       
        Stage owner = (Stage) rootPane.getScene().getWindow();
        modal.initOwner(owner);
        modal.centerOnScreen();

        
        modal.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    @FXML private void abrirHistorial(ActionEvent event) {
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
    @FXML private void salirSesion(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLinisesion.fxml"));
        Parent root = loader.load();

        
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
private void abrirProblema(ActionEvent event) {

    try {
        
        if (problemaStage != null && problemaStage.isShowing()) {

           
            problemaStage.setIconified(false);

            
            problemaStage.toFront();
            problemaStage.requestFocus();
            return;
        }

       
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("Listapr.fxml")
        );
        Parent root = loader.load();

        ListaprController controller = loader.getController();
        controller.setUser(activeUser); 

        problemaStage = new Stage();
        problemaStage.setTitle("Seleccionar problema");
        problemaStage.setScene(new Scene(root));

       

       
        Stage owner = (Stage) rootPane.getScene().getWindow();
        problemaStage.initOwner(owner);

        problemaStage.setResizable(true);
        problemaStage.centerOnScreen();

        
        problemaStage.setOnCloseRequest(e -> problemaStage = null);

        problemaStage.show();
        
        
       


    } catch (Exception e) {
        e.printStackTrace();
    }
}

    
    
    @FXML private void showPosition(MouseEvent event) {
    }
    private void makeDraggable(Node node) {
    final Point2D[] last = new Point2D[1];

    node.setOnMousePressed(e -> {
        last[0] = new Point2D(e.getSceneX(), e.getSceneY());
        node.setCursor(javafx.scene.Cursor.MOVE);
        e.consume();
    });

    node.setOnMouseDragged(e -> {
        if (last[0] == null) return;

        double dx = e.getSceneX() - last[0].getX();
        double dy = e.getSceneY() - last[0].getY();

        node.setTranslateX(node.getTranslateX() + dx);
        node.setTranslateY(node.getTranslateY() + dy);

        last[0] = new Point2D(e.getSceneX(), e.getSceneY());
        e.consume();
    });

    node.setOnMouseReleased(e -> {
        node.setCursor(javafx.scene.Cursor.DEFAULT);
        e.consume();
    });
}


    
    
   

private void habilitarArrastreZoomBox() {

    zoomBar.setOnMousePressed(e -> {
        dragOffsetX = e.getSceneX() - zoomBar.getTranslateX();
        dragOffsetY = e.getSceneY() - zoomBar.getTranslateY();
        e.consume();
    });

    zoomBar.setOnMouseDragged(e -> {
        zoomBar.setTranslateX(e.getSceneX() - dragOffsetX);
        zoomBar.setTranslateY(e.getSceneY() - dragOffsetY);
        e.consume();
    });
}

}