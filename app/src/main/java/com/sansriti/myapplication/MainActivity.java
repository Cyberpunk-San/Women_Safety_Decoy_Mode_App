package com.sansriti.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
import android.content.Intent;
import java.util.ArrayList;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private TextView calculatorDisplay;
    private final String secretCode = "505";
    private String enteredCode = "";
    private StringBuilder currentExpression = new StringBuilder();
    private boolean safetyModeActive = false;
    private static final int PERMISSION_REQUEST_SEND_SMS = 1091;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 1091;//
    private static final int PERMISSION_REQUEST_LOCATION= 1003;//
    private static final long LONG_PRESS_DURATION = 1000; // 1 second for long press
    private Handler longPressHandler = new Handler();
    private boolean isLongPress = false;
    private SpeechRecognizer speechRecognizer;
    private TextView speechOutput;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculatorDisplay = findViewById(R.id.calculatorDisplay);
        speechOutput = findViewById(R.id.speechOutput);

        // Request microphone permission
        requestAudioPermission();

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Set up recognition listener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(MainActivity.this, "...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                speechOutput.setText("...");
            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(MainActivity.this, "...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    speechOutput.setText(recognizedText);

                    // Check for the word "sun"
                    if (recognizedText.toLowerCase().contains("yo")) {
                        Toast.makeText(MainActivity.this, "Activated Safety Mode!", Toast.LENGTH_SHORT).show();
                        //activateSafetyMode();
                    }
                } else {
                    speechOutput.setText("No speech recognized.");
                }
            }
            @Override
            public void onError(int error) {
                switch (error) {
                    case SpeechRecognizer.ERROR_NETWORK:
                        Toast.makeText(MainActivity.this, "Safety mode being activated.", Toast.LENGTH_SHORT).show();
                        break;
                    // Handle other error codes
                    default:
                        Toast.makeText(MainActivity.this, "Safety Mode: " + error, Toast.LENGTH_SHORT).show();

                        break;
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partialMatches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (partialMatches != null && !partialMatches.isEmpty()) {
                    String partialText = partialMatches.get(0);
                    speechOutput.setText(partialText);

                    // Check for the word "sun" in partial results
                    if (partialText.toLowerCase().contains("yo")) {
                        Toast.makeText(MainActivity.this, "Activated Safety Mode!", Toast.LENGTH_SHORT).show();
                        //activateSafetyMode();
                    }
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {}


        });

        // Start listening for speech when the app opens
        startListening();

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);
        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);
        Button button0 = findViewById(R.id.button0);
        Button buttonClear = findViewById(R.id.buttonClear);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        Button buttonSubtract = findViewById(R.id.buttonSub);
        Button buttonMultiply = findViewById(R.id.buttonMul);
        Button buttonDivide = findViewById(R.id.buttonDiv);
        Button buttonEquals = findViewById(R.id.buttonEquals);
        Button buttonBackspace = findViewById(R.id.buttonBackspace);

        // Setting up button click listeners
        button1.setOnClickListener(v -> onDigitClick("1"));
        button2.setOnClickListener(v -> onDigitClick("2"));
        button3.setOnClickListener(v -> onDigitClick("3"));
        button4.setOnClickListener(v -> onDigitClick("4"));
        button5.setOnClickListener(v -> onDigitClick("5"));
        button6.setOnClickListener(v -> onDigitClick("6"));
        button7.setOnClickListener(v -> onDigitClick("7"));
        button8.setOnClickListener(v -> onDigitClick("8"));
        button9.setOnClickListener(v -> onDigitClick("9"));
        button0.setOnClickListener(v -> onDigitClick("0"));

        buttonAdd.setOnClickListener(v -> onOperatorClick("+"));
        buttonSubtract.setOnClickListener(v -> onOperatorClick("-"));
        buttonMultiply.setOnClickListener(v -> onOperatorClick("*"));
        buttonDivide.setOnClickListener(v -> onOperatorClick("/"));
        buttonClear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isLongPress = false;
                        longPressHandler.postDelayed(longPressRunnable, LONG_PRESS_DURATION);
                        break;
                    case MotionEvent.ACTION_UP:
                        longPressHandler.removeCallbacks(longPressRunnable);
                        if (!isLongPress) {
                            // Handle as a short press
                            onClear();
                        }
                        break;
                }
                return true;
            }
        });
        buttonEquals.setOnClickListener(v -> calculateResult());
        buttonBackspace.setOnClickListener(v -> {
            if (!enteredCode.isEmpty()) {
                enteredCode = enteredCode.substring(0, enteredCode.length() - 1);
                calculatorDisplay.setText(enteredCode.isEmpty() ? "0" : enteredCode);
            }
        });

        requestPermissions();
    }

    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            isLongPress = true;
            navigateToSafetyActivity();
        }
    };

    private void navigateToSafetyActivity() {
        // Reset the calculator display
        enteredCode = "";
        calculatorDisplay.setText("0");

        // Navigate to WomenSafetyActivity
        Intent intent = new Intent(MainActivity.this, WomenSafetyActivity.class);
        startActivity(intent);
    }

    private void onDigitClick(String digit) {
        if (safetyModeActive) return;
        enteredCode += digit;
        currentExpression.append(digit);
        calculatorDisplay.setText(currentExpression.toString());

        if (enteredCode.equals(secretCode)) {
            activateSafetyMode();
        }
    }

    private void onOperatorClick(String operator) {
        if (safetyModeActive) return;
        currentExpression.append(" ").append(operator).append(" ");
        calculatorDisplay.setText(currentExpression.toString());
    }

    private void onClear() {
        enteredCode = "";
        currentExpression.setLength(0);
        calculatorDisplay.setText("0");
    }

    private void calculateResult() {
        if (safetyModeActive) return;
        try {
            String[] tokens = currentExpression.toString().split(" ");
            double result = 0;

            if (tokens.length >= 3) {
                double operand1 = Double.parseDouble(tokens[0]);
                double operand2 = Double.parseDouble(tokens[2]);
                String operator = tokens[1];

                switch (operator) {
                    case "+":
                        result = operand1 + operand2;
                        break;
                    case "-":
                        result = operand1 - operand2;
                        break;
                    case "*":
                        result = operand1 * operand2;
                        break;
                    case "/":
                        if (operand2 != 0) {
                            result = operand1 / operand2;
                        } else {
                            Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
                calculatorDisplay.setText(String.valueOf(result));
                currentExpression.setLength(0);
                currentExpression.append(result);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid expression", Toast.LENGTH_SHORT).show();
        }
    }
    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechRecognizer.startListening(intent);
    }
    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop and destroy SpeechRecognizer
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    private void activateSafetyMode() {
        Toast.makeText(this, "Safety Mode Activated", Toast.LENGTH_SHORT).show();
        sendSOSMessage();
        sendMessage();
        dialEmergencyNumber();
        shareLocation();
    }
    private void shareLocation() {
        // Check for location permissions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Get the current location
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    // Create a Google Maps URL with the current latitude and longitude
                    String mapsUrl = String.format("https://www.google.com/maps?q=%.6f,%.6f", location.getLatitude(), location.getLongitude());

                    // Send the location to all contacts
                    sendLocationToAllContacts(mapsUrl);
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(this, e -> {
                Toast.makeText(this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } else {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    private void sendLocationToAllContacts(String locationUrl) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int phoneNumberIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (phoneNumberIndex != -1) {  // Ensure the column index is valid
                    String phoneNumber = cursor.getString(phoneNumberIndex);
                    sendSMS(phoneNumber, locationUrl);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(String phoneNumber, String locationUrl) {
        SmsManager smsManager = SmsManager.getDefault();
        String message = "I am here! Check my location: " + locationUrl;
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }


    private void sendMessage() {

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int phoneNumberIndex = cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER);
                if (phoneNumberIndex != -1) {  // Ensure the column index is valid
                    String phoneNumber = cursor.getString(phoneNumberIndex);
                    sendSMS(phoneNumber);
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "Emergency!Pls help me", null, null);
    }

    private void sendSOSMessage() {
        String phoneNumber = "1091"; // Emergency number
        String message = "Emergency!Pls help me!";

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

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 1);
                break;
            }
        }
    }
}