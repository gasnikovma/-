package app.gasnikov.helper;

import android.os.Parcel;
import android.os.Parcelable;

public class Incident implements Parcelable {
    public LocationModel locationModel;
    public String type;

    public Incident(LocationModel locationModel, String type) {
        this.locationModel = locationModel;
        this.type = type;
    }
    public Incident(){

    }

    protected Incident(Parcel in) {
        locationModel = in.readParcelable(LocationModel.class.getClassLoader());
        type = in.readString();
    }

    public static final Creator<Incident> CREATOR = new Creator<Incident>() {
        @Override
        public Incident createFromParcel(Parcel in) {
            return new Incident(in);
        }

        @Override
        public Incident[] newArray(int size) {
            return new Incident[size];
        }
    };

    public LocationModel getLocationModel() {
        return locationModel;
    }

    public void setLocationModel(LocationModel locationModel) {
        this.locationModel = locationModel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(locationModel, flags);
        dest.writeString(type);
    }
}
