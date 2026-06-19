/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ASUS
 */
package main;

import view.main.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Jalankan GUI di Event Dispatch Thread (EDT) agar aman
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Tampilkan jendela login
                new LoginFrame().setVisible(true);
            }
        });
    }
}
