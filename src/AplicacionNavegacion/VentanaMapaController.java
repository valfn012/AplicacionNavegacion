/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AplicacionNavegacion;

import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private User currentUser;
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

public void setUser(User user) {
    this.currentUser = user;
    cargarAvatar();
    cargarPerfil();
}

private Stage stage;

public void setStage(Stage stage) {
    this.stage = stage;
}

    

    private void cargarAvatar() {
        if (currentUser == null) return;

    Image avatar = currentUser.getAvatar();

    if (avatar != null) {
        imagenAvatar.setImage(avatar);
    }
    }

    private void cargarPerfil() {
        if (currentUser == null) {
            return;
        }
        perfil.setText(currentUser.getNickName());
    }

    @FXML
private void desplegarZoom(ActionEvent event) {

    boolean mostrar = !zoomBar.isVisible();

    if (mostrar) {
        // Mostrar y traer al frente
        zoomBar.setVisible(true);
        zoomBar.setManaged(true);
        zoomBar.setDisable(false);

        zoomBar.toFront();


        

    } else {
        // Ocultar y devolver atrás
        zoomBar.setVisible(false);
        zoomBar.setManaged(false);
        zoomBar.setDisable(true);

        zoomBar.toBack();
    }
}

    // ===== Drag del zoomBox (modo simple y fiable) =====

private double dragOffsetX;
private double dragOffsetY;

@FXML
private void onZoomMousePressed(MouseEvent event) {
    dragOffsetX = event.getSceneX() - zoomBar.getLayoutX();
    dragOffsetY = event.getSceneY() - zoomBar.getLayoutY();
    zoomBar.toFront();
    zoomBar.setCursor(Cursor.MOVE);
    event.consume();
}

@FXML
private void onZoomMouseDragged(MouseEvent event) {
    double newX = event.getSceneX() - dragOffsetX;
    double newY = event.getSceneY() - dragOffsetY;

    zoomBar.relocate(newX, newY);
    event.consume();
}


   



    

    
    
    

   
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

   
    private Region reglaView;
    private boolean bloqueoRotacionRegla = false;

    private boolean drawingArrowGesture = false;
    private Point2D puntoInicioFlecha = null;
    private Line previewLine = null;

    
    private Region transportadorView;

    
    private Group compasView;
    private Circle arcPreview = null;
    private Point2D arcCenter = null;

    
    private final javafx.event.EventHandler<MouseEvent> deleteHandler = e -> {
        if (activeTool != Tool.ERASER) return;
        Node n = (Node) e.getSource();
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
    zoom_slider.setMin(0.2);   // permite alejar más si hace falta para ver el mapa completo
    zoom_slider.setMax(1.5);
    zoom_slider.setValue(1.0);
    zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom(newVal.doubleValue()));

    // --- Tu estructura actual ---
    Group contentGroup = new Group();
    zoomGroup = new Group();

    Node mapContent = map_scrollpane.getContent(); // lo que tengas puesto en el FXML como contenido inicial
    StackPane stack = new StackPane();
    stack.getChildren().addAll(mapContent, overlayPane);

    overlayPane.prefWidthProperty().bind(stack.widthProperty());
    overlayPane.prefHeightProperty().bind(stack.heightProperty());
    overlayPane.setPickOnBounds(true);
    overlayPane.toFront();

    zoomGroup.getChildren().add(stack);
    contentGroup.getChildren().add(zoomGroup);

    // ✅ CLAVE: el content del ScrollPane debe ser un Region (StackPane), no un Group
    StackPane wrapper = new StackPane(contentGroup);
    map_scrollpane.setContent(wrapper);

    // Fit ahora SÍ funciona porque wrapper es Region (resizable)
    map_scrollpane.setFitToWidth(true);
    map_scrollpane.setFitToHeight(true);

    // --- Lo demás lo dejas igual ---
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

        // Clamp al rango del slider
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

// Asegura que está detrás del mapa
        zoomBar.toBack();

    
    
    if (toolbarSpacer != null) {
        HBox.setHgrow(toolbarSpacer, Priority.ALWAYS);
    }
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
                    // si clica fuera, desactiva flecha
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
                // un solo uso, como en el doc 1
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
        reglaView = new Region();
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
        final Point2D[] anchorScene = new Point2D[1];
        final double[] anchorRotate = new double[1];

        reglaView.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (bloqueoRotacionRegla) return;
            if (e.isSecondaryButtonDown()) {
                anchorScene[0] = new Point2D(e.getSceneX(), e.getSceneY());
                anchorRotate[0] = reglaView.getRotate();
                e.consume();
            }
        });

        reglaView.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (bloqueoRotacionRegla) return;
            if (e.isSecondaryButtonDown() && anchorScene[0] != null) {
                // rotación simple con deltaX
                double dx = e.getSceneX() - anchorScene[0].getX();
                reglaView.setRotate(anchorRotate[0] + dx * 0.2);
                e.consume();
            }
        });

        reglaView.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                anchorScene[0] = null;
            }
        });

       
        reglaView.setOnMouseEntered(e -> reglaView.setCursor(Cursor.HAND));
        reglaView.setOnMouseExited(e -> reglaView.setCursor(Cursor.DEFAULT));
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
        if (transportadorView != null) {
            overlayPane.getChildren().remove(transportadorView);
        }
        transportadorView = new Region();
        transportadorView.getStyleClass().add("transportador");
        transportadorView.setPrefSize(260, 260);
        transportadorView.setVisible(transportador != null && transportador.isSelected());
        transportadorView.setTranslateX(120);
        transportadorView.setTranslateY(140);
        overlayPane.getChildren().add(transportadorView);
        makeDraggable(transportadorView);
    }

    
    private void initCompasView() {
        if (compasView != null) {
            overlayPane.getChildren().remove(compasView);
        }
        
        Line leg1 = new Line(0, 0, -25, 70);
        Line leg2 = new Line(0, 0, 25, 70);
        leg1.setStroke(Color.DARKGRAY);
        leg2.setStroke(Color.DARKGRAY);
        leg1.setStrokeWidth(3);
        leg2.setStrokeWidth(3);

        Circle joint = new Circle(0, 0, 6);
        joint.setFill(Color.DARKGRAY);

        compasView = new Group(leg1, leg2, joint);
        compasView.setVisible(compas != null && compas.isSelected());
        compasView.setTranslateX(250);
        compasView.setTranslateY(180);

        overlayPane.getChildren().add(compasView);
        makeDraggable(compasView);
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
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VentanaModificarPerfil.fxml"));
        Parent root = loader.load();

        
        VentanaModificarPerfil controller = loader.getController();
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
    @FXML private void abrirProblema(ActionEvent event) {
        
    
    }
    @FXML private void showPosition(MouseEvent event) {
    }
    private void makeDraggable(Node node) {
        final Alpha dragAlpha = new Alpha();

        node.setOnMousePressed(me -> {
            dragAlpha.x = node.getLayoutX() - me.getSceneX();
            dragAlpha.y = node.getLayoutY() - me.getSceneY();
            node.setCursor(javafx.scene.Cursor.MOVE);
            me.consume();
        });

        node.setOnMouseDragged(me -> {
            node.setLayoutX(me.getSceneX() + dragAlpha.x);
            node.setLayoutY(me.getSceneY() + dragAlpha.y);
            me.consume();
        });

        node.setOnMouseReleased(me -> {
            node.setCursor(javafx.scene.Cursor.DEFAULT);
        });

    }
    
    private static class Alpha{
        double x,y;
    }

}
