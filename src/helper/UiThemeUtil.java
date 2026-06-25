package helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import raven.datetime.DatePicker;

/**
 * Small UI-foundation helpers for FlatLaf based module styling.
 */
public final class UiThemeUtil {

    public static final String PENJUALAN_FIELD = "penjualanField";
    public static final String KEUANGAN_FIELD = "keuanganField";
    public static final String BRILINK_FIELD = "brilinkField";
    public static final String BRILINK_DATE_PICKER = "brilinkDatePicker";
    public static final String BBM_DATE_PICKER = "bbmDatePicker";
    public static final String PENJUALAN_DATE_PICKER = "penjualanDatePicker";
    public static final String KEUANGAN_DATE_PICKER = "keuanganDatePicker";
    public static final String BBM_BUTTON = "bbmButton";
    public static final String KEUANGAN_BUTTON = "keuanganButton";

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

    public static void styleDatePicker(DatePicker datePicker, JFormattedTextField editor, String styleClass) {
        datePicker.setEditor(editor);
        datePicker.setColor(Color.BLACK);
        datePicker.putClientProperty("FlatLaf.styleClass", styleClass);
        datePicker.setSelectionArc(20);
        datePicker.setEditorValidation(true);
        datePicker.setValidationOnNull(true);
        datePicker.now();
    }

    public static void styleButton(JButton button, String styleClass) {
        button.putClientProperty("FlatLaf.styleClass", styleClass);
    }

    public static void styleNamedPanels(Container root, String style, String... namePrefixes) {
        for (Component component : root.getComponents()) {
            if (component instanceof JPanel) {
                String name = component.getName();
                if (name != null) {
                    for (String prefix : namePrefixes) {
                        if (name.startsWith(prefix)) {
                            ((JPanel) component).putClientProperty("FlatLaf.style", style);
                            break;
                        }
                    }
                }
            }
            if (component instanceof Container) {
                styleNamedPanels((Container) component, style, namePrefixes);
            }
        }
    }



    public static void styleNamedTables(Container root, String style, String... namePrefixes) {
        for (Component component : root.getComponents()) {
            if (component instanceof JTable) {
                JTable table = (JTable) component;
                String name = table.getName();
                if (name != null) {
                    for (String prefix : namePrefixes) {
                        if (name.startsWith(prefix)) {
                            table.putClientProperty("FlatLaf.style", style);
                            JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, table);
                            if (scrollPane != null) {
                                scrollPane.putClientProperty("FlatLaf.style", style);
                            }
                            break;
                        }
                    }
                }
            }
            if (component instanceof Container) {
                styleNamedTables((Container) component, style, namePrefixes);
            }
        }
    }

    public static void styleField(JComponent component, String styleClass) {
        component.putClientProperty("FlatLaf.styleClass", styleClass);
    }

    public static Color penjualanColor() {
        return getThemeColor("accentColor", 0x000000);
    }

    public static Color brilinkColor() {
        return getThemeColor("briColor", 0x0466c8);
    }

    private static Color getThemeColor(String key, int fallbackRgb) {
        Color color = UIManager.getColor(key);
        return color != null ? color : new Color(fallbackRgb);
    }
}
