package ua.edu.chmnu.network.java.downloader.model;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask implements Runnable {
    private final String fileURL;
    private final File saveFile;
    private final JProgressBar progressBar;
    private volatile boolean paused = false;
    private volatile boolean stopped = false;

    public DownloadTask(String fileURL, File saveFile, JProgressBar progressBar) {
        this.fileURL = fileURL;
        this.saveFile = saveFile;
        this.progressBar = progressBar;
    }

    public void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notify();  // Notify waiting threads to resume
    }

    public void stop() {
        stopped = true;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();
            connection.setRequestMethod("GET");
            int fileSize = connection.getContentLength(); // Get total file size

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(saveFile, true)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                int downloaded = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    synchronized (this) {
                        while (paused) {
                            wait(); // Wait until resumed
                        }
                        if (stopped) {
                            break; // Stop downloading
                        }
                    }

                    out.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;

                    // Update progress bar in the Swing UI thread
                    int progress = (int) ((downloaded * 100L) / fileSize);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                }
            }

            System.out.println("Download complete: " + fileURL);
            SwingUtilities.invokeLater(() -> progressBar.setValue(100)); // Ensure full completion

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
