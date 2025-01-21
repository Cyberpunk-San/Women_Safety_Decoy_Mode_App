package com.sansriti.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;

public class LocationShareActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "LocationShareActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private TextView locationText;
    private Button sendLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        locationText = findViewById(R.id.location_text);
        locationText.setText("Initializing location services...");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Create location request and callback
        createLocationRequest();
        createLocationCallback();

        // Check permissions and then get the location
        if (checkPermissions()) {
            sendLocationAsMessage(); // Directly call the method to send location
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        updateLocationOnMap(location);
                    }
                }
            }
        };
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            Toast.makeText(this, "Permission required for location", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationOnMap(Location location) {
        if (mMap != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear(); // Clear previous markers
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("I am here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            locationText.setText(String.format("Latitude: %.6f\nLongitude: %.6f", location.getLatitude(), location.getLongitude()));
        }
    }
    private void sendLocationAsMessage() {
        if (checkPermissions()) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            updateLocationOnMap(location);

                            // Generate Google Maps link
                            String mapsUrl = String.format("https://www.google.com/maps?q=%.6f,%.6f",
                                    location.getLatitude(), location.getLongitude());

                            // Format the message
                            String message = String.format("I am here! Check my location: %s", mapsUrl);

                            // Send location to colleagues
                            sendLocationToColleagues(message);
                        } else {
                            Toast.makeText(this, "Unable to retrieve location.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        Toast.makeText(this, "Failed to fetch location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            requestPermissions();
        }
    }

    private void sendLocationToColleagues(String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("colleagues");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot colleagueSnapshot : dataSnapshot.getChildren()) {
                        // Fetch the colleague's phone number directly from Firebase
                        String contact = colleagueSnapshot.child("contact").getValue(String.class);

                        // Send the location message to this contact if valid
                        if (contact != null && !contact.isEmpty()) {
                            sendSMS(contact, message);
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Location sent to colleagues.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No colleagues found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSMS(String phoneNumber, String message) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("SMS", "Message sent to: " + phoneNumber);
        } else {
            Toast.makeText(this, "SMS permission not granted.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 101);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permissions are required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendLocationAsMessage();
            } else {
                Toast.makeText(this, "SMS permission denied. Unable to send location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
