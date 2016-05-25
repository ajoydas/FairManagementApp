package ajoy.com.fairmanagementapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.util.Date;

import ajoy.com.fairmanagementapp.logging.L;

/**
 * Created by ajoy on 5/25/16.
 */
public class Stall implements Parcelable {
    public static final Parcelable.Creator<Stall> CREATOR
            = new Parcelable.Creator<Stall>() {
        public Stall createFromParcel(Parcel in) {
            L.m("create from parcel :Movie");
            return new Stall(in);
        }

        public Stall[] newArray(int size) {
            return new Stall[size];
        }
    };

    private int id;
    private String stall;
    private String stall_name;
    private String owner;
    private String description;
    private String location;

    public Stall() {
    }

    public Stall(Parcel input) {
        id = input.readInt();
        stall = input.readString();
        stall_name= input.readString();
        owner = input.readString();
        description = input.readString();
        location= input.readString();
    }

    public Stall(int id, String stall, String stall_name, String owner, String description, String location) {
        this.id = id;
        this.stall = stall;
        this.stall_name = stall_name;
        this.owner = owner;
        this.description = description;
        this.location = location;
    }

    public static Parcelable.Creator<Stall> getCREATOR() {
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

    public String getStall_name() {
        return stall_name;
    }

    public void setStall_name(String stall_name) {
        this.stall_name = stall_name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\n Stall: "+stall+
                "\nStallName: " + stall_name +
                "\nOwner: "+owner+
                "\nDescription: "+description+
                "\nLocation: "+location+
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
        parcel.writeString(stall_name);
        parcel.writeString(owner);
        parcel.writeString(description);
        parcel.writeString(location);

    }
}
