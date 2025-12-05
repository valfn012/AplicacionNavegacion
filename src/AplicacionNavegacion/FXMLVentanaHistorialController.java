/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package AplicacionNavegacion;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import model.Session;
import model.User;
import model.Navegacion;

/**
 * FXML Controller class
 *
 * @author LERI
 */
public class FXMLVentanaHistorialController implements Initializable {

    @FXML
    private TableView<?> tableHistorial;
    @FXML
    private TableColumn<?, ?> colFecha;
    @FXML
    private TableColumn<?, ?> colAciertos;
    @FXML
    private TableColumn<?, ?> colFallos;
    @FXML
    private TableColumn<?, ?> colTasa;
    @FXML
    private Button bCancel1;

    
    private User currentUser;

    private ObservableList<Session> listaOriginal;
    private ObservableList<Session> listaFiltrada;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
