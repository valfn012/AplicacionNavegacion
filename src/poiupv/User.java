/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poiupv;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 *
 * @author LERI
 */
public class User {
    
   private final String nickName;           // No se puede modificar
    private String email;
    private String password;
    private Image avatar;
    private LocalDate birthdate;
    private final ArrayList<Session> sessions;

    // Constructor
    public User(String nickName, String email, String password, 
                Image avatar, LocalDate birthdate) {

        this.nickName = nickName;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.birthdate = birthdate;
        this.sessions = new ArrayList<>();
    }

    // ---------- GETTERS ----------
    public String getNickName() { return nickName; }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Image getAvatar() { return avatar; }
    public LocalDate getBirthdate() { return birthdate; }
    public ArrayList<Session> getSessions() { return sessions; }

    // ---------- SETTERS ----------
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setAvatar(Image avatar) { this.avatar = avatar; }

    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
}
    

