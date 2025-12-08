package AplicacionNavegacion;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Session;
import model.User;

public class FXMLVentanaHistorialController implements Initializable {

    @FXML
    private TableView<Session> tableHistory;

    @FXML
    private TableColumn<Session, String> colDate;

    @FXML
    private TableColumn<Session, Integer> colHits;

    @FXML
    private TableColumn<Session, Integer> colFaults;

    @FXML
    private TableColumn<Session, String> colRate;

    // Usuario enviado desde la ventana principal
    private User activeUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configColumns();
    }

    /**
     * Método que la ventana principal usará para pasar el usuario activo.
     */
    public void setUser(User u) {
        this.activeUser = u;
        loadHistory(); // cargamos historial cuando recibimos el usuario
    }

    private void configColumns() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Formatear fecha/hora
        colDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTimeStamp().format(formatter)
                )
        );

        colHits.setCellValueFactory(new PropertyValueFactory<>("hits"));
        colFaults.setCellValueFactory(new PropertyValueFactory<>("faults"));

        // Tasa de acierto
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

    private void loadHistory() {
        if (activeUser == null) return;

        tableHistory.setItems(
                FXCollections.observableArrayList(activeUser.getSessions())
        );
    }
}
