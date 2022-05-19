package com.example.reservasdeportes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.databinding.FragmentMapsBinding;
import com.example.reservasdeportes.model.FacilityDTO;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FacilityDTO facilityDTO;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng latLng;
    private Location lastKnownLocation;
    private boolean firstLocation = true;
    private Circle me;

    private final float DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentMapsBinding binding = FragmentMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Recupera la clase de datos del intent
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        facilityDTO = getIntent().getParcelableExtra("facilityDTO");

        //Obtiene el mapFragment y notifica cuando esta listo para ser usado
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //Crea un marcador en la posicion de la instalacion
        latLng = new LatLng(facilityDTO.getLatitude(), facilityDTO.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title(facilityDTO.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

        updateLocationUI();
        checkPermissions();
    }

    //Si es posible abre el GUI de localizacion
    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //Si es posible obtiene la ubicacion del dispositivo
    private void checkPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getDeviceLocation();
                }
            } else {
                getDeviceLocation();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    //Obtiene la ultima ubicacion cocnocida del dispositivo
    private void getDeviceLocation() {
        @SuppressLint("MissingPermission")
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                lastKnownLocation = task.getResult();
                if (lastKnownLocation != null) {
                    if (firstLocation) {
                        drawPosition();
                        firstLocation = false;
                    }
                    me.setCenter(new LatLng(lastKnownLocation.getLatitude(),
                            lastKnownLocation.getLongitude()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    drawRoute();
                }
            }
        });
    }

    //Dibuja la posicion actual
    private void drawPosition() {
        me = mMap.addCircle(new CircleOptions()
                .center(new LatLng(-lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                .radius(10)
                .strokeColor(Color.RED)
                .fillColor(Color.RED));
    }

    //Dibuja la ruta
    private void drawRoute() {
        List<LatLng> path = new ArrayList<>();
        path.add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
        path.add(me.getCenter());

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context,
                "" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude(),
                "" + latLng.latitude + "," + latLng.longitude);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Toast.makeText(this, getString(R.string.cant_find_route), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me.getCenter(), DEFAULT_ZOOM));
    }
}