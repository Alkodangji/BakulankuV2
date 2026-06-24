package helper;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

public enum AppIcon {

    HOME("\uE88A", 18f, 24, 24),
    SALES("\uE8CC", 18f, 24, 24),
    BBM("\uE546", 18f, 24, 24),
    BRILINK("\uE84F", 18f, 24, 24),
    FINANCE("\uE227", 18f, 24, 24),
    REPORT("\uE915", 18f, 24, 24),

    HISTORY("\uE889", 18f, 24, 24),
    CATEGORY("\uE429", 18f, 24, 24),

    BBM_SMALL("\uE546", 18f, 24, 24),
    BBM_BIG("\uE546", 24f, 32, 32);

    private final String unicode;
    private final float size;
    private final int width;
    private final int height;

    private static Font OUTLINED;

    static {

        try (InputStream is = AppIcon.class.getResourceAsStream(
                "/assets/fonts/MaterialIconsOutlined-Regular.otf")) {

            OUTLINED = Font.createFont(
                    Font.TRUETYPE_FONT,
                    is
            );

        } catch (Exception ex) {

            ex.printStackTrace();

            OUTLINED = new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    18
            );

        }

    }

    AppIcon(
            String unicode,
            float size,
            int width,
            int height
    ) {

        this.unicode = unicode;
        this.size = size;
        this.width = width;
        this.height = height;

    }

    public FontIcon create(Color color) {

        return new FontIcon(
                unicode,
                OUTLINED.deriveFont(size),
                color,
                width,
                height
        );

    }

}