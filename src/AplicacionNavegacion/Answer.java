/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacionNavegacion;

/**
 *
 * @author LERI
 */
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Answer {

    // Texto de la respuesta
    private final StringProperty text;

    // Validez de la respuesta (true = correcta, false = incorrecta)
    private final BooleanProperty validity;

    /**
     * Constructor de Answer.
     *
     * @param text     Texto de la respuesta
     * @param validity Indica si es la respuesta correcta
     */
    public Answer(String text, boolean validity) {
        this.text = new SimpleStringProperty(text);
        this.validity = new SimpleBooleanProperty(validity);
    }

    // Getter del texto
    public String getText() {
        return text.get();
    }

    // Propiedad para JavaFX
    public StringProperty textProperty() {
        return text;
    }

    // Getter de validez
    public boolean isValid() {
        return validity.get();
    }

    // Propiedad boolean para JavaFX
    public BooleanProperty validityProperty() {
        return validity;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "text=" + text.get() +
                ", validity=" + validity.get() +
                '}';
    }
}

