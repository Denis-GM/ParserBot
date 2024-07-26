package parser.app;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Application {
    private static JFrame frame = new JFrame("WebParser");

    private static JButton appStartBtn;
    private static JButton appStopBtn;
    private static JLabel errorLabel;

    private static JTextField delayField;
    private static JLabel delayCounter;
    private static Timer swingTimer;

    private static int countRow;

    private static JLabel labelFilePath;
    private static String fileName = "файл не выбран";
    private static String filePath = "";

    private static boolean programIsRunning = false;

    public Application(){
        addLocalizationForJFileChooser();
        createGUI();
    }

    public static void startTimer(){
        int delayRecaptchaSolution = WebBot.GetDelayRecaptchaSolution();
        swingTimer = new Timer(1000, new ActionListener() {
            int counter = delayRecaptchaSolution;
            public void actionPerformed(ActionEvent e) {
                delayCounter.setText(String.format("%s", counter));
                counter--;
                if(counter < 0) {
                    stopTimer();
                    delayCounter.setText(String.format("%s", WebBot.GetDelayRecaptchaSolution()));
                }
            }
        });
        swingTimer.start();
    }

    public static void stopTimer(){
        if(swingTimer != null){
            swingTimer.stop();
        }
    }

    public static void setStatus(String status){
        errorLabel.setText(status);
    }

    public static void updateProgressStatus(int curValue){
        errorLabel.setText(String.format("%s из %s", curValue, countRow));
    }

    private void createGUI() {
        frame.setResizable(false);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setLocationRelativeTo(null); // окно в центре экрана
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cst = new GridBagConstraints();
        cst.weighty = 1.0;

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        frame.setJMenuBar(menuBar);

        JLabel labelNameFile = new JLabel("Имя файла: ", JLabel.LEFT);
        labelNameFile.setPreferredSize(new Dimension(150, 30));
        cst.insets = new Insets(0, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 1;
        cst.gridy = 0;
        panel.add(labelNameFile,cst);

        labelFilePath = new JLabel(fileName, JLabel.CENTER);
        labelFilePath.setPreferredSize(new Dimension(150, 30));
        cst.insets = new Insets(0, 0, 0, 0);
        cst.gridwidth = 2;
        cst.gridx = 2;
        cst.gridy = 0;
        panel.add(labelFilePath,cst);

        JButton selectFile = new JButton("Выбрать файл");
        selectFile.setFocusPainted(false);
        selectFile.addActionListener(new SelectFileInDirectory());
        selectFile.setPreferredSize(new Dimension(150, 20));
        selectFile.setBackground(Color.WHITE);
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(-30, 0, 0, 0);
        cst.gridwidth = 2;
        cst.gridx = 2;
        cst.gridy = 1;
        panel.add(selectFile,cst);

        JLabel delayLabel = new JLabel("Задержка: ");
        delayLabel.setPreferredSize(new Dimension(150, 30));
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(0, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 1;
        cst.gridy = 2;
        panel.add(delayLabel,cst);

        delayField= new JTextField();
        delayField.setPreferredSize(new Dimension(150, 30));
        delayField.setText("120");
        cst.insets = new Insets(0, 0, 0, 0);
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.gridwidth = 2;
        cst.gridx = 2;
        cst.gridy = 2;
        panel.add(delayField,cst);

        delayCounter = new JLabel(String.format("%s", WebBot.GetDelayRecaptchaSolution()), JLabel.CENTER);
        delayCounter.setPreferredSize(new Dimension(150, 30));
        cst.insets = new Insets(0, 0, 0, 0);
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.gridwidth = 2;
        cst.gridx = 2;
        cst.gridy = 2;
        panel.add(delayCounter,cst);
        delayCounter.setVisible(programIsRunning);

        JLabel progress = new JLabel("Прогресс: ");
        progress.setPreferredSize(new Dimension(150, 30));
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(0, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 1;
        cst.gridy = 3;
        panel.add(progress,cst);

        errorLabel = new JLabel("", JLabel.CENTER);
        errorLabel.setPreferredSize(new Dimension(150, 30));
        cst.insets = new Insets(0, 0, 0, 0);
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.gridwidth = 2;
        cst.gridx = 2;
        cst.gridy = 3;
        panel.add(errorLabel,cst);

        appStartBtn = new JButton("Запустить");
        appStartBtn.setFocusPainted(false);
        appStartBtn.setPreferredSize(new Dimension(130, 30));
        appStartBtn.setForeground(Color.BLACK);
        appStartBtn.setBackground(Color.WHITE);
        appStartBtn.addActionListener(new StartAppForButton());
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(50, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 2;
        cst.gridy = 5;
        panel.add(appStartBtn,cst);

        appStopBtn = new JButton("Отмена");
        appStopBtn.setFocusPainted(false);
        appStopBtn.setPreferredSize(new Dimension(130, 30));
        appStopBtn.setForeground(Color.BLACK);
        appStopBtn.setBackground(Color.WHITE);
        appStopBtn.addActionListener(new StopAppForButton());
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(50, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 2;
        cst.gridy = 5;
        panel.add(appStopBtn,cst);
        appStopBtn.setVisible(programIsRunning);

        JButton close = new JButton("Выйти");
        close.setFocusPainted(false);
        close.setPreferredSize(new Dimension(130, 20));
        close.setForeground(Color.BLACK);
        close.setBackground(Color.WHITE);
        close.addActionListener(new CloseApp());
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(25, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 3;
        cst.gridy = 6;
        panel.add(close,cst);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private JMenu createFileMenu() {
        JMenu file = new JMenu("Файл");
        JMenuItem open = new JMenuItem("Открыть", new ImageIcon("images/open.png"));
        open.addActionListener(new SelectFileInDirectory());
        file.add(open);

        JMenuItem help = new JMenuItem("Помощь");
        file.add(help);

        JMenuItem exit = new JMenuItem(new ExitApp());
        exit.setIcon(new ImageIcon("images/exit.png"));
        file.add(exit);

        return file;
    }

    private static void checkStatusApplication(){
        appStartBtn.setVisible(!programIsRunning);
        appStopBtn.setVisible(programIsRunning);

        delayField.setVisible(!programIsRunning);
        delayCounter.setVisible(programIsRunning);
    }

    private void addLocalizationForJFileChooser() {
        UIManager.put("FileChooser.saveButtonText"      , "Сохранить"             );
        UIManager.put("FileChooser.openButtonText"      , "Открыть"               );
        UIManager.put("FileChooser.cancelButtonText"    , "Отмена"                );
        UIManager.put("FileChooser.fileNameLabelText"   , "Наименование файла"    );
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов"           );
        UIManager.put("FileChooser.lookInLabelText"     , "Директория"            );
        UIManager.put("FileChooser.saveInLabelText"     , "Сохранить в директории");
        UIManager.put("FileChooser.folderNameLabelText" , "Путь директории"       );
    }

    private static void StopApp(){
        WebBot.Stop();
        Application.stopTimer();
        programIsRunning = false;
        checkStatusApplication();

        errorLabel.setText("Принудительное завершение");
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

    private static class StartAppForButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Thread thread = new Thread() {
                public void run() {
                    WebBot.Start();
                    WebBot.SetDelayRecaptchaSolution(Integer.parseInt(delayField.getText()));
                    delayCounter.setText(String.format("%s", WebBot.GetDelayRecaptchaSolution()));
                    errorLabel.setText("В процессе...");
                    programIsRunning = true;
                    checkStatusApplication();
                    try {
                        Excel excel = new Excel(filePath);
                        countRow = excel.GetCountRow() + 1;
                        excel.readFromExcel();
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

    private static class StopAppForButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            StopApp();
        }
    }

    private class CloseApp implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to close the application?",
                    "Close Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                StopApp();
                System.exit(0);
            }
        }
    }

    private class ExitApp extends AbstractAction {
        private static final long serialVersionUID = 1L;

        ExitApp() {
            putValue(NAME, "Выход");
        }

        public void actionPerformed(ActionEvent e) {
            StopApp();
            System.exit(0);
        }
    }
}
