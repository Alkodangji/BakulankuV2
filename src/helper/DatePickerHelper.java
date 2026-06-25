package helper;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Method;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import raven.datetime.DatePicker;

public final class DatePickerHelper {

    public static final String DISPLAY_PATTERN = "dd/MM/yyyy";

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private static final DateTimeFormatter LEGACY_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu");

    // Warna field input tanggal
    private static final Color FIELD_BACKGROUND = Color.WHITE;
    private static final Color FIELD_FOREGROUND = Color.BLACK;
    private static final Color FIELD_READONLY_BACKGROUND = new Color(245, 245, 245);

    // Warna kalender popup
    private static final Color CALENDAR_COLOR = Color.BLACK;

    private DatePickerHelper() {
        // Utility class.
    }

    public static DatePicker install(JFormattedTextField field) {
        return install(field, LocalDate.now(), FIELD_BACKGROUND);
    }

    public static DatePicker install(JFormattedTextField field, LocalDate defaultDate) {
        return install(field, defaultDate, FIELD_BACKGROUND);
    }

    public static DatePicker install(
        JFormattedTextField field,
        LocalDate defaultDate,
        Color background
) {
    Objects.requireNonNull(field, "field tidak boleh null");

    DatePicker datePicker = new DatePicker();

    configureDatePicker(datePicker, field);
    applyStyle(field, background == null ? FIELD_BACKGROUND : background);

    if (defaultDate != null) {
        setDate(field, datePicker, defaultDate);
    } else {
        clear(field);
    }

    return datePicker;
}
    
    public static void applyStyle(JFormattedTextField field) {
        applyStyle(field, FIELD_BACKGROUND);
    }

    public static void applyStyle(JFormattedTextField field, Color background) {
    if (field == null) {
        return;
    }

    field.setOpaque(true);
    field.setBackground(background == null ? FIELD_BACKGROUND : background);
    field.setForeground(FIELD_FOREGROUND);
    field.setCaretColor(FIELD_FOREGROUND);
    field.setDisabledTextColor(FIELD_FOREGROUND);

    field.setHorizontalAlignment(SwingConstants.LEFT);

    Font currentFont = field.getFont();
    if (currentFont != null) {
        field.setFont(currentFont.deriveFont(Font.PLAIN, 14f));
    }

    field.putClientProperty("JTextField.placeholderText", DISPLAY_PATTERN);

    /*
     * Jangan null-kan styleClass kalau project lu pakai FlatLaf theme.
     * Biarkan field ikut style aplikasi.
     */
    field.putClientProperty("FlatLaf.styleClass", null);

    field.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
}
    
    public static LocalDate getLocalDate(JFormattedTextField field) {
        if (field == null) {
            return null;
        }

        String text = field.getText();

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String value = text.trim();

        try {
            return LocalDate.parse(value, DISPLAY_FORMATTER);
        } catch (DateTimeParseException ignored) {
            // Coba format lama dd-MM-yyyy.
        }

        try {
            return LocalDate.parse(value, LEGACY_FORMATTER);
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

            if (field != null) {
                field.requestFocus();
            }

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

    if (field != null) {
        field.setText(localDate.format(DISPLAY_FORMATTER));
        applyStyle(field, FIELD_BACKGROUND);
    }

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

    if (field != null) {
        field.setText(localDate.format(DISPLAY_FORMATTER));
        applyStyle(field, FIELD_BACKGROUND);
    }
}
   
    public static void clear(JFormattedTextField field) {
    if (field != null) {
        field.setText("");
        applyStyle(field, FIELD_BACKGROUND);
    }
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
        if (field == null) {
            return;
        }

        field.setEditable(!readOnly);

        if (readOnly) {
            field.setOpaque(true);
            field.setBackground(FIELD_READONLY_BACKGROUND);
            field.setForeground(FIELD_FOREGROUND);
            field.setDisabledTextColor(FIELD_FOREGROUND);
        } else {
            applyStyle(field, FIELD_BACKGROUND);
        }
    }

    private static void configureDatePicker(DatePicker datePicker, JFormattedTextField field) {
    if (datePicker == null || field == null) {
        return;
    }

    datePicker.setEditor(field);

    
    datePicker.setColor(Color.decode("#F97316"));
    datePicker.setBackground(Color.decode("#0F1115"));

    invokeIfExists(
            datePicker,
            "setSelectionArc",
            new Class[]{int.class},
            new Object[]{12}
    );

    invokeIfExists(
            datePicker,
            "setEditorValidation",
            new Class[]{boolean.class},
            new Object[]{true}
    );

    invokeIfExists(
            datePicker,
            "setValidationOnNull",
            new Class[]{boolean.class},
            new Object[]{true}
    );

    invokeIfExists(
            datePicker,
            "setDateFormat",
            new Class[]{String.class},
            new Object[]{DISPLAY_PATTERN}
    );
}
    private static void syncDatePicker(DatePicker datePicker, LocalDate localDate) {
        if (datePicker == null || localDate == null) {
            return;
        }

        invokeIfExists(
                datePicker,
                "setSelectedDate",
                new Class[]{LocalDate.class},
                new Object[]{localDate}
        );
    }

    private static void invokeIfExists(
            Object target,
            String methodName,
            Class<?>[] parameterTypes,
            Object[] args
    ) {
        if (target == null) {
            return;
        }

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            method.invoke(target, args);
        } catch (Exception ignored) {
            // Beda versi DatePicker bisa beda method. Aman diabaikan.
        }
    }
}