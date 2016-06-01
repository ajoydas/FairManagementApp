package ajoy.com.fairmanagementapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import ajoy.com.fairmanagementapp.logging.L;

/**
 * Created by ajoy on 5/31/16.
 */
public class Employee implements Parcelable {
    public static final Parcelable.Creator<Employee> CREATOR
            = new Parcelable.Creator<Employee>() {
        public Employee createFromParcel(Parcel in) {
            L.m("create from parcel :Products");
            return new Employee(in);
        }

        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    private int id;
    private String stall;
    private String name;
    private String description;
    private String contact_no;
    private String position;
    private String salary;

    public Employee() {

    }

    public Employee(Parcel input) {
        id = input.readInt();
        stall = input.readString();
        name = input.readString();
        description = input.readString();
        contact_no =input.readString();
        position = input.readString();
        salary =input.readString();
    }

    public Employee(int id, String stall, String name, String description, String contact_no, String position, String salary) {
        this.id = id;
        this.stall = stall;
        this.name = name;
        this.description = description;
        this.contact_no = contact_no;
        this.position = position;
        this.salary = salary;
    }

    public static Creator<Employee> getCREATOR() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "\nID: " + id +
                "\nStall: " + stall +
                "\nName: "+name+
                "\nDescription: "+description+
                "\nContactNo: "+ contact_no +
                "\nPosition: "+ position +
                "\nSalary: "+ salary +
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
        parcel.writeString(description);
        parcel.writeString(contact_no);
        parcel.writeString(position);
        parcel.writeString(salary);
    }
}
