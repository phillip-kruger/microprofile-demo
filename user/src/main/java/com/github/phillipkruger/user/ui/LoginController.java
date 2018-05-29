package com.github.phillipkruger.user.ui;

import com.github.phillipkruger.user.ui.util.SessionUtils;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
@Named
@SessionScoped
public class LoginController implements Serializable {
    
    private String password;
    private String message;
    private String user;

    //validate login
    public String validateUsernamePassword() {
        
        boolean valid = true;// LoginDAO.validate(user, pwd);
        if (valid) {
            HttpSession session = SessionUtils.getSession();
            session.setAttribute("username", user);
            return "index";
        } else {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Incorrect Username and Passowrd",
                            "Please enter correct username and Password"));
            return "login";
        }
    }

    //logout event, invalidate session
    public String logout() {
        HttpSession session = SessionUtils.getSession();
        session.invalidate();
        return "login";
    }
}
