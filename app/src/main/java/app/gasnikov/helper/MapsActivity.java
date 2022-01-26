package app.gasnikov.helper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG ="qweq3eqd" ;
    private GoogleMap mMap;
    private LocationModel locationModel;
    private Incident incident1;
    private String userid,userfn;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private Marker marker,marker1;
    private static final int LOCATION_UPDATE_INTERVAL = 8000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            locationModel = (LocationModel) getIntent().getParcelableExtra("Loc");
            incident1=(Incident)getIntent().getParcelableExtra("Inc");
            locationModel = arguments.getParcelable(LocationModel.class.getSimpleName());
          incident1=arguments.getParcelable(Incident.class.getSimpleName());
            userfn = arguments.getString("name");
            userid = arguments.getString("id");

        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUserLocationsRunnable();
    }

    private void startUserLocationsRunnable(){

        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }

    private void retrieveUserLocations(){

        try{

                db.getReference("User Locations").child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            final UserLocation updatedUserLocation = task.getResult().getValue(UserLocation.class);

                            LatLng updatedLatLng = new LatLng(updatedUserLocation.getGeo_point().getLat(), updatedUserLocation.getGeo_point().getLng());
                            marker1.remove();

                            Log.d(TAG,String.valueOf("Hello:" + updatedUserLocation.getGeo_point().getLat()+ "   "+ updatedUserLocation.getGeo_point().getLng()));
                            marker1=mMap.addMarker(new MarkerOptions().position(updatedLatLng).title(userfn));



                        }


                    }
                });



            }
        catch (IllegalStateException e){

        }

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (incident1 != null) {
            LatLng incident = new LatLng(incident1.getLocationModel().getLat(), incident1.getLocationModel().getLng());
            marker = mMap.addMarker(new MarkerOptions().position(incident).title(incident1.getType()));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(incident));
        }
        if (locationModel != null) {
            LatLng sydney = new LatLng(locationModel.getLat(), locationModel.getLng());
            marker1 = mMap.addMarker(new MarkerOptions().position(sydney).title(userfn));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
        }
    }
}