package com.home;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;


/**
 * Created by Dds on 18.02.2016.
 * Version = 1.13
 * ---------------Change log 1.10------------------
 * - Added invokeLater process
 * - Window not static anymore
 * - Changed setVisible at the beginning, that caused small frame at the corner for a short time
 * - Changed button/labels font styles
 * - Remodeled tuneProgram window
 * - Added new functionality
 * - Setted new icon
 * - Changed post.dat parse format
 * ---------------Change log 1.11------------------
 * - Added mails counter
 * - Added old email name on text field
 * - Name changed
 * ---------------Change log 1.12------------------
 * - Little output fixes
 * - Bug fixed
 * ---------------Change log 1.13------------------
 * - Buttons react on hit enter now
 * - Post.dat deleting in any scenarios
 * ---------------Change log 1.14------------------
 * - error.err file added
 * - FileNotFoundException message displaying and catching properly
 * ---------------Change log 1.15------------------
 * - Writing method changed;
 * - Error file now creates in any scenarios where letter was not sent
 * - Email message changed;
 */
public class Client {
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
    public static String adressToSendTo;

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
            errors = window.criticalError("<html><div style='text-align: center;'>Драйвер базы данных не найден. Обратитесь к разработчику.</html>");
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
            boolean two;
            two = false;
            while ((s = reader.readLine()) != null) {
                String cmd = s.substring(0, s.indexOf('=') + 1);
                switch (cmd) {
                    case "emailTo=":
                        adressToSendTo = s.substring(cmd.length());
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
            errors = window.criticalError("<html><div style='text-align: center;'>Не удалось произвести компрессию файла. Ошибка: " + e + "</html>");
            e.printStackTrace();
        }

        File archive = new File(fileToWriteName + ".gzip");

        if (archive.length() > 26214000) errors = window.criticalError("<html><div style='text-align: center;'>Размер Вашего файла " +
                "превышает 25 МБ, что является предельно допустимым размером файла при посылке на почту. Пожалуйста, разбейте Ваш " +
                "запрос на несколько частей и отправьте частями.</html>");

    }

    /*
        Пересылка email сообщения с хостинга gmail через SSL сертификат с прикреплённым файлом.
     */
    static int count;
    private static void sendEmailSSL(final Window window) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(adressToSendFrom, "q1w2alli627baba");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(adressToSendFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(adressToSendTo));
            message.setSubject(fileToWriteName);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Это автоматически сгенерированное письмо с вложением. Поместите данное вложение " +
                    "в папку для входящих писем и запустите Server.exe, чтобы внести данные в базу данных " +
                    "(запустите Server.exe без писем, чтобы узнать путь к этой папке). Дополнительно могут быть прикреплены фотографии, " +
                    "которые также следует сохранить в данную папку.");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(fileToWriteName + ".gzip");
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileToWriteName + ".gzip");
            multipart.addBodyPart(messageBodyPart);

            File archive = new File(fileToWriteName + ".gzip");
            long size = archive.length();
            count = 1;
            for (int i = 0; i < photos.size(); i++) {
                File photo = new File(photos.get(i));
                if (size + photo.length() < 26214000) {
                    size += photo.length();
                    messageBodyPart = new MimeBodyPart();
                    source = new FileDataSource(photo);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(photo.getName());
                    multipart.addBodyPart(messageBodyPart);
                } else {
                    count++;
                    message.setContent(multipart);
                    Transport.send(message);
                    System.out.println("Part was send");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            window.processingFrame(count);
                        }
                    }).start();
                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText("Продолжение письма, содержащее фотографии.");
                    multipart = new MimeMultipart();
                    multipart.addBodyPart(messageBodyPart);

                    messageBodyPart = new MimeBodyPart();
                    source = new FileDataSource(photo);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(photo.getName());
                    multipart.addBodyPart(messageBodyPart);
                    size = photo.length();
                }
            }

            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            if (e.toString().contains("FileNotFoundException")) {
                errors = window.criticalError("<html><div style='text-align: center;'>Не удается найти файл по указанному пути." +
                        " Ошибка:<br>" + e + "</html>");
                e.printStackTrace();
            } else {
                errors = window.criticalError("<html><div style='text-align: center;'>Не удалось отправить письмо на почту. Проверьте есть ли соединение " +
                        "с интернетом. Возможно, настройки вашего соединения блокируют сервера gmail. Ошибка: " + e + "</html>");
                e.printStackTrace();
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

        settingsFilePath = "settings.ini";
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

        window.processingFrame(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!errors) parse(window);
                if (!errors) checkDataBase(window);
                if (!errors) runJDBC(window);
                if (!errors) createQueryAndWriteIntoFile(window, nameTables, values);
                if (!errors) zip(window);
                closeAll();

                if (!errors) sendEmailSSL(window);
                clearAll();

                if (!errors) window.doneSuccess();
            }
        }).start();

    }

}
