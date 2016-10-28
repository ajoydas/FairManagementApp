package ajoy.com.fairmanagementapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.util.Date;

import ajoy.com.fairmanagementapp.logging.L;

/**
 * Created by hp on 22-10-2016.
 */

public class FavProduct implements Parcelable {

    public static final Parcelable.Creator<FavProduct> CREATOR
            = new Parcelable.Creator<FavProduct>() {
        public FavProduct createFromParcel(Parcel in) {
            L.m("create from parcel :Products");
            return new FavProduct(in);
        }

        public FavProduct[] newArray(int size) {
            return new FavProduct[size];
        }
    };

    private int id;
    private String table;
    private String productid;
    private String fair;
    private String location;
    private Date start_date;
    private Date end_date;
    private Time open_time;
    private Time close_time;
    private String stall;
    private String name;
    private String company;
    private String description;
    private String price;
    private String availability;
    private String image;
    private String stalllocation;

    public FavProduct() {
    }

    public FavProduct(Parcel input) {
        id = input.readInt();
        table = input.readString();
        productid = input.readString();
        fair = input.readString();
        location = input.readString();
        long dateMillis=input.readLong();
        start_date = (dateMillis == -1 ? null : new Date(dateMillis));
        dateMillis=input.readLong();
        end_date=(dateMillis == -1 ? null : new Date(dateMillis));
        dateMillis=input.readLong();
        open_time=(dateMillis == -1 ? null : new Time(dateMillis));
        dateMillis=input.readLong();
        close_time=(dateMillis == -1 ? null : new Time(dateMillis));
        stall = input.readString();
        name = input.readString();
        company = input.readString();
        description = input.readString();
        price=input.readString();
        availability = input.readString();
        image=input.readString();
        stalllocation=input.readString();

    }

    public FavProduct(int id,String table,String productid, String fair, String location, Date start_date, Date end_date, Time open_time, Time close_time, String stall, String name, String company, String description, String price, String availability, String image,String stalllocation) {
        this.id = id;
        this.table = table;
        this.productid = productid;
        this.fair = fair;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.open_time = open_time;
        this.close_time = close_time;
        this.stall = stall;
        this.name = name;
        this.company = company;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.image = image;
        this.stalllocation = stalllocation;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getStalllocation() {
        return stalllocation;
    }

    public void setStalllocation(String stalllocation) {
        this.stalllocation = stalllocation;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFair() {
        return fair;
    }

    public void setFair(String fair) {
        this.fair = fair;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Time getOpen_time() {
        return open_time;
    }

    public void setOpen_time(Time open_time) {
        this.open_time = open_time;
    }

    public Time getClose_time() {
        return close_time;
    }

    public void setClose_time(Time close_time) {
        this.close_time = close_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Creator<FavProduct> getCREATOR() {
        return CREATOR;
    }

    public String getStall() {
        return stall;
    }

    public void setStall(String stall) {
        this.stall = stall;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\nTable: " + table +
                "\nFair: " + fair +
                "\nProductId: " + productid +
                "\nLocation: "+location+
                "\nStartDate: "+start_date+
                "\nEndDate: "+end_date+
                "\nOpenTime: "+open_time+
                "\nCloseTime: "+close_time+
                "\nStall: " + stall +
                "\nName: "+name+
                "\nCompany: "+company+
                "\nDescription: "+description+
                "\nPrice: "+price+
                "\nAvailability: "+availability+
                "\nImage: "+image+
                "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(table);
        parcel.writeString(productid);
        parcel.writeString(fair);
        parcel.writeString(location);
        parcel.writeLong(start_date == null ? -1 : start_date.getTime());
        parcel.writeLong(end_date == null ? -1 : end_date.getTime());
        parcel.writeLong(open_time == null ? -1 : open_time.getTime());
        parcel.writeLong(close_time == null ? -1 : close_time.getTime());
        parcel.writeString(stall);
        parcel.writeString(name);
        parcel.writeString(company);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(availability);
        parcel.writeString(image);
    }
}
