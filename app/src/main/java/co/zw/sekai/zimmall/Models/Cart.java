package co.zw.sekai.zimmall.Models;

public class Cart {

    private String pid, productDate, productDiscount, productName, productPrice, productQty,
            productTime, subTotal;

    public Cart() {
    }

    public Cart(String pid, String productDate, String productDiscount, String productName,
                String productPrice, String productQty, String productTime, String subTotal) {
        this.pid = pid;
        this.productDate = productDate;
        this.productDiscount = productDiscount;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.productTime = productTime;
        this.subTotal = subTotal;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getProductTime() {
        return productTime;
    }

    public void setProductTime(String productTime) {
        this.productTime = productTime;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
}
