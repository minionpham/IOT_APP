package model;

public class User {
    private String UserId;
    private String UserName;
    private int Balance;
    private String State;

    public User(String userName, int balance, String userId, String state) {
        UserName = userName;
        Balance = balance;
        UserId = userId;
        State = state;
    }
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getBalance() {
        return Balance;
    }

    public void setBalance(int balance) {
        Balance = balance;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
