package com.prospect.faisalrestorant.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.Classes.Role;
import com.prospect.faisalrestorant.R;

import java.util.ArrayList;
import java.util.List;

public class AddRole extends AppCompatActivity {
    // Root Database Name for Firebase Database.
    String Database_Path = "Role"; // or you can use Role/Admin to create a node of admin
    DatabaseReference databaseReference;
    // Creating button.
    Button save;
    EditText editrole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        //Assign ID'S to button.
        save = (Button)findViewById(R.id.btnResetPassword);

        // Assign ID's to EditText.
        editrole = (EditText)findViewById(R.id.resetEmail); // Assign ID's to EditText.

        // Adding click listener to Upload image button.

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calling method to upload selected image on Firebase storage.
                SaveRole();
            }
        });
    }
    private void SaveRole() {
        // Checking whether FilePathUri Is empty or not.
        if (editrole.getText()!= null) {
            // Getting image name from EditText and store into string variable.
            String name = editrole.getText().toString().trim();
            String key= databaseReference.push().getKey();
            // Hiding the progressDialog after done uploading.
            Role role = new Role(
                    key,
                    name
            );
            databaseReference.child(key).setValue(role);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Is better to use a List, because you don't know the size
                    // of the iterator returned by dataSnapshot.getChildren() to
                    // initialize the array
                    final List<String> areas = new ArrayList<String>();

                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String areaName = areaSnapshot.child("role").getValue(String.class);
                        areas.add(areaName);
                    }

                    Spinner areaSpinner = (Spinner) findViewById(R.id.spinner);
                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(AddRole.this, android.R.layout.simple_spinner_item, areas);
                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    areaSpinner.setAdapter(areasAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(AddRole.this, "Please fill Name", Toast.LENGTH_LONG).show();
        }
        // If something goes wrong .
    }

}


