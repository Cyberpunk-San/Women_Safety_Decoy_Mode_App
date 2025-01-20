package com.sansriti.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.analytics.FirebaseAnalytics;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.widget.ImageButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WomenSafetyActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference colleaguesRef;
    private static final int REQUEST_CODE_PERMISSIONS = 1003;
    private DatabaseReference mDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int PERMISSION_REQUEST_SEND_SMS = 1091;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1091;//

    // Define a unique request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women_safety);
        database = FirebaseDatabase.getInstance();
        colleaguesRef = database.getReference("colleagues");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_PERMISSIONS);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Initialize components
        ImageButton settingsButton = findViewById(R.id.settingsButton);
        Button sosButton = findViewById(R.id.sosButton);
        Button locationShareButton = findViewById(R.id.locationShareButton);
        Button messageButton = findViewById(R.id.messageButton);
        Button dialButton = findViewById(R.id.dialButton);

        FloatingActionButton videoButton = findViewById(R.id.videoButton);

        // Set an OnClickListener on the FAB
        videoButton.setOnClickListener(v -> {
            // Open the VideoListActivity
            Intent intent = new Intent(WomenSafetyActivity.this, VideoListActivity.class);
            startActivity(intent);
        });

        // Set click listeners
        settingsButton.setOnClickListener(view -> {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Colleague Options")
                    .setItems(new CharSequence[]{"Add Colleague", "View Colleague", "Delete Colleague"}, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                showAddColleagueDialog();
                                break;
                            case 1:
                                viewColleagues();
                                break;
                            case 2:
                                showDeleteColleagueDialog();
                                break;
                        }
                    });
            builder.create().show();
        });

        sosButton.setOnClickListener(view -> {
            Toast.makeText(this, "SOS Button clicked", Toast.LENGTH_SHORT).show();
            sendSOSMessage();
            dialEmergencyNumber();
            sendMessage();


        });
        locationShareButton.setOnClickListener(view -> {
            Toast.makeText(this, "Share Location clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WomenSafetyActivity.this, LocationShareActivity.class);
            startActivity(intent);
        });

        messageButton.setOnClickListener(view -> {
            Toast.makeText(this, "Send Message clicked", Toast.LENGTH_SHORT).show();
            sendSOSMessage();
            sendMessage();
        });

        dialButton.setOnClickListener(view -> {
            Toast.makeText(this, "Quick Dial clicked", Toast.LENGTH_SHORT).show();
            dialEmergencyNumber();
        });

    }

    private void showAddColleagueDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Colleague");

        // Set up the input fields for Name and Contact Number
        final EditText nameInput = new EditText(this);
        nameInput.setHint("Enter Name");
        final EditText numberInput = new EditText(this);
        numberInput.setHint("Enter Contact Number");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(nameInput);
        layout.addView(numberInput);

        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString();
            String number = numberInput.getText().toString();
            addColleagueToFirebase(name, number);
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void addColleagueToFirebase(String name, String contact) {
        String colleagueId = colleaguesRef.push().getKey(); // Generate a unique ID

        // Create a map for the colleague's data
        Map<String, String> colleague = new HashMap<>();
        colleague.put("name", name);
        colleague.put("contact", contact);

        // Add the colleague to the Firebase database under the unique ID
        colleaguesRef.child(colleagueId).setValue(colleague)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WomenSafetyActivity.this, "Colleague added successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle errors if there are any
                        if (task.getException() != null) {
                            Toast.makeText(WomenSafetyActivity.this, "Failed to add colleague: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void viewColleagues() {
        colleaguesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> colleagues = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String colleagueName = snapshot.child("name").getValue(String.class);
                    String colleagueContact = snapshot.child("contact").getValue(String.class);
                    colleagues.add(colleagueName + " - " + colleagueContact); // Add colleague to the list
                }
                showColleagueListDialog(colleagues); // Show the list in a dialog
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WomenSafetyActivity.this, "Failed to load colleagues: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showColleagueListDialog(List<String> colleagues) {
        // Convert the list of colleague details to an array
        CharSequence[] colleagueArray = colleagues.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Colleague List")
                .setItems(colleagueArray, (dialog, which) -> {
                    // Handle item clicks if needed
                })
                .setPositiveButton("Close", null)
                .create().show();
    }
    private void showDeleteColleagueDialog() {
        colleaguesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Prepare the list of colleagues to display
                List<String> colleagueNames = new ArrayList<>();
                final Map<String, String> colleagueIdMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String colleagueId = snapshot.getKey(); // Get the colleague's ID
                    String colleagueName = snapshot.child("name").getValue(String.class);

                    if (colleagueName != null) {
                        colleagueNames.add(colleagueName);
                        colleagueIdMap.put(colleagueName, colleagueId); // Map name to ID
                    }
                }

                // Show the dialog with colleague names
                AlertDialog.Builder builder = new AlertDialog.Builder(WomenSafetyActivity.this);
                builder.setTitle("Select a Colleague to Delete")
                        .setItems(colleagueNames.toArray(new CharSequence[0]), (dialog, which) -> {
                            // Get the selected colleague's name
                            String selectedColleague = colleagueNames.get(which);

                            // Get the colleague ID from the map
                            String colleagueIdToDelete = colleagueIdMap.get(selectedColleague);

                            // Call removeColleague to delete the selected colleague
                            removeColleague(colleagueIdToDelete);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WomenSafetyActivity.this, "Failed to load colleagues: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeColleague(String colleagueId) {
        colleaguesRef.child(colleagueId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WomenSafetyActivity.this, "Colleague removed successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WomenSafetyActivity.this, "Failed to remove colleague: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sendMessage() {
        colleaguesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean messageSent = false;

                // Iterate through the colleagues and send SMS to each
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String phoneNumber = snapshot.child("contact").getValue(String.class);

                    // Check if phone number exists for the colleague
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        sendSMS(phoneNumber);
                        messageSent = true;
                    }
                }

                if (messageSent) {
                    Toast.makeText(WomenSafetyActivity.this, "Emergency messages sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WomenSafetyActivity.this, "No colleagues with phone numbers found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WomenSafetyActivity.this, "Failed to fetch colleague data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendSMS(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "Emergency: Please somebody help me!", null, null);
    }

    private void sendSOSMessage() {
        String phoneNumber = "1091"; // Emergency number
        String message = "Emergency Alert: Please help! I am in danger.";

        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Send the SMS message
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this, "SOS Message Sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SOS message", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Request SMS permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
    }

    private void dialEmergencyNumber() {
        String emergencyNumber = "1091"; // Emergency number
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            try {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + emergencyNumber));
                startActivity(intent);
                Toast.makeText(this, "Dialing Emergency Number", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to dial emergency number", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);
        }
    }
    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSOSMessage();
            } else {
                Toast.makeText(this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
        } if (requestCode == PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialEmergencyNumber();
            } else {
                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }
}