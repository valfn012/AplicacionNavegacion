package AplicacionNavegacion;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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

import model.Session;
import model.User;

public class VentanaHistorialController implements Initializable {

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

    @FXML
    private DatePicker dateFilter;
    @FXML
    private Button bVolver;

    private User activeUser;

    private final ObservableList<Session> allDailySessions
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configColumns();

        dateFilter.valueProperty().addListener((obs, oldV, newV) -> filterByDate());
    }

    public void setUser(User u) {
        this.activeUser = u;
        loadHistory();
    }

    /* =========================
       CONFIGURACIÓN DE COLUMNAS
       ========================= */
    private void configColumns() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colDate.setCellValueFactory(cellData
                -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue()
                                .getTimeStamp()
                                .toLocalDate()
                                .format(formatter)
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

    /* =========================
       CARGA Y AGRUPACIÓN
       ========================= */
    private void loadHistory() {

    if (activeUser == null || activeUser.getSessions() == null) {
        return;
    }

    Map<LocalDate, Session> resumenPorDia = new HashMap<>();

    for (Session s : activeUser.getSessions()) {

        LocalDate fecha = s.getTimeStamp().toLocalDate();

        if (!resumenPorDia.containsKey(fecha)) {

            resumenPorDia.put(
                fecha,
                new Session(
                    s.getTimeStamp(),
                    s.getHits(),
                    s.getFaults()
                )
            );

        } else {

            Session acumulada = resumenPorDia.get(fecha);

            Session nueva = new Session(
                acumulada.getTimeStamp(),
                acumulada.getHits() + s.getHits(),
                acumulada.getFaults() + s.getFaults()
            );

            // 🔴 ESTO ES LO QUE FALTABA
            resumenPorDia.put(fecha, nueva);
        }
    }

    allDailySessions.setAll(resumenPorDia.values());
    tableHistory.setItems(allDailySessions);
}

    /* =========================
       FILTRO POR FECHA
       ========================= */
    private void filterByDate() {

        LocalDate selected = dateFilter.getValue();

        if (selected == null) {
            tableHistory.setItems(allDailySessions);
            return;
        }

        ObservableList<Session> filtered = FXCollections.observableArrayList();

        for (Session s : allDailySessions) {
            if (!s.getTimeStamp().toLocalDate().isBefore(selected)) {
                filtered.add(s);
            }
        }

        tableHistory.setItems(filtered);
    }

    /* =========================
       VOLVER
       ========================= */
    @FXML
    private void onVolver() {
        try {
            FXMLLoader loader
                    = new FXMLLoader(getClass().getResource("FXMLVentanaMapa.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) bVolver.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setMaximized(true);

            VentanaMapaController controller = loader.getController();
            controller.setUser(activeUser);
            controller.setStage(stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
