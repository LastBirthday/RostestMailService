package com.home;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.zip.GZIPInputStream;


/**
 * Created by Dds on 22.02.2016.
 * Version = 1.3
 * ---------------Change log 1.3------------------
 * - Added default buttons;
 * - Changed labels;
 * - Added absolute paths;
 * ---------------Change log 1.4------------------
 * - Unique constraints failed now is not valuable error;
 * - Output changed;
 */
public class Server {
    private final double version = 1.4;

    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet resultSet = null;

    public static String settingsFilePath;
    public static String databaseName;
    public static String archiveFolderPath;
    public static String searchFolderPath;
    public static String photosPath;
    public static String logFileName;
    public static File fileToOpen;

    private static ArrayList<File> filesToOpenNames = new ArrayList<>();
    private static ArrayList<File> otherFiles = new ArrayList<>();

    private static String err;
    private static int count;
    private static int countAll;
    public static int countSUM;
    private static int successPhotos;
    private static boolean errors;
    private static boolean partError;

    /*
        Взятие драйвера работы с базой данных Sqlite3 и попытка установки соединения.
     */
    private static void runJDBC (Window window) {

        try {

            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);

        } catch (ClassNotFoundException e) {
            errors = window.criticalError("Драйвер базы данных не найден. Обратитесь к разработчику.");
            System.out.println("Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            errors = window.criticalError("<html><div style='text-align: center;'>Соединение с базой данных установить не удалось." +
                    " Обратитесь к разработчику.</html>");
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

    }

    /*
        Закрытие всех ресурсов, во избежание утечки памяти.
     */
    private static void closeAll () {

        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*
        Парсинг входного письма и занесение информации в базу данных.
     */
    private static void parseAndUpdateQuery() {

        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(FilenameUtils.getBaseName(fileToOpen.getName()) + ".dat")))) {

            String s;
            String s1[];
            String dataTableName = "";
                while ((s = reader.readLine()) != null) {
                    try {
                        statement = connection.createStatement();
                        if (s.contains("TABLE=")) {
                                dataTableName = s.substring(6);
                                System.out.println(dataTableName + ":");
                        } else {
                            count++;
                            countAll++;
                            String sql = "INSERT INTO " + dataTableName + " VALUES (";
                            builder.append(sql);
                            s1 = s.split("\\|");
                            for (int i = 0; i < s1.length; i++) {
                                //System.out.println("TRACE: " + s1[i] + " i: " + i + " length: " + s1.length);
                                if (i == s1.length - 1) {
                                    if (s1[i].equals("NULL")) builder.append("null").append(");");
                                    else builder.append("'").append(s1[i]).append("'").append(");");
                                } else {
                                    if (s1[i].equals("NULL")) builder.append("null").append(", ");
                                    else builder.append("'").append(s1[i]).append("'").append(", ");
                                }
                            }
                            statement.executeUpdate(builder.toString());
                            System.out.println(builder.toString());
                            builder.setLength(0);
                        }
                        statement.executeUpdate(builder.toString());
                        System.out.println(builder.toString());

                    } catch (SQLException e) {
                        if (!e.toString().contains("UNIQUE constraint failed")) {
                            System.out.println(builder);
                            err += e + " В строке: " + builder.toString() + System.getProperty("line.separator");
                            count--;
                            builder.setLength(0);
                            e.printStackTrace();
                            //if (e.toString().contains("UNIQUE constraint failed")) Errors.uniqueConstraintFailedError++;
                            if (e.toString().contains("no such table")) Errors.noSuchTableError++;
                            else if (e.toString().contains("values were supplied")) Errors.differentColumnsCountError++;
                            else Errors.anotherError++;
                        } else {
                            System.out.println(builder);
                            count--;
                            builder.setLength(0);
                            e.printStackTrace();
                        }
                    }

                }

                //builder.append(";");
                //statement.executeUpdate(builder.toString());
                //System.out.println(builder.toString());

            /*} catch (SQLException e) {
                //partError = true;
                System.out.println(builder);
                err = "Ошибка при обработке SQL выражения. Возможно, данные записи уже имеются в таблице, либо структура таблицы базы данных " +
                        "не соответствует структуре данных письма. Ошибка: " + e + " В строке: " + builder.toString();
                e.printStackTrace();
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        Сканирование всех доступных писем для дальнейшей работы с ними.
     */
    private static void scanFiles(Window window) {

        File folder = new File(searchFolderPath);
        System.out.println(folder);
        File listOfFiles[] = folder.listFiles();

        if (listOfFiles == null) {
            errors = window.criticalError("<html><div style='text-align: center;'>Папки сканирования по указанному пути: " +
                    searchFolderPath + " обнаружено не было.</html>");
        } else {
            for (int i = 0; i < listOfFiles.length; i++) {
                System.out.println(listOfFiles[i].getName());
                if (listOfFiles[i].isFile() && FilenameUtils.getExtension(listOfFiles[i].getName()).equals("gzip")) {
                    filesToOpenNames.add(listOfFiles[i]);
                } else if (listOfFiles[i].isFile()) {
                    otherFiles.add(listOfFiles[i]);
                }
            }

            if ((filesToOpenNames.size() + otherFiles.size()) == 0) {
                errors = window.criticalError("<html><div style='text-align: center;'>В папке " + searchFolderPath +
                        " не было найдено ни одного файла. Пожалуйста, перенесите все архивы с письмами и фотографии в данную" +
                        " папку и перезапустите программу.</html>");
            }
        }

    }

    /*
        Считывание данных из файла настроек. Проверка их корректности и полноты.
     */
    private static boolean checkSettings (BufferedReader reader) {

        try {

            String s;
            if ((s = reader.readLine()) == null) return false;
            if (!s.equals("[Settings]")) return false;
            boolean one, two, three, four;
            one = two = three = four = false;
            while ((s = reader.readLine()) != null) {
                String cmd = s.substring(0, s.indexOf('=') + 1);
                switch (cmd) {
                    case "archiveFolder=":
                        archiveFolderPath = s.substring(cmd.length());
                        one = true;
                        break;
                    case "searchFolder=":
                        searchFolderPath = s.substring(cmd.length());
                        four = true;
                        break;
                    case "dbname=":
                        databaseName = s.substring(cmd.length());
                        two = true;
                        break;
                    case "log=":
                        logFileName = s.substring(cmd.length());
                        three = true;
                        break;
                }
            }
            if (!(one && two && three && four)) return false;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    /*
        Проверка на наличие настроек.
     */
    private static void getSettings(Window window) {

        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFilePath))) {

            if (!checkSettings(reader)) errors = window.criticalError("<html><div style='text-align: center;'>" +
            "settings.ini не является корректным файлом настроек. Восстановите корректный файл настроек. Образец содержимого:<br>" +
                    "<div style='text-align: left;'>[Settings]<br>" +
                    "dbname=main.db<br>" +
                    "archiveFolder=archive<br>" +
                    "log=log.log</html>");

        } catch (IOException e) {
            errors = window.criticalError("<html><div style='text-align: center;'>Файл настроек settings.ini в директории с программой не найден. " +
                    "Пожалуйста, перенесите его в директорию с программой.</html>");
            e.printStackTrace();
        }

    }

    /*
        Перенос входного письма в специальный архив.
     */
    private static void moveToArchive() {

        File archiveFolder = new File(archiveFolderPath);

        try {
            FileUtils.copyFileToDirectory(fileToOpen, archiveFolder);
        } catch (IOException e) {
            partError = true;
            err = "Не удалось переместить файл в архив. Возможно, у Вас нет доступа для записи в папку " +
                    archiveFolderPath + ". Ошибка: " + e + System.getProperty("line.separator");
            e.printStackTrace();
        }

    }

    /*
        Перенос фотографий в специальный архив.
     */
    private static void movePhotosToArchive(File file) {
        File archiveFolder = new File(archiveFolderPath);

        try {
            FileUtils.copyFileToDirectory(file, archiveFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Перенос фотографий в хранилище.
     */
    private static void movePhotosToBase(File file) {
        String s = new File(databaseName).getParentFile().getAbsolutePath() + System.getProperty("file.separator") + photosPath;
        System.out.println(s);
        File baseFolder = new File(s);

        try {
            FileUtils.copyFileToDirectory(file, baseFolder);
        } catch (IOException e) {
            successPhotos--;
            e.printStackTrace();
        }
    }

    /*
        Удаление временных файлов.
     */
    private static void deleteFiles() {

        File datLetter = new File(FilenameUtils.getBaseName(fileToOpen.getName()) + ".dat");

        if (fileToOpen.delete()) System.out.println("Файл " + fileToOpen.getName() + " был удалён.");
        if (datLetter.delete()) System.out.println("Файл " + datLetter + " был удалён.");

    }

    /*
        Очистка папки scan от фото.
     */
    private static void deletePhotos(File file) {
        if (file.delete()) System.out.println("Файл " + file.getName() + " был удалён.");
    }

    /*
        Создание лога программы с сообщением об успехе, либо ошибке.
     */
    private static void createLog(String status, String err) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new Date();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {

            writer.write("Дата: " + dateFormat.format(date) + " Наименование: '" + FilenameUtils.getBaseName(fileToOpen.getName()) +
                    "' Позиций добавлено: " + count + "\\" + countAll + " Статус: " + status);
            writer.newLine();
            writer.write(err);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        Создание лога по перемещённым фотографиям.
     */
    private static void photosLog(int all, int success) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new Date();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write("Дата: " + dateFormat.format(date) + " Фотографий перемещено: " + all + "\\" + success + System.getProperty("line.separator"));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Декомпрессия архива-письма.
     */
    private static void unzip() {

        try {

            GZIPInputStream in = new GZIPInputStream(new FileInputStream(fileToOpen));

            OutputStream out = new FileOutputStream(FilenameUtils.getBaseName(fileToOpen.getName()) + ".dat");

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (IOException e) {
            partError = true;
            err = "Ошибка при разархивировании файла. Архив в неизвестном формате, либо поврежден." + System.getProperty("line.separator");
            e.printStackTrace();
        }

    }

    /*
        Уменьшение log файла в 2 раза, если он достиг 10МБ
     */
    private static void checkLog() {

        int maxSize = 10000000;

        File log = new File("log.log");

        if (log.length() > maxSize) {
            System.out.println(log.length());
            try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(log));
                 BufferedReader reader = new BufferedReader(new FileReader(log));
                 BufferedWriter writer = new BufferedWriter(new FileWriter("tmp.log"))) {

                lineNumberReader.skip(Long.MAX_VALUE);

                int position = lineNumberReader.getLineNumber() / 2;
                System.out.println(lineNumberReader.getLineNumber() + 1);

                String s;
                for (int i = 0; i < position; i++) {
                    reader.readLine();
                }
                while ((s = reader.readLine()) != null) {
                    writer.write(s);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                File oldfile = new File("tmp.log");
                File newfile = new File("log.log");

                if (newfile.delete()) System.out.println(newfile.getName() + " is deleted!");
                else System.out.println("Delete operation is failed!");

                if (oldfile.renameTo(newfile)) System.out.println("Rename successful");
                else System.out.println("Rename failed");
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

    }

    private static boolean checkDataBase() {
        File database = new File(databaseName);
        return database.exists();
    }

    /*
        Входная точка программы.
     */
    public static void main(String[] args) {

        errors = false;

        settingsFilePath = "settings.ini";
        photosPath = "photos";

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final Window window = new Window();

                getSettings(window);
                if (!errors) {
                    if (!checkDataBase()) window.dataBaseNotFound();
                    else startTasks(window);
                }
            }
        });
    }

    static int number;
    public static void startTasks(final Window window) {
        //window.processingFrame(1);
        number = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean wasError = false;
                if (!errors) runJDBC(window);
                if (!errors) scanFiles(window);

                int successCount = 0;
                countSUM = 0;

                for (File file : filesToOpenNames) {
                    number++;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            window.processingFrame(number);
                        }
                    }).start();
                    System.out.println("NEW FILE IS OPEN: " + file.getName());
                    partError = false;
                    err = "";
                    fileToOpen = file;
                    count = countAll = 0;

                    unzip();
                    if (!partError) {
                        parseAndUpdateQuery();
                        countSUM += count;
                    }
                    if (!partError) moveToArchive();
                    if (!partError) deleteFiles();
                    if (!partError) {
                        createLog("ОБРАБОТАНО И ПЕРЕМЕЩЕНО В АРХИВ", err);
                        if (!err.equals("")) wasError = true;
                        successCount++;
                    } else {
                        createLog("КРИТИЧЕСКАЯ ОШИБКА", err);
                        wasError = true;
                    }

                }
                successPhotos = 0;
                for (File file : otherFiles) {
                    movePhotosToBase(file);
                    movePhotosToArchive(file);
                    deletePhotos(file);
                    successPhotos++;
                }
                if (otherFiles.size() != 0) photosLog(otherFiles.size(), successPhotos);

                if (!errors) window.doneSuccess(successCount, successPhotos, filesToOpenNames.size(), wasError);

                checkLog();
                closeAll();
            }
        }).start();

    }

}
