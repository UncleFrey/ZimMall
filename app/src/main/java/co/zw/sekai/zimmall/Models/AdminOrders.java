package co.zw.sekai.zimmall.Models;

public class AdminOrders {

    private String address, btnAction, name, phone, provinceName, state, stateMsg, totalBill,
            townCityName, date, time;

    public AdminOrders() {
    }

    public AdminOrders(String address, String btnAction, String name, String phone,
                       String provinceName, String state, String stateMsg, String totalBill,
                       String townCityName, String date, String time) {
        this.address = address;
        this.btnAction = btnAction;
        this.name = name;
        this.phone = phone;
        this.provinceName = provinceName;
        this.state = state;
        this.stateMsg = stateMsg;
        this.totalBill = totalBill;
        this.townCityName = townCityName;
        this.date = date;
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBtnAction() {
        return btnAction;
    }

    public void setBtnAction(String btnAction) {
        this.btnAction = btnAction;
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

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateMsg() {
        return stateMsg;
    }

    public void setStateMsg(String stateMsg) {
        this.stateMsg = stateMsg;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalBill) {
        this.totalBill = totalBill;
    }

    public String getTownCityName() {
        return townCityName;
    }

    public void setTownCityName(String townCityName) {
        this.townCityName = townCityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
