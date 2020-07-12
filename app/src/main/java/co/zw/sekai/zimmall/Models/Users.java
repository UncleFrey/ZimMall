package co.zw.sekai.zimmall.Models;

public class Users

{
    private String name, phone, pin, imageUrl, address, balance, firstTime;

    public Users() {
    }

    public Users(String name, String phone, String pin, String imageUrl, String address, String balance,
                 String firstTime) {
        this.name = name;
        this.phone = phone;
        this.pin = pin;
        this.imageUrl = imageUrl;
        this.address = address;
        this.balance = balance;
        this.firstTime = firstTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }
}
