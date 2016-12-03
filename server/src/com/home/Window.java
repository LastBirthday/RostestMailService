package com.home;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by Dds on 25.02.2016.
 */
public class Window {

    private static JFrame frame;
    private static JLabel label;
    private static JPanel panel;
    private Window thisWindow;

    public Window () {
        thisWindow = this;
        frame = new JFrame("Сохранение данных");
        frame.setIconImage(new ImageIcon("database.png").getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

        panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(4,4,4,4));

        label = new JLabel();
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);

        frame.setPreferredSize(new Dimension(450, 250));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

    }

    public boolean criticalError(String message) {

        frame.setPreferredSize(new Dimension(450, 250));
        panel.setLayout(new GridLayout(2, 1));
        panel.add(label);

        label.setText(message);
        label.setForeground(Color.RED);

        JButton button = new JButton("Закрыть");
        panel.add(button);
        frame.pack();
        frame.getRootPane().setDefaultButton(button);
        //frame.setLocationRelativeTo(null);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                System.exit(1);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(1);
            }
        });

        frame.setVisible(true);
        return true;
    }

    public void doneSuccess(int successCount, int successPhotos, int count, boolean err) {

        panel.add(label);
        JLabel errorLabel = new JLabel();
        GridLayout layout;
        if (successCount == count) {
            if (!err) {
                frame.setPreferredSize(new Dimension(470, 320));
                layout = new GridLayout(2, 1);
                label.setText("<html><div style='text-align: center;'>База данных: " + Server.databaseName + "<br>" +
                        "Сканируемая папка: " + Server.searchFolderPath + "<br>" +
                        "Папка архива: " + Server.archiveFolderPath + "<br><br>" +
                        "Было обработано <font color='blue'>" + successCount + "</font> из " +
                        count + " писем, и все данные были полностью и успешно добалены в базу данных!<br>" +
                        "Записей добавлено: " + Server.countSUM + "<br>" +
                        "Фотографий и других файлов перенесено: " + successPhotos + "</html>");
            }
            else {
                frame.setPreferredSize(new Dimension(470, 440));
                layout = new GridLayout(3, 1);
                label.setText("<html><div style='text-align: center;'>База данных: " + Server.databaseName + "<br>" +
                        "Сканируемая папка: " + Server.searchFolderPath + "<br>" +
                        "Папка архива: " + Server.archiveFolderPath + "<br><br>" +
                        "Было обработано <font color='blue'>" + successCount + "</font> из " +
                        count + " писем. Но не все записи удалось добавить в базу данных вследствие " +
                        " <font color='red'>ошибок</font>:</html>");
                errorLabel.setText("<html><div style='text-align: left;'>Записей в несуществующую таблицу: " + Errors.noSuchTableError + "<br>" +
                        "Не совпадений структур таблиц со структурами данных в письме: " + Errors.differentColumnsCountError + "<br>" +
                        "Другие ошибки: " + Errors.anotherError + "</div><br>" +
                        "<div style='text-align: center;'>Более подробно смотрите в log файле.<br>" +
                        "Записей добавлено: " + Server.countSUM + "<br>" +
                        "Фотографий и других файлов перенесено: " + successPhotos + "</div></div></html>");
                panel.add(errorLabel);
            }
        } else if (successCount != 0) {
            if ((Errors.differentColumnsCountError + Errors.noSuchTableError + Errors.uniqueConstraintFailedError) != 0) {
                frame.setPreferredSize(new Dimension(470, 440));
                layout = new GridLayout(3, 1);
                label.setText("<html><div style='text-align: center;'>База данных: " + Server.databaseName + "<br>" +
                        "Сканируемая папка: " + Server.searchFolderPath + "<br>" +
                        "Папка архива: " + Server.archiveFolderPath + "<br><br>" +
                        "Было обработано <font color='red'>" + successCount + "</font> из "
                        + count + " писем. Возможные причины ошибки: архив поврежден, архив в неизвестном формате. " +
                        "Среди обработаных писем так же возникли <font color='red'>ошибки</font> и не все записи удалось добавить. Причины:<html>");
                errorLabel.setText("<html><div style='text-align: left;'>Записей в несуществующую таблицу: " + Errors.noSuchTableError + "<br>" +
                        "Не совпадений структур таблиц со структурами данных в письме: " + Errors.differentColumnsCountError + "<br>" +
                        "Другие ошибки: " + Errors.anotherError + "</div><br>" +
                        "<div style='text-align: center;'>Более подробно смотрите в log файле.<br>" +
                        "Записей добавлено: " + Server.countSUM + "<br>" +
                        "Фотографий и других файлов перенесено: " + successPhotos + "</div></div></html>");
                panel.add(errorLabel);
            } else {
                frame.setPreferredSize(new Dimension(470, 400));
                layout = new GridLayout(2, 1);
                label.setText("<html><div style='text-align: center;'>База данных: " + Server.databaseName + "<br>" +
                        "Сканируемая папка: " + Server.searchFolderPath + "<br>" +
                        "Папка архива: " + Server.archiveFolderPath + "<br><br>" +
                        "Было обработано <font color='red'>" + successCount + "</font> из "
                        + count + " писем. Возможные причины ошибки: архив поврежден, архив в неизвестном формате. " +
                        "Из обработынных писем, все данные были полностью и успешно добавлены в базу данных!<br>" +
                        "Более подробно смотрите в log файле.<br>" +
                        "Записей добавлено: " + Server.countSUM + "<br>" +
                        "Фотографий и других файлов перенесено: " + successPhotos + "</html>");
            }

        } else {
            frame.setPreferredSize(new Dimension(470, 320));
            layout = new GridLayout(2, 1);
            label.setText("<html><div style='text-align: center;'>База данных: " + Server.databaseName + "<br>" +
                    "Сканируемая папка: " + Server.searchFolderPath + "<br>" +
                    "Папка архива: " + Server.archiveFolderPath + "<br><br>" +
                    "Было обработано <font color='red'>" + successCount + "</font> из "
                    + count + " писем. Возможные причины ошибки: архив поврежден, архив сформирован не правильно. " +
                    "Более подробно причину смотрите в log файле.<br>" +
                    "Записей добавлено: " + Server.countSUM + "<br>" +
                    "Фотографий и других файлов перенесено: " + successPhotos + "</html>");
        }
        layout.setVgap(10);
        panel.setLayout(layout);

        JButton button = new JButton("Готово! (Enter, чтобы закрыть)");
        panel.add(button);
        frame.pack();
        frame.getRootPane().setDefaultButton(button);
        //frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });

    }

    public void processingFrame(int count) {

        frame.setPreferredSize(new Dimension(450, 250));
        panel.setLayout(new BorderLayout());
        panel.add(label);
        label.setText("<html><div style='text-align: center; vertical-align: top;'>Выполняются необходимые операции, пожалуйста, подождите. " +
                "При обработке большого количества писем и (или) большого количества данных в письмах, операция может занять значительное время.<br><br>" +
                "Идет обработка письма " + count + "...</div></html>");
        label.setForeground(Color.DARK_GRAY);
        frame.pack();
        //frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public boolean dataBaseNotFound() {

        frame.setPreferredSize(new Dimension(450, 250));
        panel.setLayout(new GridLayout(2, 1));
        panel.add(label);

        label.setText("<html><div style='text-align: center;'>База Данных не найдена. Пожалуйста, укажите местоположение Базы Данных вручную...</div></html>");
        label.setForeground(Color.RED);

        JButton button = new JButton("Выбрать");
        panel.add(button);
        frame.pack();
        //frame.setLocationRelativeTo(null);

        final WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(1);
            }
        };
        frame.addWindowListener(listener);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Server.databaseName = fileChooser(".db");

                if (Server.databaseName != null) {

                    try (BufferedReader reader = new BufferedReader(new FileReader(Server.settingsFilePath));
                         BufferedWriter writer = new BufferedWriter(new FileWriter("settings_tmp.ini"))) {

                        String s;
                        while ((s = reader.readLine()) != null) {
                            String cmd = s.substring(0, s.indexOf('=') + 1);
                            if (cmd.equals("dbname=")) {
                                writer.write(cmd + Server.databaseName);
                                writer.newLine();
                            } else {
                                writer.write(s);
                                writer.newLine();
                            }
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        File oldfile = new File("settings_tmp.ini");
                        File newfile = new File(Server.settingsFilePath);

                        if (newfile.delete()) System.out.println(newfile.getName() + " is deleted!");
                        else System.out.println("Delete operation is failed!");

                        if (oldfile.renameTo(newfile)) System.out.println("Rename successful");
                        else System.out.println("Rename failed");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    panel.removeAll();
                    frame.removeWindowListener(listener);
                    Server.startTasks(thisWindow);
                }

            }
        });

        frame.setVisible(true);
        return true;

    }

    public String fileChooser(final String extension) {

        UIManager.put("FileChooser.lookInLabelText", "Директория:");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.openButtonText", "Выбрать");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов:");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла:");
        UIManager.put("FileChooser.upFolderToolTipText", "Выше");
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        //UIManager.put("FileChooser.homeFolderToolTipText", "Рабочий стол");
        UIManager.put("FileChooser.newFolderToolTipText", "Новая папка");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Окно выбора Базы Данных");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(extension);
            }

            @Override
            public String getDescription() {
                switch (extension) {
                    case "dir" :
                        return "Только директории";
                    case ".ini" :
                        return "Settings files *.ini";
                    case ".dat" :
                        return "Data files *.dat";
                    case ".db" :
                        return "База Данных *.db";
                }
                return null;
            }
        });

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

}
