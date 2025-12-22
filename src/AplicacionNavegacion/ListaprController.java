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
import javafx.scene.Node;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Navigation nav = Navigation.getInstance();
            problems = nav.getProblems();
        } catch (NavDAOException e) {
            error(
                "Error",
                "Error cargando problemas",
                "No se han podido recuperar los problemas."
            );
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    // =========================
    // PREGUNTA X
    // =========================
    @FXML
    private void selectProblem(ActionEvent event) throws IOException {

        Button boton = (Button) event.getSource();

        // Texto tipo: "Pregunta 12"
        String texto = boton.getText();
        int numeroProblema =
            Integer.parseInt(texto.replace("Pregunta", "").trim());

        Problem seleccionado = problems.get(numeroProblema - 1);

        abrirResolucion(event, seleccionado, numeroProblema);
    }

    // =========================
    // ALEATORIO
    // =========================
    @FXML
    private void selectRandomProblem(ActionEvent event) throws IOException {

        int randomIndex = new Random().nextInt(problems.size());
        Problem seleccionado = problems.get(randomIndex);

        abrirResolucion(event, seleccionado, randomIndex + 1);
    }

    // =========================
    // CAMBIO DE ESCENA
    // =========================
    private void abrirResolucion(
            ActionEvent event,
            Problem problem,
            int numero
    ) throws IOException {

        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("resolprom.fxml"));
        Parent root = loader.load();

        ResolpromController controller = loader.getController();
        controller.setUser(currentUser);
        controller.setProblem(problem, numero, problems);

        Stage stage =
            (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void error(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
