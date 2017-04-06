package com.seeyon.dee.configurator.dto;

public class UserPwd {
    
    private String oldPwd;
    
    private String newPwd;
    
    private String rnewPwd;

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getRnewPwd() {
        return rnewPwd;
    }

    public void setRnewPwd(String rnewPwd) {
        this.rnewPwd = rnewPwd;
    }
}
