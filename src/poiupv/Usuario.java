/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poiupv;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author LERI
 */
public class Usuario {
    
     private String email;
    private String password;

    public Usuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
    

