package helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import raven.datetime.DatePicker;

/**
 * Small UI-foundation helpers for FlatLaf based module styling.
 */
public final class UiThemeUtil {

    public static final Color PENJUALAN = new Color(0x000000);
    public static final Color KEUANGAN = new Color(0x009900);
    public static final Color BRILINK = new Color(0x0466c8);
    public static final Color BBM = new Color(0xFF0000);

    private UiThemeUtil() {
    }

    public static void applyTextFieldClearButton(Container root) {
        for (Component component : root.getComponents()) {
            if (component instanceof Container) {
                applyTextFieldClearButton((Container) component);
            }
            if (component instanceof JTextField) {
                JTextField field = (JTextField) component;
                String name = field.getName();
                if (name != null
                        && name.startsWith("Txt")
                        && !name.toLowerCase().contains("tgl")
                        && field.isEditable()) {
                    field.putClientProperty("JTextField.showClearButton", true);
                }
            }
        }
    }

    public static void styleDatePicker(DatePicker datePicker, JTextField editor, String flatLafColorToken) {
        datePicker.setEditor(editor);
        datePicker.setColor(Color.BLACK);
        datePicker.putClientProperty(
                "FlatLaf.style",
                "background:" + flatLafColorToken + ";"
        );
        datePicker.setSelectionArc(20);
        datePicker.setEditorValidation(true);
        datePicker.setValidationOnNull(true);
        datePicker.now();
    }

    public static void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.putClientProperty(
                "FlatLaf.style",
                "arc:12;"
        );
    }

    public static void styleField(JComponent component, String flatLafColorToken) {
        component.putClientProperty(
                "FlatLaf.style",
                "borderColor:" + flatLafColorToken + ";"
                        + "focusedBorderColor:" + flatLafColorToken + ";"
        );
    }
}
