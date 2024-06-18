package app.booking.util.img;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class DownloadFile {

    public static java.io.File downloadFileFromURL(String fileUrl, String prefix, String suffix) {
        try {
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            java.io.File outputFile = java.io.File.createTempFile(prefix, suffix);

            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return outputFile;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
