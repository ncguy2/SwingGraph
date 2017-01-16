package net.ncguy.graph.data.icons;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Guy on 15/01/2017.
 */
public class Icons {

    public static BufferedImage loadIcon(Icon icon) {
        try {
            return ImageIO.read(Icons.class.getResourceAsStream(icon.path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum Icon {
        WARNING_WHITE("icons/warning_white@x4.png"),
        TICK_WHITE("icons/tick_white@x4.png"),
        ;
        public final String path;
        Icon(String path) {
            this.path = path;
        }

        // Helper

        public BufferedImage loadIcon() {
            return Icons.loadIcon(this);
        }

    }

}
