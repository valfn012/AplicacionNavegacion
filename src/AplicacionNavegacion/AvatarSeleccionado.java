/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AplicacionNavegacion;

import javafx.scene.image.Image;

/**
 *
 * @author LERI
 */
class AvatarSeleccionado {
    private static Image avatar;

    public static void setAvatar(Image img) {
        avatar = img;
    }

    public static Image getAvatar() {
        return avatar;
    }
}
