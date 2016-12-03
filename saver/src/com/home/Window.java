package com.home;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/**
 * Created by Dds on 25.04.2016.
 */

public class Window {

    private static JFrame frame;
    private static JLabel label;
    private static JPanel panel;
    private Window thisWindow;

    public Window () {

        thisWindow = this;
        frame = new JFrame("Сохранение в директорию");
        frame.setIconImage(new ImageIcon("Travler16.png").getImage());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

        panel = new JPanel();
        frame.add(panel);
        panel.setLayout(new BorderLayout());

        label = new JLabel();
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);

        frame.setResizable(false);

    }

    public boolean criticalError(String message) {

        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(new EmptyBorder(4,4,4,4));
        panel.add(label);

        label.setText(message);
        label.setForeground(Color.RED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton button = new JButton("Закрыть");
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Saver.clearAll();
                createErrorFile();
                System.exit(1);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                Saver.clearAll();
                createErrorFile();
                System.exit(1);
            }
        });

        return true;
    }

    private WindowListener listener;

    public void processingFrame() {

        panel.removeAll();
        frame.setPreferredSize(new Dimension(450, 250));
        panel.setLayout(new BorderLayout());
        panel.add(label);
        label.setText("<html><div style='text-align: center; vertical-align: top;'>Выполняются необходимые операции, пожалуйста, подождите. " +
                "При сохранении множества фотографий и (или) слабой сиситеме, операция может занять некоторое время.</div></html>");
        label.setForeground(Color.DARK_GRAY);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        frame.pack();
        listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                Saver.clearAll();
                createErrorFile();
                System.exit(1);
            }
        };
        frame.addWindowListener(listener);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void doneSuccess() {

        frame.removeWindowListener(listener);
        panel.add(label);
        label.setText("Файлы были успешно сохранены!");
        label.setForeground(Color.BLUE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(new EmptyBorder(4,4,4,4));

        JButton button = new JButton("Готово! (Enter, чтобы закрыть)");
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });

    }

    private String getDirectory() {
        File settings = new File(Saver.settingsFilePath);
        if (settings.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(settings))) {
                if (Saver.checkSettings(reader)) return Saver.folderToSaveTo;
                else return "Укажите директорию";
            } catch (IOException e) {
                e.printStackTrace();
                return "Укажите директорию";
            }
        }
        else return "Укажите директорию";
    }

    public boolean tuneProgramm(String message, boolean input) {

        panel.removeAll();

        JLabel errorLabel = new JLabel(message);
        final JLabel dialogLabel = new JLabel("Укажите директорию, куда будут сохранены файлы...");
        final JLabel addressLabel = new JLabel("<html><div style='text-align: center;'>Директория:</html>");
        dialogLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        addressLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        final JButton chooseDirectory = new JButton("Выбрать");
        final JButton saveAndProceedButton = new JButton("Сохранить и записать");
        final JButton saveAndExitButton = new JButton("Сохранить и выйти");
        final JButton cancelButton = new JButton("Отмена");
        chooseDirectory.setFont(new Font("SansSerif", Font.PLAIN, 12));
        saveAndProceedButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        saveAndExitButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 12));

        chooseDirectory.registerKeyboardAction(chooseDirectory.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
        chooseDirectory.registerKeyboardAction(chooseDirectory.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);
        saveAndProceedButton.registerKeyboardAction(saveAndProceedButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
        saveAndProceedButton.registerKeyboardAction(saveAndProceedButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);
        saveAndExitButton.registerKeyboardAction(saveAndExitButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
        saveAndExitButton.registerKeyboardAction(saveAndExitButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);
        cancelButton.registerKeyboardAction(cancelButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);
        cancelButton.registerKeyboardAction(cancelButton.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);

        if (!input) saveAndProceedButton.setEnabled(false);

        final JTextField directoryTextField = new JTextField(getDirectory());
        directoryTextField.setHorizontalAlignment(SwingConstants.CENTER);
        directoryTextField.setFocusable(false);

        GridBagLayout gbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        panel.setLayout(gbag);
        frame.setPreferredSize(new Dimension(600, 270));

        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbag.setConstraints(errorLabel, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbag.setConstraints(dialogLabel, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.ipadx = 50;
        gbc.ipady = 10;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbag.setConstraints(addressLabel, gbc);
        gbc.ipadx = 200;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbag.setConstraints(chooseDirectory, gbc);

        gbc.ipadx = 600;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbag.setConstraints(directoryTextField, gbc);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.ipadx = 140;
        gbc.ipady = 10;
        gbc.weighty = 1.0;
        gbc.gridwidth = GridBagConstraints.FIRST_LINE_START;
        gbag.setConstraints(saveAndProceedButton, gbc);
        gbc.ipadx = 140;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbag.setConstraints(saveAndExitButton, gbc);
        gbc.ipadx = 200;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbag.setConstraints(cancelButton, gbc);

        panel.add(errorLabel);
        panel.add(dialogLabel);
        panel.add(addressLabel); panel.add(chooseDirectory);
        panel.add(directoryTextField);
        panel.add(saveAndProceedButton); panel.add(saveAndExitButton); panel.add(cancelButton);

        frame.pack();
        frame.setLocationRelativeTo(null);

        final WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (directoryTextField.getText().equals("") || directoryTextField.getText().equals("Укажите директорию")) {
                    directoryTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
                    directoryTextField.setForeground(Color.RED);
                    dialogLabel.setText("Пожалуйста, сначала укажите директорию, куда будет произведено сохранение файлов!");
                    dialogLabel.setForeground(Color.RED);
                } else {
                    recoverSettings();
                    frame.dispose();
                    Saver.clearAll();
                    createErrorFile();
                    System.exit(0);
                }
            }
        };
        frame.addWindowListener(listener);

        directoryTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (directoryTextField.getText().equals("Укажите директорию")) {
                    directoryTextField.setText("");
                }
                directoryTextField.setForeground(Color.DARK_GRAY);
                directoryTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (directoryTextField.getText().equals("")) {
                    directoryTextField.setText("Укажите директорию");
                }
            }
        });

        chooseDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s;
                if ((s = fileChooser("dir")) != null) {
                    directoryTextField.setText(s);
                    Saver.folderToSaveTo = s;
                    directoryTextField.setForeground(Color.DARK_GRAY);
                    directoryTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    dialogLabel.setText("Директория была успешно выбрана!");
                    dialogLabel.setForeground(Color.BLUE);
                }
            }
        });

        saveAndExitButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                saveAndExitButton.setToolTipText("Убедитесь, что Вы указали директорию для сохранения");
            }
        });

        saveAndExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (directoryTextField.getText().equals("") || directoryTextField.getText().equals("Укажите директорию")) {
                    directoryTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
                    directoryTextField.setForeground(Color.RED);
                    dialogLabel.setText("Пожалуйста, сначала укажите директорию, куда будет произведено сохранение файлов!");
                    dialogLabel.setForeground(Color.RED);
                } else {
                    recoverSettings();
                    frame.dispose();
                    Saver.clearAll();
                    createErrorFile();
                    System.exit(0);
                }
            }
        });

        saveAndProceedButton.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                saveAndProceedButton.setToolTipText("Убедитесь, что Вы указали директорию для сохранения");
            }
        });

        saveAndProceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (directoryTextField.getText().equals("") || directoryTextField.getText().equals("Укажите директорию")) {
                    directoryTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
                    directoryTextField.setForeground(Color.RED);
                    dialogLabel.setText("Пожалуйста, сначала укажите директорию, куда будет произведено сохранение файлов!");
                    dialogLabel.setForeground(Color.RED);
                } else {
                    recoverSettings();
                    panel.removeAll();
                    frame.removeWindowListener(listener);
                    Saver.startTasks(thisWindow);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Saver.clearAll();
                createErrorFile();
                System.exit(1);
            }
        });

        frame.setVisible(true);
        return true;

    }

    /*
    public boolean inputNotFound(String message) {

        panel.add(label);
        label.setText(message);
        label.setForeground(Color.RED);
        panel.setLayout(new GridLayout(2, 1));

        JButton button = new JButton("Выбрать");
        panel.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);

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
                Client.fileToOpenName = fileChooser(".dat");
                if (Client.fileToOpenName != null) {

                    try (BufferedReader reader = new BufferedReader(new FileReader(Client.settingsFilePath));
                            BufferedWriter writer = new BufferedWriter(new FileWriter("settings_tmp.ini"))) {

                        String s;
                        while ((s = reader.readLine()) != null) {
                            String cmd = s.substring(0, s.indexOf('=') + 1);
                            if (cmd.equals("input=")) {
                                writer.write(cmd + Client.fileToOpenName);
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
                        File newfile = new File(Client.settingsFilePath);

                        if (newfile.delete()) System.out.println(newfile.getName() + " is deleted!");
                        else System.out.println("Delete operation is failed!");

                        if (oldfile.renameTo(newfile)) System.out.println("Rename successful");
                        else System.out.println("Rename failed");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    panel.removeAll();
                    frame.removeWindowListener(listener);
                    Client.main(null);
                }

            }
        });

        return true;

    }
    */

    /*
    public boolean settingsNotFound(String message, Color color) {

        panel.remove(label);
        panel.setLayout(new GridLayout(3, 2));
        frame.setPreferredSize(new Dimension(600, 400));

        JLabel label1 = new JLabel(message);
        label1.setForeground(color);
        JLabel label2 = new JLabel("<html><div style='text-align: center;'>Настроить все элементы вручную:</div></html>");
        JLabel label3 = new JLabel("<html><div style='text-align: left;'>Восстановить заводские настройки:<br>" +
                "<font color='blue'>-входной файл:</font> " + Client.fileToOpenName + " в директории с программой;<br>" +
                "<font color='blue'>-база данных:</font> " + Client.databaseName + " в директории с программой;<br>" +
                "<font color='blue'>-email для отправки:</font> " + Client.adressToSendFrom + "<br>" +
                "<font color='blue'>-email для получения:</font> " + Client.folderToSaveTo + "</html>");
        JButton button1 = new JButton("Выбрать файл настроек");
        JButton button2 = new JButton("Настроить");
        JButton button3 = new JButton("Восстановить");

        panel.add(label1);
        panel.add(button1);
        panel.add(label2);
        panel.add(button2);
        panel.add(label3);
        panel.add(button3);

        frame.pack();
        frame.setLocationRelativeTo(null);

        final WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(1);
            }
        };
        frame.addWindowListener(listener);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client.settingsFilePath = fileChooser(".ini");
                boolean good = false;
                if (Client.settingsFilePath != null) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(Client.settingsFilePath))) {

                        if (reader.readLine().equals("[Settings]")) {
                            good = true;
                            Path from = Paths.get(Client.settingsFilePath);
                            Path to = Paths.get("settings_tmp.ini");
                            try {
                                Files.copy(from, to);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (good) {
                        try {
                            File oldfile = new File("settings_tmp.ini");
                            File newfile = new File("settings.ini");

                            if (newfile.exists()) {
                                System.out.println("Existing is bad. Time to delete!");
                                if (newfile.delete()) System.out.println(newfile.getName() + " is deleted!");
                                else System.out.println("Delete operation is failed!");
                            }

                            if (oldfile.renameTo(newfile)) System.out.println("Rename successful");
                            else System.out.println("Rename failed");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    panel.removeAll();
                    frame.removeWindowListener(listener);
                    frame.setPreferredSize(new Dimension(450, 250));
                    Client.main(null);
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                panel.removeAll();
                frame.removeWindowListener(listener);
                frame.setPreferredSize(new Dimension(450, 250));
                setNewSettings();

            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                panel.removeAll();
                frame.removeWindowListener(listener);
                frame.setPreferredSize(new Dimension(450, 250));
                recoverSettings();

            }
        });

        return true;

    }
    */

    private void recoverSettings() {

        final File settings = new File(Saver.settingsFilePath);

        if (settings.exists()) {
            if(settings.delete()) System.out.println("Файл " + settings + " был удален.");
            else System.out.println("Файл удалить не удалось.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settings))) {

            writer.write("[Settings]" + System.getProperty("line.separator") +
                    //"dbname=" + Client.databaseName + System.getProperty("line.separator") +
                    "folder=" + Saver.folderToSaveTo + System.getProperty("line.separator"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    private boolean canFirst;
    private boolean canSecond;
    private String aPart;

    private void setNewSettings() {

        canFirst = true;
        canSecond = false;

        panel.setLayout(new GridLayout(2, 1));

        final JButton button1 = new JButton("Шаг 1. Назначить входной файл.");
        JButton button2 = new JButton("Шаг 2. Назначить базу данных.");

        panel.add(button1);
        panel.add(button2);
        frame.pack();
        frame.repaint();
        frame.setLocationRelativeTo(null);

        final WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(1);
            }
        };
        frame.addWindowListener(listener);

        final File settings = new File("settings.ini");

        if (settings.exists()) {
            if(settings.delete()) System.out.println("Файл " + settings + " был удален.");
            else System.out.println("Файл удалить не удалось.");
        }

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canFirst) {
                    Client.fileToOpenName = fileChooser(".dat");

                    if (Client.fileToOpenName != null) {

                        aPart = "[Settings]" + System.getProperty("line.separator") +
                                "input=" + Client.fileToOpenName;

                        canFirst = false;
                        canSecond = true;
                        button1.setForeground(Color.BLUE);
                        button1.setText("Сделано!");

                    }
                }

            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (canSecond) {
                    Client.databaseName = fileChooser(".db");

                    if (Client.databaseName != null) {

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settings))) {

                            writer.write(aPart);
                            writer.newLine();
                            writer.write("dbname=" + Client.databaseName);
                            writer.newLine();
                            writer.write("emailFrom=" + Client.adressToSendFrom);
                            writer.newLine();
                            writer.write("emailTo=" + Client.folderToSaveTo);

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        panel.removeAll();
                        frame.removeWindowListener(listener);
                        Client.main(null);
                    }
                }

            }
        });

    }
    */

    /*public boolean dataBaseNotFound(String message) {

        panel.setLayout(new GridLayout(2, 1));
        panel.add(label);

        label.setText(message);
        label.setForeground(Color.RED);

        JButton button = new JButton("Выбрать");
        panel.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);

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

                Client.databaseName = fileChooser(".db");

                if (Client.databaseName != null) {

                    try (BufferedReader reader = new BufferedReader(new FileReader(Client.settingsFilePath));
                         BufferedWriter writer = new BufferedWriter(new FileWriter("settings_tmp.ini"))) {

                        String s;
                        while ((s = reader.readLine()) != null) {
                            String cmd = s.substring(0, s.indexOf('=') + 1);
                            if (cmd.equals("dbname=")) {
                                writer.write(cmd + Client.databaseName);
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
                        File newfile = new File(Client.settingsFilePath);

                        if (newfile.delete()) System.out.println(newfile.getName() + " is deleted!");
                        else System.out.println("Delete operation is failed!");

                        if (oldfile.renameTo(newfile)) System.out.println("Rename successful");
                        else System.out.println("Rename failed");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    panel.removeAll();
                    frame.removeWindowListener(listener);
                    Client.main(null);
                }

            }
        });

        return true;

    }*/

    /*
    public boolean sqlError(String message) {

        panel.setLayout(new GridLayout(2, 1));
        panel.add(label);

        label.setText(message);
        label.setForeground(Color.RED);

        JButton button = new JButton("Перейти к выбору.");
        panel.add(button);
        frame.pack();
        frame.setLocationRelativeTo(null);

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

                panel.removeAll();
                frame.removeWindowListener(listener);
                setNewSettings();

            }
        });

        return true;

    }
    */

    public String fileChooser(final String extension) {

        UIManager.put("FileChooser.lookInLabelText", "Директория:");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.openButtonText", "Выбрать");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов:");
        UIManager.put("FileChooser.folderNameLabelText", "Путь к директории:");
        UIManager.put("FileChooser.upFolderToolTipText", "Выше");
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        //UIManager.put("FileChooser.homeFolderToolTipText", "Рабочий стол");
        UIManager.put("FileChooser.newFolderToolTipText", "Новая папка");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Директория для сохранения");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
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

    private void createErrorFile() {
        File errFile = new File("error.err");
        try {
            if (errFile.createNewFile()) {
                System.out.println("Файл ошибки создан.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

