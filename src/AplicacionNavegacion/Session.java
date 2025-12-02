/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacionNavegacion;

import java.time.LocalDateTime;

/**
 *
 * @author LERI
 */
public class Session {
    // Atributos inmutables
    private final LocalDateTime timeStamp;
    private final int hits;
    private final int faults;

    /**
     * Constructor de la clase Session.
     * Se debe llamar cuando el usuario cierra la sesión.
     *
     * @param timeStamp Momento en que se registra la sesión.
     * @param hits Número de aciertos.
     * @param faults Número de fallos.
     */
    public Session(LocalDateTime timeStamp, int hits, int faults) {
        this.timeStamp = timeStamp;
        this.hits = hits;
        this.faults = faults;
    }

    // Getters (no hay setters para mantener la inmutabilidad)
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public int getHits() {
        return hits;
    }

    public int getFaults() {
        return faults;
    }

    @Override
    public String toString() {
        return "Session{" +
                "timeStamp=" + timeStamp +
                ", hits=" + hits +
                ", faults=" + faults +
                '}';
    }
}
