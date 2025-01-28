package ua.edu.chmnu.network.java.downloader.view;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ua.edu.chmnu.network.java.downloader.model.DownloadTask;

public class DownloadManagerUI extends JFrame {
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private DownloadTask currentDownloadTask;
    private JProgressBar progressBar;

    public DownloadManagerUI() {
        setTitle("Multi-Threaded File Downloader");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JTextField urlField = new JTextField(30);
        JButton startButton = new JButton("Start");
        JButton pauseButton = new JButton("Pause");
        JButton resumeButton = new JButton("Resume");

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 20));
        progressBar.setStringPainted(true);

        add(urlField);
        add(startButton);
        add(pauseButton);
        add(resumeButton);
        add(progressBar);

        startButton.addActionListener(e -> {
            String fileURL = urlField.getText();
            if (!fileURL.isEmpty()) {
                File saveFile = new File("downloaded_" + System.currentTimeMillis() + ".file");
                currentDownloadTask = new DownloadTask(fileURL, saveFile, progressBar);
                executor.submit(currentDownloadTask);
            }
        });

        pauseButton.addActionListener(e -> {
            if (currentDownloadTask != null) {
                currentDownloadTask.pause();
            }
        });

        resumeButton.addActionListener(e -> {
            if (currentDownloadTask != null) {
                currentDownloadTask.resume();
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DownloadManagerUI::new);
    }
}