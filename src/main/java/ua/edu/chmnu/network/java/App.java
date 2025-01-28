package ua.edu.chmnu.network.java;

import javax.swing.*;
import ua.edu.chmnu.network.java.downloader.view.DownloadManagerUI;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DownloadManagerUI::new);
    }
}
