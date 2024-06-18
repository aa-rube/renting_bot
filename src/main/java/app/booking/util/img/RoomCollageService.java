package app.booking.util.img;


import app.booking.sheets.model.Room;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class RoomCollageService {

    public static File[] donloasInManyThreads(Room room) {
        ExecutorService executorService = Executors.newFixedThreadPool(room.getLinks().size());

        List<Callable<File>> tasks = new ArrayList<>();

        for (String link : room.getLinks()) {
            tasks.add(() -> DownloadFile.downloadFileFromURL(link,
                    String.valueOf(System.currentTimeMillis()), ".jpg"));
        }

        List<File> downloadedFiles = new ArrayList<>();
        try {
            List<Future<File>> futures = executorService.invokeAll(tasks);
            for (Future<File> future : futures) {
                downloadedFiles.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return downloadedFiles.toArray(new File[0]);
    }

}