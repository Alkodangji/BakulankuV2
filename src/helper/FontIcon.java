package helper;

import javax.swing.*;
import java.awt.*;

public class FontIcon implements Icon {

    private final String unicode;
    private Font font;
    private final Color color;
    private final int width;
    private final int height;

    public FontIcon(
            String unicode,
            Font font,
            Color color,
            int width,
            int height
    ) {

        this.unicode = unicode;
        this.font = font;
        this.color = color;
        this.width = width;
        this.height = height;

    }

    

    

    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public void paintIcon(
            Component c,
            Graphics g,
            int x,
            int y
    ) {

        Graphics2D g2d =
                (Graphics2D) g.create();

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        g2d.setFont(font);
        g2d.setColor(color);

        FontMetrics fm =
                g2d.getFontMetrics();

        int textWidth =
                fm.stringWidth(unicode);

        int textHeight =
                fm.getAscent();

        int textX =
                x + (width - textWidth) / 2;

        int textY =
                y + (height - textHeight) / 2
                        + fm.getAscent();

        g2d.drawString(
                unicode,
                textX,
                textY
        );

        g2d.dispose();

    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

}