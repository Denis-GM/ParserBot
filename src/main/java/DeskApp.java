import webBots.WebBot;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.Component.RIGHT_ALIGNMENT;

public class DeskApp {
    private JLabel errorLabel;
    private JLabel labelFilePath;
    private String fileName = "Имя файла";
    private String filePath = "";

    public DeskApp(){
        AddLocalizationForJFileChooser();
        CreateGUI();
    }

    private void CreateGUI() {
        JFrame frame = new JFrame("WebParser");
        frame.setSize(500, 250);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setLocationRelativeTo(null); // окно в центре экрана
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel selectFilePanel = new JPanel();
        JPanel errorsPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Настройки");
        JMenuItem menuItem1 = new JMenuItem("example");
        JMenuItem menuItem2 = new JMenuItem("example1");
        menu.add(menuItem1);
        menu.add(menuItem2);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        JButton start = new JButton("Запустить");
        start.addActionListener(new Start());
        buttonsPanel.add(start);

        JButton stop = new JButton("Остановить");
        start.addActionListener(new Stop());
        buttonsPanel.add(stop);

        JButton selectFile = new JButton("Выбрать файл");
        selectFile.addActionListener(new SelectFileInDirectory());
        selectFilePanel.add(selectFile);

        errorLabel = new JLabel();
        errorsPanel.add(errorLabel);

        labelFilePath = new JLabel(fileName);
        labelFilePath.setAlignmentX(RIGHT_ALIGNMENT);
        selectFilePanel.add(labelFilePath);

        frame.getContentPane().add(BorderLayout.NORTH, selectFilePanel);
        frame.getContentPane().add(BorderLayout.CENTER, errorsPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, buttonsPanel);
        frame.setVisible(true);
    }

    private class SelectFileInDirectory implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Открыть файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileopen.getSelectedFile();
                fileName = file.getName();
                filePath = file.getPath();
                labelFilePath.setText(fileName);
                errorLabel.setText("Файл выбран");
            }
        }
    }

    private class Start implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Thread thread = new Thread() {
                public void run() {
                    errorLabel.setText("В процессе...");
                    try {
                        WebBot.Start("https://www.list-org.com/search?val=N/");
                        Excel.readFromExcel(filePath);
                    }
                    catch(Exception exception) {
                        errorLabel.setText("Ошибка");
                        System.out.println(exception);
                    }
                }
            };
            thread.start();
        }
    }

    private class Stop implements ActionListener {
        public void actionPerformed(ActionEvent e) {
//            WebBot.Stop();
            errorLabel.setText("Принудительное завершение");
        }
    }

    private void AddLocalizationForJFileChooser() {
        UIManager.put("FileChooser.saveButtonText"      , "Сохранить"             );
        UIManager.put("FileChooser.openButtonText"      , "Открыть"               );
        UIManager.put("FileChooser.cancelButtonText"    , "Отмена"                );
        UIManager.put("FileChooser.fileNameLabelText"   , "Наименование файла"    );
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов"           );
        UIManager.put("FileChooser.lookInLabelText"     , "Директория"            );
        UIManager.put("FileChooser.saveInLabelText"     , "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText" , "Путь директории"       );
    }
}
