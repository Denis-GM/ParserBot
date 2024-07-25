package parser.app;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Application {
    private JFrame frame = new JFrame("WebParser");

    private JButton appStartBtn;
    private JButton appStopBtn;
    private JLabel labelFilePath;
    private static JLabel errorLabel;
    private static int countRow;

    private String fileName = "файл не выбран";
    private String filePath = "";

    private  boolean programIsRunning = false;

    public Application(){
        addLocalizationForJFileChooser();
        createGUI();
    }

    private void createGUI() {
//        frame.setSize(500, 250);
        frame.setResizable(false);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setLocationRelativeTo(null); // окно в центре экрана
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cst = new GridBagConstraints();
//        cst.weightx = 1.0;
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

        JLabel progress = new JLabel("Прогресс: ");
        progress.setPreferredSize(new Dimension(150, 30));
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(-20, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 1;
        cst.gridy = 2;
        panel.add(progress,cst);

        errorLabel = new JLabel("", JLabel.CENTER);
        errorLabel.setPreferredSize(new Dimension(150, 30));
        cst.insets = new Insets(-20, 0, 0, 0);
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.gridwidth = 2;
        cst.gridx = 2;
        cst.gridy = 2;
        panel.add(errorLabel,cst);

        appStartBtn = new JButton("Запустить");
        appStartBtn.setFocusPainted(false);
        appStartBtn.setPreferredSize(new Dimension(130, 30));
        appStartBtn.setForeground(Color.BLACK);
        appStartBtn.setBackground(Color.WHITE);
        appStartBtn.addActionListener(new Start());
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(50, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 2;
        cst.gridy = 4;
        panel.add(appStartBtn,cst);

        appStopBtn = new JButton("Отмена");
        appStopBtn.setFocusPainted(false);
        appStopBtn.setPreferredSize(new Dimension(130, 30));
        appStopBtn.setForeground(Color.BLACK);
        appStopBtn.setBackground(Color.WHITE);
        appStopBtn.addActionListener(new Stop());
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(50, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 2;
        cst.gridy = 4;
        panel.add(appStopBtn,cst);
        appStopBtn.setVisible(programIsRunning);

        JButton close = new JButton("Выйти");
        close.setFocusPainted(false);
        close.setPreferredSize(new Dimension(130, 20));
        close.setForeground(Color.BLACK);
        close.setBackground(Color.WHITE);
        close.addActionListener(new Close());
        cst.fill = GridBagConstraints.HORIZONTAL;
        cst.insets = new Insets(25, 0, 0, 0);
        cst.gridwidth = 1;
        cst.gridx = 3;
        cst.gridy = 5;
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

        JMenuItem exit = new JMenuItem(new ExitAction());
        exit.setIcon(new ImageIcon("images/exit.png"));
        file.add(exit);

        return file;
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("Вид");

        JCheckBoxMenuItem line  = new JCheckBoxMenuItem("Фиксированный размер окна");
        line.setState(true);
        viewMenu.add(line);

        JCheckBoxMenuItem grid  = new JCheckBoxMenuItem("Сетка");
        viewMenu.add(grid);

        return viewMenu;
    }

    private void checkStatusApplication(){
        appStartBtn.setVisible(!programIsRunning);
        appStopBtn.setVisible(programIsRunning);
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
                    programIsRunning = true;
                    checkStatusApplication();
                    try {
                        Excel excel = new Excel(filePath);
                        countRow = excel.GetCountRow() + 1;
                        excel.readFromExcel();
                    }
                    catch(Exception exception) {
                        try {

                        }
                        catch (Exception e){

                        }
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
            WebBot.Stop();

            programIsRunning = false;
            checkStatusApplication();

            errorLabel.setText("Принудительное завершение");
        }
    }

    private class Close implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to close the application?",
                    "Close Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    private class ExitAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        ExitAction() {
            putValue(NAME, "Выход");
        }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static void UpdateProgress(int curValue){
        errorLabel.setText(String.format("%s из %s", curValue, countRow));
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
}
