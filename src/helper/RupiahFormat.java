package helper;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTextField;

public class RupiahFormat {

    private static final Locale LOCALE_ID = new Locale("id", "ID");

    public static String format(long angka) {
        NumberFormat format = NumberFormat.getCurrencyInstance(LOCALE_ID);
        format.setMaximumFractionDigits(0);
        return format.format(angka);
    }

    // Tambahan
    public static String format(double angka) {
        NumberFormat format = NumberFormat.getCurrencyInstance(LOCALE_ID);
        format.setMaximumFractionDigits(0);
        return format.format(angka);
    }

    // Tambahan
    public static String format(String angka) {
        try {
            return format(Double.parseDouble(angka));
        } catch (Exception e) {
            return "Rp 0";
        }
    }

    public static double parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String angka = text.replaceAll("[^0-9]", "");

        if (angka.isEmpty()) {
            return 0;
        }

        return Double.parseDouble(angka);
    }

    public static void formatTextField(JTextField textField) {
        String text = textField.getText();

        String angka = text.replaceAll("[^0-9]", "");

        if (angka.isEmpty()) {
            textField.setText("");
            return;
        }

        long nominal = Long.parseLong(angka);

        textField.setText(format(nominal));
        textField.setCaretPosition(textField.getText().length());
    }
}