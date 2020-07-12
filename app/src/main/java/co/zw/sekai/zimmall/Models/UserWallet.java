package co.zw.sekai.zimmall.Models;

public class UserWallet {
    private String phone, balance;

    public UserWallet() {
    }

    public UserWallet(String phone, String balance) {
        this.phone = phone;
        this.balance = balance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
