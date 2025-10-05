package com.example.actividad_clase24_9;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.actividad_clase24_9.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final int LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar LocationRequest para actualizaciones en tiempo real
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);        // cada 5 segundos
        locationRequest.setFastestInterval(2000); // mínimo intervalo
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // LocationCallback para actualizar el marcador
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    showLocationOnMap(location);
                }
            }
        };

        // Botón para iniciar seguimiento
        Button btnUbicacion = findViewById(R.id.btnUbicacion);
        btnUbicacion.setOnClickListener(v -> startLocationUpdates());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Verificar permisos al iniciar
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    // Iniciar actualizaciones periódicas de ubicación
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        Toast.makeText(this, "Actualización de ubicación iniciada", Toast.LENGTH_SHORT).show();
    }

    private void showLocationOnMap(Location location) {
        LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                        .position(miUbicacion)
                        .title("Estoy aquí")
                        .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude()))
                .showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16f));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Detener actualizaciones para ahorrar batería
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
