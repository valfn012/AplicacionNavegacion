package AplicacionNavegacion;

import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Navigation;
import model.Problem;
import model.User;

public class ListaprController implements Initializable {

    private List<Problem> problems;
    private User currentUser;

    @FXML
    private VBox vboxPreguntas;

    /* =========================
       INITIALIZE
       ========================= */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Navigation navigation = Navigation.getInstance();
            problems = navigation.getProblems();
            cargarListaProblemas();
        } catch (Exception e) {
            error(
                "Error",
                "Error cargando problemas",
                "No se pudieron cargar los problemas."
            );
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    /* =========================
       CARGAR LISTA DE PROBLEMAS
       ========================= */
    private void cargarListaProblemas() {

        vboxPreguntas.getChildren().clear();

        if (problems == null || problems.isEmpty()) {
            return;
        }

        for (int i = 0; i < problems.size(); i++) {

            Problem problem = problems.get(i);
            int numero = i + 1;

            HBox fila = new HBox();
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setSpacing(10);

            Button btn = new Button("Pregunta " + numero);
            btn.setPrefWidth(160);

            btn.setOnAction(e -> abrirResolucion(problem, numero));

            fila.getChildren().add(btn);
            vboxPreguntas.getChildren().add(fila);
        }
    }

    /* =========================
       PROBLEMA ALEATORIO
       ========================= */
    @FXML
    private void selectRandomProblem() {

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
    private void abrirResolucion(Problem problem, int numero) {

        try {
            FXMLLoader loader =
                new FXMLLoader(getClass().getResource("resolprom.fxml"));

            Parent root = loader.load();

            ResolpromController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setProblem(problem, numero, problems);

            // 👉 USAMOS EL STAGE ACTUAL (cerramos Listapr)
            Stage stage = (Stage) vboxPreguntas.getScene().getWindow();
            stage.setTitle("Resolución del problema " + numero);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            error(
                "Error",
                "No se pudo abrir el problema",
                "Error al abrir la ventana de resolución."
            );
        }
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
