package ajoy.com.fairmanagementapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import  ajoy.com.fairmanagementapp.logging.L;

/**
 * Created by ajoy on 5/19/16.
 */
public class Product implements Parcelable {

    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            L.m("create from parcel :Products");
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private int id;
    private String stall;
    private String name;
    private String company;
    private String description;
    private String price;
    private String availability;
    private String image;

    public Product() {

    }

    public Product(Parcel input) {
        id = input.readInt();
        stall = input.readString();
        name = input.readString();
        company = input.readString();
        description = input.readString();
        price=input.readString();
        availability = input.readString();
        image=input.readString();
    }

    public Product(int id, String stall, String name, String company, String description, String price, String availability, String image) {
        this.id = id;
        this.stall = stall;
        this.name = name;
        this.company = company;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Creator<Product> getCREATOR() {
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
        parcel.writeString(stall);
        parcel.writeString(name);
        parcel.writeString(company);
        parcel.writeString(description);
        parcel.writeString(price);
        parcel.writeString(availability);
        parcel.writeString(image);
    }
}
