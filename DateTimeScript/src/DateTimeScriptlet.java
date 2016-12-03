import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

/**
 * Created by Dds on 16.07.2016.
 */
public class DateTimeScriptlet extends JRDefaultScriptlet {

    public static void main(String[] args) {

        String res_tab = "0,10 -0,09 0,05 0,00 -0,04 0,02 1,77 3,71 -0,98 0,00 -0,81 -0,17";

        System.out.println(res_tab.trim().split(" ")[11]);

    }

    public String getDate (String strDay, String month, String strYear) throws JRScriptletException {
        int day = Integer.parseInt(strDay);
        int year = Integer.parseInt(strYear);

        if (month.equals("января") && day == 1) {
            month = "декабря";
            day = 31;
        } else if (month.equals("февраля") && day == 1) {
            month = "января";
            day = 31;
            year += 1;
        } else if (month.equals("марта") && day == 1) {
            month = "февраля";
            if ((year + 1) % 4 == 0) day = 29;
            else day = 28;
            year += 1;
        } else if (month.equals("апреля") && day == 1) {
            month = "марта";
            day = 31;
            year += 1;
        } else if (month.equals("мая") && day == 1) {
            month = "апреля";
            day = 30;
            year += 1;
        } else if (month.equals("июня") && day == 1) {
            month = "мая";
            day = 31;
            year += 1;
        } else if (month.equals("июля") && day == 1) {
            month = "июня";
            day = 30;
            year += 1;
        } else if (month.equals("августа") && day == 1) {
            month = "июля";
            day = 31;
            year += 1;
        } else if (month.equals("сентября") && day == 1) {
            month = "августа";
            day = 31;
            year += 1;
        } else if (month.equals("октября") && day == 1) {
            month = "сентября";
            day = 30;
            year += 1;
        } else if (month.equals("ноября") && day == 1) {
            month = "октября";
            day = 31;
            year += 1;
        } else if (month.equals("декабря") && day == 1) {
            month = "ноября";
            day = 30;
            year += 1;
        } else {
            day -= 1;
            year += 1;
        }

        if (day < 10) strDay = "0" + day;
        else strDay = "" + day;

        return "«" + strDay + "» " + month + " " + year + " г.";
    }

}
