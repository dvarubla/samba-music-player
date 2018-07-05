package com.dvarubla.sambamusicplayer.smbutils;

public class LoginPass {
    private String _login, _pass;
    public LoginPass(String login, String pass){
        _login = login;
        _pass = pass;
    }

    public String getLogin() {
        return _login;
    }

    public String getPass() {
        return _pass;
    }

    public void setLogin(String login) {
        _login = login;
    }

    public void setPass(String pass) {
        _pass = pass;
    }
}
