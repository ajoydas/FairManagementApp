package ajoy.com.fairmanagementapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.util.Date;

import ajoy.com.fairmanagementapp.logging.L;

/**
 * Created by ajoy on 5/22/16.
 */
public class Fair implements Parcelable{
    public static final Parcelable.Creator<Fair> CREATOR
            = new Parcelable.Creator<Fair>() {
        public Fair createFromParcel(Parcel in) {
            L.m("create from parcel :Movie");
            return new Fair(in);
        }

        public Fair[] newArray(int size) {
            return new Fair[size];
        }
    };

    private int id;
    private String title;
    private String db_name;
    private String organizer;
    private String location;
    private Date start_date;
    private Date end_date;
    private Time open_time;
    private Time close_time;
    private String map_address;

    public Fair() {

    }

    public Fair(Parcel input) {
        id = input.readInt();
        db_name= input.readString();
        title = input.readString();
        organizer= input.readString();
        location= input.readString();
        long dateMillis=input.readLong();
        start_date = (dateMillis == -1 ? null : new Date(dateMillis));
        dateMillis=input.readLong();
        end_date=(dateMillis == -1 ? null : new Date(dateMillis));
        dateMillis=input.readLong();
        open_time=(dateMillis == -1 ? null : new Time(dateMillis));
        dateMillis=input.readLong();
        close_time=(dateMillis == -1 ? null : new Time(dateMillis));
        map_address=input.readString();
    }

    public Fair(int id, String title, String db_name, String organizer, String location, Date start_date, Date end_date, Time open_time, Time close_time, String map_address) {
        this.id = id;
        this.db_name = db_name;
        this.title = title;
        this.organizer = organizer;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.open_time = open_time;
        this.close_time = close_time;
        this.map_address = map_address;
    }

    public static Creator<Fair> getCREATOR() {
        return CREATOR;
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

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
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

    public String getMap_address() {
        return map_address;
    }

    public void setMap_address(String map_address) {
        this.map_address = map_address;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\n DabaseName: "+db_name+
                "\nTitle: " + title +
                "\nOrganizer: "+organizer+
                "\nLocation: "+location+
                "\nStartDate: "+start_date+
                "\nEndDate: "+end_date+
                "\nOpenTime: "+open_time+
                "\nCloseTime: "+close_time+
                "\nMapAddress: "+map_address+
                "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(db_name);
        parcel.writeString(title);
        parcel.writeString(organizer);
        parcel.writeString(location);
        parcel.writeLong(start_date == null ? -1 : start_date.getTime());
        parcel.writeLong(end_date == null ? -1 : end_date.getTime());
        parcel.writeLong(open_time == null ? -1 : open_time.getTime());
        parcel.writeLong(close_time == null ? -1 : close_time.getTime());
        parcel.writeString(map_address);

    }
}
