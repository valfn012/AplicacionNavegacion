/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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

/**
 * VentanaMapaController (versión integrada):
 * - Implementa TODO lo del “primer documento” (regla, flecha, compás/arco, transportador,
 *   dibujo libre, punto, texto editable con menú, modo color, goma, papelera)
 * - Usando los nombres del segundo (overlayPane, regla/compas/transportador/goma/lapiz/trazoLinea/trazoArco, etc.)
 */
public class VentanaMapaController implements Initializable {

    // ======================================
    // Modelo / usuario (se mantiene)
    // ======================================
    private final HashMap<String, Poi> hm = new HashMap<>();
    private ObservableList<Poi> data;
    private User activeUser;

    // ======================================
    // Zoom
    // ======================================
    private Group zoomGroup;

    @FXML private ScrollPane map_scrollpane;
    @FXML private Slider zoom_slider;
    @FXML private SplitPane splitPane;

    // ======================================
    // Barra herramientas (FXML)
    // ======================================
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
    @FXML private Region espaciado;

    // (de tu FXML original; no imprescindibles para el dibujo)
    @FXML private MenuButton map_pin;
    @FXML private MenuItem pin_info;

    @FXML private VBox rootPane;
    @FXML private Button mostOcult;
    @FXML private MenuItem modificarPerfil;
    @FXML private MenuItem cerrarSesion;
    @FXML private MenuItem historial;
    @FXML
    private Button bProblema;

    void setStage(Stage stage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // ======================================
    // Estado herramientas
    // ======================================
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

    // ======================================
    // Capas de dibujo (debe ir dentro de zoomGroup)
    // ======================================
    private final Pane overlayPane = new Pane();

    // ======================================
    // FREEHAND
    // ======================================
    private Polyline currentStroke = null;

    // ======================================
    // LINE
    // ======================================
    private Point2D firstPoint = null;

    // ======================================
    // POINT / TEXT: última posición ratón
    // ======================================
    private Point2D lastMousePosition = null;

    // ======================================
    // REGLA + FLECHA
    // ======================================
    private Region reglaView;
    private boolean bloqueoRotacionRegla = false;

    private boolean drawingArrowGesture = false;
    private Point2D puntoInicioFlecha = null;
    private Line previewLine = null;

    // ======================================
    // TRANSPORTADOR
    // ======================================
    private Region transportadorView;

    // ======================================
    // COMPÁS + ARCO
    // ======================================
    private Group compasView;
    private Circle arcPreview = null;
    private Point2D arcCenter = null;

    // ======================================
    // BORRADO
    // ======================================
    private final javafx.event.EventHandler<MouseEvent> deleteHandler = e -> {
        if (activeTool != Tool.ERASER) return;
        Node n = (Node) e.getSource();
        overlayPane.getChildren().remove(n);
        e.consume();
    };

    // ======================================
    // API
    // ======================================
    public void setUser(User u) {
        this.activeUser = u;
    }

    // ======================================
    // UI general
    // ======================================
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

    // ======================================
    // INIT
    // ======================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Slider zoom
        zoom_slider.setMin(0.5);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom(newVal.doubleValue()));

        // Construir escena zoom: mapa + overlay encima
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
        map_scrollpane.setContent(contentGroup);

        // HBox spacer
        if (espaciado != null) {
            HBox.setHgrow(espaciado, Priority.ALWAYS);
        }

        // Color + grosor
        if (colorPicker != null) {
            currentColor = colorPicker.getValue();
            colorPicker.valueProperty().addListener((obs, o, n) -> currentColor = n);
        }
        if (grosorSlider != null) {
            currentStrokeWidth = Math.max(1.0, grosorSlider.getValue());
            grosorSlider.valueProperty().addListener((obs, o, n) -> currentStrokeWidth = Math.max(1.0, n.doubleValue()));
        }

        // ToggleGroup herramientas
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

        // Vistas (regla/transportador/compás)
        initReglaView();
        initTransportadorView();
        initCompasView();

        // Estado inicial
        trazoArco.setDisable(true);

        // Guardar última posición ratón
        overlayPane.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            lastMousePosition = new Point2D(e.getX(), e.getY());
        });

        // Handlers de ratón principales
        registerDrawingHandlers();
    }

    private void registerDrawingHandlers() {
        // CLICK: punto / línea (2 clics) / texto (si estuviera en modo)
        overlayPane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            Point2D p = new Point2D(e.getX(), e.getY());

            // MODO COLOR: recolorear el nodo clicado
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

        // PRESSED: flecha (si clic en borde superior regla) / arco (centro) / freehand
        overlayPane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;

            // Gesto flecha desde la regla
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

            // Arco (solo si compás activo)
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

            // Freehand
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

        // DRAGGED: flecha preview / arco radio / freehand puntos
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

        // RELEASED: terminar flecha / terminar arco / terminar freehand
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

    // ======================================
    // Herramientas (handlers de botones)
    // ======================================

    @FXML
    private void usarRegla(ActionEvent event) {
        boolean selected = regla.isSelected();
        reglaView.setVisible(selected);
        if (selected) {
            activeTool = Tool.RULER;
            reglaView.toFront();
            // la flecha solo tiene sentido con regla visible
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
            // requiere regla visible
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
            // deshabilitar el resto (como en el doc 1)
            setToolsDisabled(true);
        } else {
            activeTool = Tool.NONE;
            setToolsDisabled(false);
            // estados “dependientes”
            flecha.setDisable(!regla.isSelected());
            trazoArco.setDisable(!compas.isSelected());
        }
    }

    private void setToolsDisabled(boolean disabled) {
        // NOTA: no deshabilitamos la propia goma
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
            // reinsertar herramientas visuales
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

        // En el doc 1 el texto se coloca con un TextField en el mapa y se convierte a Text al final.
        crearTextFieldEditable(p);
    }

    // ======================================
    // Implementaciones “doc 1”
    // ======================================

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

        // ContextMenu (tamaño + color), como en el doc 1
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

        // ENTER: convertir
        tf.setOnAction(ev -> convertirTextFieldATexto(tf));

        // Foco perdido: convertir
        tf.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV && overlayPane.getChildren().contains(tf)) {
                convertirTextFieldATexto(tf);
            }
        });

        // ESC: cancelar
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
            // nota: esto es “solo para el TextField”; el Text final se creará con ese color
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
        t.setLayoutY(tf.getLayoutY() + 16); // pequeño ajuste visual

        // tamaño: si el TextField tenía estilo font-size, lo copiamos
        String style = tf.getStyle() == null ? "" : tf.getStyle();
        if (!style.isEmpty()) {
            t.setStyle(style.replace("-fx-text-fill", "-fx-fill"));
        }

        // color: si el usuario eligió uno en el menú
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

    // ================= REGLA =================
    private void initReglaView() {
        if (reglaView != null) {
            overlayPane.getChildren().remove(reglaView);
        }
        reglaView = new Region();
        reglaView.getStyleClass().add("regla");
        reglaView.setPrefSize(420, 90);
        reglaView.setVisible(regla != null && regla.isSelected());

        // posición inicial
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

        // cursor
        reglaView.setOnMouseEntered(e -> reglaView.setCursor(Cursor.HAND));
        reglaView.setOnMouseExited(e -> reglaView.setCursor(Cursor.DEFAULT));
    }

    private boolean clickEnBordeSuperiorRegla(MouseEvent e) {
        if (reglaView == null || !reglaView.isVisible()) return false;

        // Convertir el punto del overlay al sistema local de la regla
        Point2D scene = overlayPane.localToScene(e.getX(), e.getY());
        Point2D local = reglaView.sceneToLocal(scene);
        // Banda superior: y entre 0 y 20px
        return local.getX() >= 0 && local.getX() <= reglaView.getWidth() && local.getY() >= 0 && local.getY() <= 20;
    }

    private void drawArrow(Point2D start, Point2D end) {
        // línea principal
        Line shaft = new Line(start.getX(), start.getY(), end.getX(), end.getY());
        shaft.setStroke(currentColor);
        shaft.setStrokeWidth(Math.max(2, currentStrokeWidth));

        // punta
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

    // ================= TRANSPORTADOR =================
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

    // ================= COMPÁS =================
    private void initCompasView() {
        if (compasView != null) {
            overlayPane.getChildren().remove(compasView);
        }
        // compás simplificado: dos patas y un eje
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

    // ================= COLOR helper =================
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
            // recolorear hijos típicos (línea + punta de flecha)
            for (Node child : ((Group) n).getChildren()) {
                actualizarColor(child, newColor);
            }
        } else if (n instanceof Region) {
            // regla/transportador: no recoloreamos (son herramienta)
        }
    }

    private static String toWeb(Color c) {
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
    // ======================================
    // Stubs (mantén si tu app los usa)
    // ======================================
    @FXML private void about(ActionEvent event) {
        Alert mensaje = new Alert(Alert.AlertType.INFORMATION);
        // Acceder al Stage del Dialog y cambiar el icono
        Stage dialogStage = (Stage) mensaje.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2025");
        mensaje.showAndWait();
    }
    @FXML private void abrirModificarPerfil(ActionEvent event) {
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
