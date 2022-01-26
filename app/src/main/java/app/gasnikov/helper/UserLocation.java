package app.gasnikov.helper;


public class UserLocation {

    private LocationModel geo_point;

    private User user;


    public UserLocation(LocationModel geo_point, User user) {
        this.geo_point = geo_point;

        this.user = user;
    }

    public UserLocation() {

    }


    public LocationModel getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(LocationModel geo_point) {
        this.geo_point = geo_point;
    }







    public void setUser(User user) {
        this.user = user;
    }



    public User getUser() {
        return user;
    }


}
