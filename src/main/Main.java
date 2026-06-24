package main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import view.main.LoginFrame;

public class Main {

    public static void main(String[] args) {

        FlatLaf.registerCustomDefaultsSource("theme");
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}