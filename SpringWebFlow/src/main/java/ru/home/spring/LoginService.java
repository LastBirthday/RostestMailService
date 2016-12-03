package main.java.ru.home.spring;

import org.springframework.stereotype.Service;

/**
 * Created by Dds on 20.11.2016.
 */

@Service
public class LoginService {

    public String validateUser (LoginBean loginBean) {

        String userName = loginBean.getUserName();
        String password = loginBean.getPassword();

        if (userName.equals("Alba") && password.equals("test")) {
            return "true";
        } else return "false";

    }

}
