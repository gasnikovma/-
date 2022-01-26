package app.gasnikov.helper;

import android.os.Parcel;
import android.os.Parcelable;


public class User implements Parcelable {
    public String fullname,email,blood_type,rh_factor,cd,ar,id;
    public int number_of_calls;
    public User(){

    }

    public User(String fullname, String email,String blood_type,String rh_factor,String cd,String ar,String id,int number){
        this.blood_type=blood_type;
        this.fullname=fullname;
        this.email=email;
        this.id=id;
        this.rh_factor=rh_factor;
        this.cd=cd;
        this.ar=ar;
        number_of_calls=number;

    }
    public User(String fullname, String email,String blood_type,String rh_factor,String cd,String ar,String id){
        this.blood_type=blood_type;
        this.fullname=fullname;
        this.email=email;
        this.id=id;
        this.rh_factor=rh_factor;
        this.cd=cd;
        this.ar=ar;


    }


    protected User(Parcel in) {
        fullname = in.readString();
        email = in.readString();
        blood_type = in.readString();
        rh_factor = in.readString();
        cd = in.readString();
        ar = in.readString();
        id = in.readString();
        number_of_calls=in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFullname() {
        return fullname;
    }

    public String getId() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullname);
        dest.writeString(email);
        dest.writeString(blood_type);
        dest.writeString(rh_factor);
        dest.writeString(cd);
        dest.writeString(ar);
    }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type(String blood_type) {
        this.blood_type = blood_type;
    }

    public String getRh_factor() {
        return rh_factor;
    }

    public void setRh_factor(String rh_factor) {
        this.rh_factor = rh_factor;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getAr() {
        return ar;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumber_of_calls() {
        return number_of_calls;
    }

    public void setNumber_of_calls(int number_of_calls) {
        this.number_of_calls = number_of_calls;
    }
}
