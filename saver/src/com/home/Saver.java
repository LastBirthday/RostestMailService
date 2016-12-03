package com.home;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Dds on 25.04.2016.
 * version = 1.13
 * ---------------Change log 1.10------------------
 * - Copied from Client project
 * - Deleted email system adn created file saving system;
 * - Remodeled interface and functionality;
 * ---------------Change log 1.11------------------
 * - Added old directory path on path field;
 * - Changed name;
 * ---------------Change log 1.12------------------
 * - Changed interface a bit;
 * ---------------Change log 1.13------------------
 * - Buttons react on hit enter now;
 * - Post.dat deleting in any scenarios;
 * ---------------Change log 1.14------------------
 * - error.err file added;
 * - FileNotFoundException message displaying and catching properly;
 * ---------------Change log 1.15------------------
 * - Writing method changed;
 * - Error file now creates in any scenarios where letter was not sent
 */

public class Saver {
    private final double version = 1.15;

    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public static String settingsFilePath;
    public static String fileToOpenName;
    public static String databaseName;
    public static String FIOFilePath;
    private static String fileToWriteName;
    public static String adressToSendFrom;
    public static String folderToSaveTo;

    private static ArrayList<String> nameTables = new ArrayList<>();
    private static List<List<String>> values = new ArrayList<>();
    private static List<String> photos = new ArrayList<>();

    private static boolean errors;

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
        Создание письма. Пересылка запросов в базу данных, извлечение информации через SELECT-ы, и запись информации в письмо.
     */
    private static void createQueryAndWriteIntoFile(Window window, ArrayList<String> nameTables, List<List<String>> values) {

        File letter = new File(fileToWriteName + ".dat");

        if (letter.exists()) {
            if (letter.delete()) System.out.println("Старое письмо было удалено.");
            else System.out.println("Старое письмо не получилось удалить");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(letter))) {

            for (int i = 0; i < nameTables.size(); i++) {

                if (errors) break;

                String sql = "SELECT * FROM " + nameTables.get(i) + " WHERE id = ?";

                writer.write("TABLE=" + nameTables.get(i));
                writer.newLine();
                System.out.println(nameTables.get(i) + ":");

                try {
                    preparedStatement = connection.prepareStatement(sql);
                    for (int j = 0; j < values.get(i).size(); j++) {
                        preparedStatement.setString(1, values.get(i).get(j));
                        resultSet = preparedStatement.executeQuery();
                        ResultSetMetaData rsmd = resultSet.getMetaData();

                        while (resultSet.next()) {
                            for (int k = 0; k < rsmd.getColumnCount(); k++) {
                                if (k + 1 == rsmd.getColumnCount()) {
                                    if (resultSet.getString(k + 1) == null || resultSet.getString(k + 1).equals("")) {
                                        writer.write("NULL");
                                        System.out.print("NULL");
                                    } else {
                                        writer.write(resultSet.getString(k + 1));
                                        System.out.print(resultSet.getString(k + 1));
                                    }
                                    writer.newLine();
                                } else {
                                    if (resultSet.getString(k + 1) == null || resultSet.getString(k + 1).equals("")) {
                                        writer.write("NULL" + "|");
                                        System.out.print("NULL" + " ");
                                    } else {
                                        writer.write(resultSet.getString(k + 1) + "|");
                                        System.out.print(resultSet.getString(k + 1) + " ");
                                    }
                                }
                            }
                            System.out.println();
                        }
                    }

                } catch (SQLException e) {
                    errors = window.criticalError("<html><div style='text-align: center;'>Произошла ошибка при обработке запроса в Базе Данных. " +
                            "Возможно, не удалось найти одну из таблиц, либо файл базы данных поврежден. " +
                            "Сформируйте другой запрос, либо выберите другую базу данных." +
                            " Ошибка: " + e + "</html>");
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        Закрытие всех ресурсов, дабы не было утечки памяти.
     */
    private static void closeAll () {

        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*
        Метод извлечения данных из настроек. Одновременно проверка на корректность файла настроек.
     */
    public static boolean checkSettings (BufferedReader reader) {

        try {

            String s;
            if ((s = reader.readLine()) == null) return false;
            if (!s.equals("[Settings]")) return false;
            boolean one, two;
            two = false;
            while ((s = reader.readLine()) != null) {
                String cmd = s.substring(0, s.indexOf('=') + 1);
                switch (cmd) {
                    /*case "dbname=":
                        databaseName = s.substring(cmd.length());
                        one = true;
                        break;*/
                    case "folder=":
                        folderToSaveTo = s.substring(cmd.length());
                        two = true;
                        break;
                }
            }
            if (!two) return false;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }

    /*
        Проверка на наличие настроек и запуск метода извлечения данных из настроек.
     */
    private static void getSettings (Window window) {

        try (BufferedReader reader = new BufferedReader(new FileReader(settingsFilePath))) {

            if (!checkSettings(reader)) errors = window.tuneProgramm("Необходимо настроить программу.", true);

        } catch (IOException e) {
            errors = window.tuneProgramm("Необходимо настроить программу.", true);
            e.printStackTrace();
        }

    }

    /*
        Проверка на существование базы данных с указанным в настройках именем.
     */
    private static void checkDataBase(Window window) {

        File database = new File(databaseName);

        if (!database.exists()) errors = window.criticalError("<html><div style='text-align: center;'>Программе не удалось " +
                "обнаружить Вашу базу данных. Попробуйте выбрать Базу Данных заново. При повторном возникновении проблемы, пожалуйста, " +
                "обратитесь к разработчикам.</html>");

    }

    /*
        Парсинг входного файла и занесение информации в массивы данных.
     */
    private static void parse(Window window) {

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileToOpenName)))) {
            boolean flag = true;
            String s;
            String s1[];
            while ((s = reader.readLine()) != null) {
                if (flag) {
                    fileToWriteName = s;
                    s = reader.readLine();
                    databaseName = s;
                    flag = false;
                } else {

                    if (s.charAt(0) == '&') {
                        System.out.println(s.substring(1));
                        photos.add(s.substring(1));
                    }
                    else {

                        s1 = s.split(":");
                        for (int i = 0; i < nameTables.size() + 1; i++) {
                            if (i == nameTables.size()) {
                                nameTables.add(s1[0]);
                                values.add(new ArrayList<String>());
                                values.get(i).add(s1[1]);
                                break;
                            } else if (nameTables.get(i).equals(s1[0])) {
                                values.get(i).add(s1[1]);
                                break;
                            }
                        }
                    }
                }
            }

        } catch (IOException e1) {
            errors = window.criticalError("<html><div style='text-align: center;'>По пока непонятным причинам, программе не удалось обработать информацию. " +
                    "Входной файл не найден, либо не корректен. Попробуйте перезапустить программу, либо обратитесь к разработчикам.</html>");
            e1.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e1) {
            errors = window.criticalError("<html><div style='text-align: center;'>Входной файл " + new File(fileToOpenName).getName() +
                    " не является корректным. Попробуйте перезапустить программу, либо обратитесь к разработчикам.</html>");
            e1.printStackTrace();
        }

    }

    /*
        Компрессия письма методом GZIP.
     */
    private static void zip(Window window) {

        try {

            GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(fileToWriteName + ".gzip"));


            FileInputStream in = new FileInputStream(fileToWriteName + ".dat");

            byte[] buf  = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0 , len);
            }

            in.close();
            out.finish();
            out.close();

        } catch (IOException e) {
            errors = window.criticalError("<html><div style='text-align: center;'>Не удалось произвести компрессию файла. " +
                    "Ошибка: " + e + "</html>");
            e.printStackTrace();
        }

        //ile archive = new File(fileToWriteName + ".gzip");

        /*if (archive.length() > 26214000) errors = window.criticalError("<html><div style='text-align: center;'>Размер Вашего файла " +
                "превышает 25 МБ, что является предельно допустимым размером файла при посылке на почту. Пожалуйста, разбейте Ваш " +
                "запрос на несколько частей и отправьте частями.</html>");*/

    }

    /*
        Сохранение файлов в облачную директорию.
     */
    private static void saveToDirectory(Window window) {
        File oldFile = new File(fileToWriteName + ".gzip");
        File newFile = new File(folderToSaveTo + System.getProperty("file.separator") + fileToWriteName + ".gzip");
        try (FileInputStream in = new FileInputStream(oldFile);
             FileOutputStream out = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            errors = window.criticalError("<html><div style='text-align: center;'>Не удалось записать файлы в директорию, возможно у Вас нет прав доступа " +
                    "для записи в данную директорию, либо её не существует. Попробуйте настроить директорию заново и повторить попытку записи. Если " +
                    "проблема не исчезает, обратитесь к разработчикам.</div></html>");
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < photos.size(); i++) {
            oldFile = new File(photos.get(i));
            newFile = new File(folderToSaveTo + System.getProperty("file.separator") + oldFile.getName());
            try (FileInputStream in = new FileInputStream(oldFile);
                 FileOutputStream out = new FileOutputStream(newFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                if (e.toString().contains("FileNotFoundException")) {
                    errors = window.criticalError("<html><div style='text-align: center;'>Не удалось найти файл по указанному пути. " +
                            "Ошибка:<br>" + e + "</div></html>");
                    e.printStackTrace();
                    return;
                } else {
                    errors = window.criticalError("<html><div style='text-align: center;'>Не удалось записать файлы в директорию, возможно у Вас нет прав доступа " +
                            "для записи в данную директорию, либо её не существует. Попробуйте настроить директорию заново и повторить попытку записи. Если " +
                            "проблема не исчезает, обратитесь к разработчикам.</div></html>");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /*
        Очистка временных файлов и входного файла.
     */
    public static void clearAll() {

        File letter = new File(fileToWriteName + ".dat");
        File gzipLetter = new File(fileToWriteName + ".gzip");
        File fileToOpen = new File(fileToOpenName);

        if (letter.exists()) if (letter.delete()) System.out.println("Файл " + letter.getName() + " был удалён.");
        if (gzipLetter.exists()) if (gzipLetter.delete()) System.out.println("Файл " + gzipLetter.getName() + " был удалён.");
        if (fileToOpen.exists()) if (fileToOpen.delete()) System.out.println("Файл " + fileToOpen.getName() + " был удалён.");

    }

    /*
        Проверка на входной файл. Если он отсутствует, то запускается окно настроек.
     */
    private static boolean checkInputFile() {

        File inputFile = new File(fileToOpenName);

        return inputFile.exists();

    }

    /*
        Входной метод программы.
     */
    public static void main(String[] args) {

        errors = false;

        settingsFilePath = "ssettings.ini";
        FIOFilePath = ".." + System.getProperty("file.separator") + "pers.ini";
        fileToOpenName = "post.dat";
        adressToSendFrom = "rostest.sort@gmail.com";

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final Window window = new Window();

                if (!checkInputFile()) {
                    window.tuneProgramm("Вы вошли в режим настроек.", false);
                } else {
                    getSettings(window);
                    if (!errors) startTasks(window);
                }

            }
        });

    }

    public static void startTasks(final Window window) {
        errors = false;

        window.processingFrame();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!errors) parse(window);
                if (!errors) checkDataBase(window);
                if (!errors) runJDBC(window);
                if (!errors) createQueryAndWriteIntoFile(window, nameTables, values);
                if (!errors) zip(window);
                closeAll();

                if (!errors) saveToDirectory(window);
                clearAll();

                if (!errors) window.doneSuccess();
            }
        }).start();

    }
}
