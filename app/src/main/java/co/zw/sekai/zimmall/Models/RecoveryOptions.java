package co.zw.sekai.zimmall.Models;

public class RecoveryOptions {
    private String phone, qst, ans, state;

    public RecoveryOptions() {
    }

    public RecoveryOptions(String phone, String qst, String ans, String state) {
        this.phone = phone;
        this.qst = qst;
        this.ans = ans;
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQst() {
        return qst;
    }

    public void setQst(String qst) {
        this.qst = qst;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
