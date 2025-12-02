/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacionNavegacion;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.ArrayList;

/**
 *
 * @author LERI
 */
public class Problem {
   
    // Texto del enunciado como propiedad JavaFX
    private final StringProperty text;

    // Lista de respuestas (4 respuestas tipo Answer)
    private final ArrayList<Answer> answers;

    /**
     * Constructor de Problem.
     * Este objeto normalmente se crea al cargar los problemas desde la base de datos.
     *
     * @param text     Texto del enunciado
     * @param answers  Lista de respuestas (4 Answer)
     */
    public Problem(String text, ArrayList<Answer> answers) {
        this.text = new SimpleStringProperty(text);
        this.answers = answers;
    }

    // Getter para el texto
    public String getText() {
        return text.get();
    }

    // Propiedad para JavaFX (binding)
    public StringProperty textProperty() {
        return text;
    }

    // Getter para la lista de respuestas
    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "text=" + text.get() +
                ", answers=" + answers +
                '}';
    }

}
