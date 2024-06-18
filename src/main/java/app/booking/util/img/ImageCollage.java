package app.booking.util.img;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageCollage {

    public static File createCollage(File image1, File image2, File image3) {
        try {
            BufferedImage img1 = ImageIO.read(image1);
            BufferedImage img2 = ImageIO.read(image2);
            BufferedImage img3 = ImageIO.read(image3);

            int collageWidth = Math.max(img1.getWidth(), img2.getWidth() + img3.getWidth());
            int collageHeight = Math.max(img1.getHeight(), img2.getHeight() + img3.getHeight());

            BufferedImage collage = new BufferedImage(collageWidth, collageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = collage.createGraphics();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, collageWidth, collageHeight);

            int img1Width = (int) ((double) img1.getWidth() / img1.getHeight() * collageHeight);

            BufferedImage croppedImg1 = img1.getSubimage(0, 0, Math.min(img1Width, img1.getWidth()),
                    Math.min(collageHeight, img1.getHeight()));

            g.drawImage(croppedImg1, 0, 0, collageWidth / 2, collageHeight, null);

            g.drawImage(img2, collageWidth / 2, 0, collageWidth / 2, collageHeight / 2, null);

            g.drawImage(img3, collageWidth / 2, collageHeight / 2, collageWidth / 2,
                    collageHeight / 2, null);

            g.dispose();

            File tempFile = Files.createTempFile("collage_", ".jpg").toFile();
            ImageIO.write(collage, "jpg", tempFile);

            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
