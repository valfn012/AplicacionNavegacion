package AplicacionNavegacion;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import model.Session;
import model.User;

public class VentanaHistorialController implements Initializable {

    @FXML
    private TableView<Session> tableHistory;

    @FXML private TableColumn<Session, String> colDate;
    @FXML
    private TableColumn<Session, Integer> colHits;
    @FXML
    private TableColumn<Session, Integer> colFaults;
    @FXML
    private TableColumn<Session, String> colRate;

    @FXML
    private DatePicker dateFilter;
    @FXML
    private Button bVolver;

    private User activeUser;

    private final ObservableList<Session> allSessions = FXCollections.observableArrayList();
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configColumns();

        // Cuando cambie la fecha del DatePicker → filtrar
        dateFilter.valueProperty().addListener((obs, oldV, newV) -> filterByDate());
    }

    /**
     * Recibe el usuario activo desde la ventana anterior.
     */
    public void setUser(User u) {
        this.activeUser = u;
        loadHistory();
    }

    /**
     * Configurar columnas de la tabla.
     */
    private void configColumns() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTimeStamp().format(formatter)
                )
        );

        colHits.setCellValueFactory(new PropertyValueFactory<>("hits"));
        colFaults.setCellValueFactory(new PropertyValueFactory<>("faults"));

        colRate.setCellValueFactory(cellData -> {
            int h = cellData.getValue().getHits();
            int f = cellData.getValue().getFaults();
            int total = h + f;

            String rate = (total == 0)
                    ? "0%"
                    : String.format("%.1f%%", (h * 100.0 / total));

            return new javafx.beans.property.SimpleStringProperty(rate);
        });
    }

    /**
     * Cargar historial completo del usuario.
     */
    private void loadHistory() {
        if (activeUser == null) return;

        allSessions.setAll(activeUser.getSessions());
        tableHistory.setItems(allSessions);
    }

    /**
     * Filtrar sesiones por fecha seleccionada.
     */
    private void filterByDate() {
        LocalDate selected = dateFilter.getValue();

        if (selected == null) {
            tableHistory.setItems(allSessions);
            return;
        }

        ObservableList<Session> filtered = FXCollections.observableArrayList();

        for (Session s : allSessions) {
            if (s.getTimeStamp().toLocalDate().isEqual(selected)) {
                filtered.add(s);
            }
        }

        tableHistory.setItems(filtered);
    }

    /**
     * Acción del botón Volver.
     */
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLVentanaMapa.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) bVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
