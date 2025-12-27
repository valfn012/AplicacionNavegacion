package AplicacionNavegacion;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.NavDAOException;
import model.Navigation;
import model.Problem;
import model.User;

public class ListaprController implements Initializable {

    private List<Problem> problems;
    private User currentUser;

    /* =========================
       INIT
       ========================= */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // NO cargamos problemas aquí
    }

    /* =========================
       USER
       ========================= */
    public void setUser(User user) {
        this.currentUser = user;
        cargarProblemas();
    }

    private void cargarProblemas() {
        try {
            Navigation nav = Navigation.getInstance();
            problems = nav.getProblems();

            if (problems == null || problems.isEmpty()) {
                error(
                    "Sin problemas",
                    "No hay problemas disponibles",
                    "La base de datos no contiene problemas."
                );
            }

        } catch (NavDAOException e) {
            error(
                "Error",
                "Error cargando problemas",
                "No se han podido recuperar los problemas."
            );
        }
    }

    /* =========================
       PREGUNTA X
       ========================= */
    @FXML
    private void selectProblem(ActionEvent event) throws IOException {

        if (problems == null || problems.isEmpty()) {
            error(
                "Error",
                "No hay problemas",
                "No hay problemas cargados para seleccionar."
            );
            return;
        }

        Button boton = (Button) event.getSource();

        // Texto tipo: "Pregunta 12"
        String texto = boton.getText();
        int numeroProblema;

        try {
            numeroProblema =
                Integer.parseInt(texto.replace("Pregunta", "").trim());
        } catch (NumberFormatException e) {
            error(
                "Error",
                "Formato incorrecto",
                "No se pudo identificar el número del problema."
            );
            return;
        }

        if (numeroProblema < 1 || numeroProblema > problems.size()) {
            error(
                "Error",
                "Problema inexistente",
                "Ese problema no existe."
            );
            return;
        }

        Problem seleccionado = problems.get(numeroProblema - 1);
        abrirResolucion(seleccionado, numeroProblema);
    }

    /* =========================
       ALEATORIO
       ========================= */
    @FXML
    private void selectRandomProblem(ActionEvent event) throws IOException {

        if (problems == null || problems.isEmpty()) {
            error(
                "Error",
                "No hay problemas",
                "No hay problemas cargados para seleccionar."
            );
            return;
        }

        int randomIndex = new Random().nextInt(problems.size());
        Problem seleccionado = problems.get(randomIndex);

        abrirResolucion(seleccionado, randomIndex + 1);
    }

    /* =========================
       ABRIR RESOLUCIÓN
       ========================= */
    private void abrirResolucion(Problem problem, int numero)
            throws IOException {

        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("resolprom.fxml"));
        Parent root = loader.load();

        ResolpromController controller = loader.getController();
        controller.setUser(currentUser);
        controller.setProblem(problem, numero, problems);

        Stage stage = new Stage();
        stage.setTitle("Resolución del problema " + numero);
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.show();
    }

    /* =========================
       ALERTAS
       ========================= */
    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
