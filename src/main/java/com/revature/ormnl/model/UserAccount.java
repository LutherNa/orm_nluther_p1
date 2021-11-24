package com.revature.ormnl.model;
import java.io.Serializable;

public class UserAccount implements Serializable {
    private int AccountID;
    private String Username;
    private String Password;
    private String FirstName;
    private String LastName;
    //private AllowedBankAccounts int[];
    //needs dynamic array implementation

    public UserAccount() {
    }

    public UserAccount(String Username, String Password) {
        this.setUsername(Username);
        this.setPassword(Password);
    }
    public UserAccount(String Username, String Password, String FirstName, String LastName) {
        this.setUsername(Username);
        this.setPassword(Password);
        this.setFirstName(FirstName);
        this.setLastName(LastName);
    }

//    Setters and Getters
    public String getUsername() {
        return Username;
    }
    public void setUsername(String Username) {
        this.Username = Username;
    }
    public String getPassword() {return Password;}
    public void setPassword(String password) {
        Password = password;
    }
    public int getUserId() {return AccountID;}
    public void setAccountID(int accountID) {
        AccountID = accountID;
    }
    public String getFirstName() {
        return FirstName;
    }
    public void setFirstName(String firstName) {
        FirstName = firstName;
    }
    public String getLastName() {
        return LastName;
    }
    public void setLastName(String lastName) {
        LastName = lastName;
    }
}