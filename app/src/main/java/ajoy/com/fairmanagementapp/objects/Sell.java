package ajoy.com.fairmanagementapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import  ajoy.com.fairmanagementapp.logging.L;

/**
 * Created by ajoy on 5/19/16.
 */
public class Sell implements Parcelable {

    public static final Parcelable.Creator<Sell> CREATOR
            = new Parcelable.Creator<Sell>() {
        public Sell createFromParcel(Parcel in) {
            L.m("create from parcel :Sells");
            return new Sell(in);
        }

        public Sell[] newArray(int size) {
            return new Sell[size];
        }
    };

    private int id;
    private String stall;
    private String product_name;
    private String employee_name;
    private String date;
    private String time;
    private String price;
    private String description;




    public Sell() {

    }

    public Sell(Parcel input) {
        id = input.readInt();
        stall = input.readString();
        product_name = input.readString();
        employee_name = input.readString();
        date = input.readString();
        time = input.readString();
        price = input.readString();
        description = input.readString();
    }

    public Sell(int id, String stall, String product_name, String employee_name, String date, String time, String price, String description) {
        this.id = id;
        this.stall = stall;
        this.product_name = product_name;
        this.employee_name = employee_name;
        this.date = date;
        this.time = time;
        this.price = price;
        this.description = description;
    }

    public static Creator<Sell> getCREATOR() {
        return CREATOR;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStall() {
        return stall;
    }

    public void setStall(String stall) {
        this.stall = stall;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\nStall: " + stall +
                "\nProductName: "+product_name+
                "\nEmployeeName: "+employee_name+
                "\nDate: "+ date +
                "\nTime: "+ time +
                "\nPrice: "+price+
                "\nDescription: "+description+
                "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(stall);
        parcel.writeString(product_name);
        parcel.writeString(employee_name);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(price);
        parcel.writeString(description);
    }
}
