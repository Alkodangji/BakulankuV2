package helper;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Method;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import raven.datetime.DatePicker;



public final class DatePickerHelper {

    private static final String DISPLAY_PATTERN = "dd-MM-yyyy";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DISPLAY_PATTERN);

    private static final Color DEFAULT_BACKGROUND = Color.WHITE;
    private static final Color READONLY_BACKGROUND = new Color(245, 245, 245);

    private DatePickerHelper() {
        // Utility class, tidak perlu dibuat object.
    }

    public static DatePicker install(JFormattedTextField field) {
        return install(field, LocalDate.now(), DEFAULT_BACKGROUND);
    }

    public static DatePicker install(JFormattedTextField field, LocalDate defaultDate) {
        return install(field, defaultDate, DEFAULT_BACKGROUND);
    }

    public static DatePicker install(
            JFormattedTextField field,
            LocalDate defaultDate,
            Color background
    ) {
        DatePicker datePicker = new DatePicker();

        applyStyle(field, background);

       
        datePicker.setEditor(field);

        if (defaultDate != null) {
            setDate(field, datePicker, defaultDate);
        } else {
            clear(field);
        }

        return datePicker;
    }

    public static void applyStyle(JFormattedTextField field) {
        applyStyle(field, DEFAULT_BACKGROUND);
    }

    public static void applyStyle(JFormattedTextField field, Color background) {
        field.setOpaque(true);
        field.setBackground(background);
        field.setHorizontalAlignment(SwingConstants.LEFT);
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 14f));

        field.putClientProperty("JTextField.placeholderText", DISPLAY_PATTERN);
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("FlatLaf.styleClass", "date-field");

        field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    }

    public static LocalDate getLocalDate(JFormattedTextField field) {
        String text = field.getText();

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(text.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Format tanggal tidak valid. Gunakan format " + DISPLAY_PATTERN + "."
            );
        }
    }

    public static LocalDate requireLocalDate(
            JFormattedTextField field,
            String fieldName
    ) {
        LocalDate date = getLocalDate(field);

        if (date == null) {
            throw new IllegalArgumentException(fieldName + " wajib diisi.");
        }

        return date;
    }

    public static Date getSqlDate(JFormattedTextField field) {
        LocalDate localDate = getLocalDate(field);

        if (localDate == null) {
            return null;
        }

        return Date.valueOf(localDate);
    }

    public static Date requireSqlDate(
            JFormattedTextField field,
            String fieldName
    ) {
        Date date = getSqlDate(field);

        if (date == null) {
            throw new IllegalArgumentException(fieldName + " wajib diisi.");
        }

        return date;
    }

    public static boolean validateRequired(
            JFormattedTextField field,
            String fieldName
    ) {
        try {
            requireSqlDate(field, fieldName);
            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    field,
                    e.getMessage(),
                    "Validasi Tanggal",
                    JOptionPane.WARNING_MESSAGE
            );
            field.requestFocus();
            return false;
        }
    }

    public static void setDate(
            JFormattedTextField field,
            DatePicker datePicker,
            LocalDate localDate
    ) {
        if (localDate == null) {
            clear(field);
            return;
        }

        field.setText(localDate.format(FORMATTER));
        syncDatePicker(datePicker, localDate);
    }

    public static void setDate(
            JFormattedTextField field,
            LocalDate localDate
    ) {
        if (localDate == null) {
            clear(field);
            return;
        }

        field.setText(localDate.format(FORMATTER));
    }

    public static void clear(JFormattedTextField field) {
        field.setText("");
    }

    public static void resetToToday(
            JFormattedTextField field,
            DatePicker datePicker
    ) {
        setDate(field, datePicker, LocalDate.now());
    }

    public static void setReadOnly(
            JFormattedTextField field,
            boolean readOnly
    ) {
        field.setEditable(!readOnly);

        if (readOnly) {
            field.setBackground(READONLY_BACKGROUND);
        } else {
            field.setBackground(DEFAULT_BACKGROUND);
        }
    }

    private static void syncDatePicker(DatePicker datePicker, LocalDate localDate) {
        if (datePicker == null || localDate == null) {
            return;
        }

       
        try {
            Method method = datePicker.getClass()
                    .getMethod("setSelectedDate", LocalDate.class);

            method.invoke(datePicker, localDate);
        } catch (Exception ignored) {
            // Tidak wajib. Field tetap terisi walaupun DatePicker tidak punya method ini.
        }
    }
}