package tfs.geobat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    double latitude, longitude;
    String friendname;
    String distance;
    double currentLat;
    double currentLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get latitude, longitude, name and distance of friend
        latitude = Double.parseDouble(getIntent().getExtras().getString("latitude"));
        longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));
        friendname = getIntent().getExtras().getString("friendname");
        distance = getIntent().getExtras().getString("distance");
        currentLat = Double.parseDouble(getIntent().getExtras().getString("currentLat"));
        currentLong = Double.parseDouble(getIntent().getExtras().getString("currentLong"));

    }



    @Override
    public void onMapReady(GoogleMap map) {
        //set a marker in the friend's position
        LatLng frientPosition = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(frientPosition).title(friendname + ": " + distance));
        map.moveCamera(CameraUpdateFactory.newLatLng(frientPosition));
        map.setMyLocationEnabled(true);

        //connect the friend's position with the actual device's position
        PolylineOptions line=
                new PolylineOptions().add(new LatLng(latitude,longitude), new LatLng( currentLat,  currentLong))
                        .width(5).color(Color.BLUE);

        map.addPolyline(line);
    }
}
