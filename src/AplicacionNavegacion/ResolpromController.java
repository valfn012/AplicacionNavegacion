/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package AplicacionNavegacion;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    @FXML private Label problemaX;
    @FXML private Label enunciadoPx;

    @FXML private RadioButton r1Px;
    @FXML private RadioButton r2Px;
    @FXML private RadioButton r3Px;
    @FXML private RadioButton r4Px;

    @FXML private Button bAnterior;
    @FXML private Button bSiguiente;
    @FXML private Button corregir;
    @FXML private Button bSalir;

    // ===== STATE =====
    private ToggleGroup grupo;
    private User currentUser;

    private List<Problem> allProblems;
    private int indexActual;

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

    public void setProblem(
            Problem problem,
            int numero,
            List<Problem> lista
    ) {
        this.allProblems = lista;
        this.indexActual = numero - 1;
        cargarProblema();
    }

    // =========================
    // CARGA DEL PROBLEMA
    // =========================
    private void cargarProblema() {

        Problem p = allProblems.get(indexActual);

        problemaX.setText("Problema " + (indexActual + 1));
        enunciadoPx.setText(p.getText());

        List<Answer> answers = p.getAnswers();

        r1Px.setText(answers.get(0).getText());
        r2Px.setText(answers.get(1).getText());
        r3Px.setText(answers.get(2).getText());
        r4Px.setText(answers.get(3).getText());

        r1Px.setUserData(answers.get(0));
        r2Px.setUserData(answers.get(1));
        r3Px.setUserData(answers.get(2));
        r4Px.setUserData(answers.get(3));

        grupo.selectToggle(null);
        habilitarRadios(true);
    }

    @FXML
    private void corregirEj(ActionEvent event) {

        Toggle selected = grupo.getSelectedToggle();
        if (selected == null) return;

        Answer respuesta = (Answer) selected.getUserData();

        if (respuesta.getValidity()) {
            System.out.println("Correcta");
        } else {
            System.out.println("Incorrecta");
        }

        habilitarRadios(false);
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
    private void irAnt(ActionEvent event) {
        indexActual--;
        if (indexActual < 0) {
            indexActual = allProblems.size() - 1;
        }
        cargarProblema();
    }

    @FXML
    private void irSig(ActionEvent event) {
        indexActual = (indexActual + 1) % allProblems.size();
        cargarProblema();
    }

    // =========================
    // SALIR
    // =========================
    @FXML
    private void salirMapa(ActionEvent event) throws IOException {

        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("listapr.fxml"));
        Parent root = loader.load();

        ListaprController controller = loader.getController();
        controller.setUser(currentUser);

        Stage stage =
            (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
