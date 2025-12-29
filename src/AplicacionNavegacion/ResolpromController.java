package AplicacionNavegacion;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import model.Answer;
import model.Problem;
import model.User;

public class ResolpromController implements Initializable {

    // ===== FXML =====
    @FXML
    private Label problemaX;
    @FXML
    private Label enunciadoPx;

    @FXML
    private RadioButton r1Px;
    @FXML
    private RadioButton r2Px;
    @FXML
    private RadioButton r3Px;
    @FXML
    private RadioButton r4Px;

    @FXML
    private Button bAnt;
    @FXML
    private Button bSig;
    @FXML
    private Button bCorregir;

    // ===== STATE =====
    private ToggleGroup grupo;
    private User currentUser;

    private List<Problem> allProblems;
    private int indexActual;

    // ===== HISTORIAL EN SESIÓN =====
    // clave = índice del problema, valor = Answer elegida
    private static Map<Integer, Answer> respuestasUsuario = new HashMap<>();
    @FXML
    private Button bSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        grupo = new ToggleGroup();

        r1Px.setToggleGroup(grupo);
        r2Px.setToggleGroup(grupo);
        r3Px.setToggleGroup(grupo);
        r4Px.setToggleGroup(grupo);
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public void setProblem(Problem problem, int numero, List<Problem> lista) {
        this.allProblems = lista;
        this.indexActual = numero - 1;
        cargarProblema();
    }

    private void cargarProblema() {

        limpiarEstilos();

        Problem p = allProblems.get(indexActual);

        problemaX.setText("Problema " + (indexActual + 1));
        enunciadoPx.setText(p.getText());
        enunciadoPx.setWrapText(true);

        
        List<Answer> answers = new ArrayList<>(p.getAnswers());


        Collections.shuffle(answers);


        configurarRadio(r1Px, answers.get(0));
        configurarRadio(r2Px, answers.get(1));
        configurarRadio(r3Px, answers.get(2));
        configurarRadio(r4Px, answers.get(3));


        grupo.selectToggle(null);
        habilitarRadios(true);

        // 🔁 Si ya fue respondido → restaurar
        if (respuestasUsuario.containsKey(indexActual)) {
            Answer guardada = respuestasUsuario.get(indexActual);
            seleccionarRespuestaGuardada(guardada);
            aplicarCorreccionVisual(guardada);
            habilitarRadios(false);
        }
    }

    private void configurarRadio(RadioButton rb, Answer ans) {
        rb.setText(ans.getText());
        rb.setUserData(ans);
        rb.setDisable(false);
    }

    // =========================
    // CORRECCIÓN CON BOTÓN
    // =========================
    @FXML
private void corregirEj(ActionEvent event) {

    Toggle selected = grupo.getSelectedToggle();
    if (selected == null) return;

    Answer seleccionada = (Answer) selected.getUserData();

    // Guardar respuesta en sesión
    respuestasUsuario.put(indexActual, seleccionada);

    // Limpiar estilos previos
    for (Toggle t : grupo.getToggles()) {
        RadioButton rb = (RadioButton) t;
        rb.getStyleClass().removeAll("respuesta-correcta", "respuesta-incorrecta");

        Answer a = (Answer) rb.getUserData();

        // Mostrar la correcta SIEMPRE
        if (a.getValidity()) {
            rb.getStyleClass().add("respuesta-correcta");
        }
    }

    // Si falló, marcar la elegida como incorrecta
    if (!seleccionada.getValidity()) {
        ((RadioButton) selected).getStyleClass().add("respuesta-incorrecta");
    }

    // Bloquear cambios
    for (Toggle t : grupo.getToggles()) {
        ((RadioButton) t).setDisable(true);
    }
}

    private void aplicarCorreccionVisual(Answer seleccionada) {

        for (Toggle t : grupo.getToggles()) {
            RadioButton rb = (RadioButton) t;
            Answer a = (Answer) rb.getUserData();

            rb.getStyleClass().removeAll("respuesta-correcta", "respuesta-incorrecta");

            if (a.getValidity()) {
                rb.getStyleClass().add("respuesta-correcta");
            }
        }

        if (!seleccionada.getValidity()) {
            ((RadioButton) grupo.getSelectedToggle())
                    .getStyleClass().add("respuesta-incorrecta");
        }
    }

    private void seleccionarRespuestaGuardada(Answer guardada) {
        for (Toggle t : grupo.getToggles()) {
            if (t.getUserData() == guardada) {
                grupo.selectToggle(t);
                break;
            }
        }
    }

    private void limpiarEstilos() {
        r1Px.getStyleClass().removeAll("respuesta-correcta", "respuesta-incorrecta");
        r2Px.getStyleClass().removeAll("respuesta-correcta", "respuesta-incorrecta");
        r3Px.getStyleClass().removeAll("respuesta-correcta", "respuesta-incorrecta");
        r4Px.getStyleClass().removeAll("respuesta-correcta", "respuesta-incorrecta");
    }

    private void habilitarRadios(boolean enabled) {
        r1Px.setDisable(!enabled);
        r2Px.setDisable(!enabled);
        r3Px.setDisable(!enabled);
        r4Px.setDisable(!enabled);
    }

    // =========================
    // ANTERIOR / SIGUIENTE
    // =========================
    @FXML
    private void irAnt() {
        indexActual--;
        if (indexActual < 0) {
            indexActual = allProblems.size() - 1;
        }
        cargarProblema();
    }

    @FXML
    private void irSig() {
        indexActual = (indexActual + 1) % allProblems.size();
        cargarProblema();
    }

    
    @FXML
    private void salirMapa(ActionEvent event) throws IOException {

        FXMLLoader loader
                = new FXMLLoader(getClass().getResource("listapr.fxml"));
        Parent root = loader.load();

        ListaprController controller = loader.getController();
        controller.setUser(currentUser);

        Stage stage
                = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
