package ajoy.com.fairmanagementapp.pojo;

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
            L.m("create from parcel :Movie");
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private int id;
    private String title;
    private double price;
    private String thumbnail;

    public Product() {

    }

    public Product(Parcel input) {
        id = input.readInt();
        title = input.readString();
        price=input.readDouble();
        thumbnail=input.readString();
    }

    public Product(int id, String title, double price, String thumbnail) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\nTitle " + title +
                "\nPrice: "+price+
                "\nThumbnail "+thumbnail+
                "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeDouble(price);
        parcel.writeString(thumbnail);
    }
}
