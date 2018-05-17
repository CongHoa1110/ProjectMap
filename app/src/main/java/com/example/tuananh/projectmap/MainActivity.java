package com.example.tuananh.projectmap;

import android.app.ListFragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuananh.projectmap.app.ProjectMapApplication;
import com.example.tuananh.projectmap.model.MapResponse;
import com.example.tuananh.projectmap.model.PlaceResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;

    private ImageView imgNext;
    private ImageView imgBack;

    private LinearLayout lnDirection;
    private LinearLayout lnPlace;

    private EditText edSource;
    private EditText edDestination;
    private EditText edSearch;
    private Button btSearchPlace;

    String source, destination, search, keyMap, location, radius, keyPlace, type;

    private Spinner spinnerType;

    String arrayType[] = {"ATM", "restaurant", "hospital"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();

    }

    private void initial() {

        imgNext = (ImageView) findViewById(R.id.imgNext);
        imgBack = (ImageView) findViewById(R.id.imgBack);

        lnDirection = (LinearLayout) findViewById(R.id.lnDirection);
        lnPlace = (LinearLayout) findViewById(R.id.lnPlace);

        edSource = (EditText) findViewById(R.id.edSource);
        edDestination = (EditText) findViewById(R.id.edDestination);
        edSearch = (EditText) findViewById(R.id.edSearch);

        btSearchPlace = (Button) findViewById(R.id.btSearchPlace);

        spinner();


        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnDirection.setVisibility(View.GONE);
                lnPlace.setVisibility(View.VISIBLE);

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnPlace.setVisibility(View.GONE);
                lnDirection.setVisibility(View.VISIBLE);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);


        btSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlace();
            }
        });
    }

    public void kickSearch(View v) {
        source = edSource.getText().toString().trim();
        destination = edDestination.getText().toString().trim();
        search = edSearch.getText().toString().trim();

        if (TextUtils.isEmpty(source)) {
            Toast.makeText(this, "Nhap diem di", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(destination)) {
            Toast.makeText(this, "Nhap diem den", Toast.LENGTH_SHORT).show();
            return;
        }


        getDirection();


    }

    private void getDirection() {
        source = edSource.getText().toString().trim();
        destination = edDestination.getText().toString().trim();
        keyMap = "AIzaSyDoutcRpVShBwpHXy3dmV6gdujXszAheqg";

        Call<MapResponse> call = ProjectMapApplication.apiService.getDirection(source, destination, keyMap);
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {

                String placeId = response.body().getGeocodedWaypoints().get(0).getPlaceId();
                String StringPoints = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                Log.d("Place", placeId);

                List<LatLng> latLngs = decodePoly(StringPoints);
                Log.d("String", latLngs.size() + "");

                drawPatch(latLngs);
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
    }

    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


    }

    private void drawPatch(List<LatLng> latLngs) {
        PolylineOptions rectOptions = new PolylineOptions();

        for (int i = 0; i < latLngs.size(); i++) {
            rectOptions.add(latLngs.get(i));
            mGoogleMap.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(latLngs.get(i)));
        }

        mGoogleMap.addPolyline(rectOptions);

        LatLng sydney = latLngs.get(0);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
    }

    private void getPlace() {
        location = edSearch.getText().toString().trim();
        location = "21.686886,105.686868";
        keyPlace = "AIzaSyCsVwT3zUM2DPYMu7QUimvIBj2VqE-IDQM";
        radius = "5000";
        String type = arrayType[spinnerType.getSelectedItemPosition()];

        Call<PlaceResponse> call = ProjectMapApplication.apiService.getPlace(location, radius, type, keyPlace);
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                List<PlaceResponse.Result> results = response.body().getResults();
                for (int i = 0; i < results.size(); i++) {
                    PlaceResponse.Result result = results.get(i);

                    LatLng latLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                    String title = result.getName();
                    String snippet = result.getVicinity();
                    mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(snippet));

                    if (i == 0) {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 13, 0, 45)));
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {

            }
        });

    }

    private void spinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayType);
        spinnerType = (Spinner) findViewById(R.id.spType);

        spinnerType.setAdapter(adapter);


    }
}
