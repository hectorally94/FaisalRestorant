package com.prospect.faisalrestorant.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.UserActitities.UserPanel;

public class Login extends AppCompatActivity {
    //declare our elements
    TextInputEditText email, pass;
    Button btnLogin;
    //declare your Firebase Auth
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //invictus
        //initialize the firebase auth
        auth = FirebaseAuth.getInstance();
        //reference  your views
        email = findViewById(R.id.loginEmail);
        pass = findViewById(R.id.loginPass);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        //onclicklistener for login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
    // method to login to firebase
    private void login() {
        //store the users input
        String login_email = email.getText().toString().trim();
        String login_pass = pass.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        //check the entries before login in the user
        if(TextUtils.isEmpty(login_email))
        {
            email.setError("Email Required");
            email.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(login_pass))
        {
            pass.setError("Password Required");
            pass.requestFocus();
            return;
        }


        if(pass.length()<8)
        {
            pass.setError("min 8 characters required");
            pass.requestFocus();
            return;
        }// Check if email id is valid or not
        if (email.getText().toString().trim().matches(emailPattern)) {
            ///Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
        } else {
            email.setError("Wrong Email Address");
            email.requestFocus();
        }
        if (TextUtils.isEmpty(login_email) && TextUtils.isEmpty(login_pass))  {
            Toast.makeText(this, "All the fields cannot be empty", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            pass.requestFocus();
        }
        else {
            run(login_email, login_pass);
        }


    }

    private void run(String login_email, String login_pass) {
        //show the progress bar
        progressBar.setVisibility(View.VISIBLE);
        //the method to signin a registered user on firebase project
        auth.signInWithEmailAndPassword(login_email, login_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (!task.isSuccessful()) {
                    // replace toast with the sweet alert
                    Toast.makeText(Login.this, "Sorry check the credentials", Toast.LENGTH_SHORT).show();
                } else {
                    updateUi();
                }
            }
        });
    }

    private void updateUi() {
        String emailVerificatio = email.getText().toString().trim();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference();
        Query Q=nm.child("User").orderByChild("email").equalTo(emailVerificatio);
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String userType = ds.child("role").getValue(String.class);
                        if(userType.equals("Admin")){
                            Intent intentResident = new Intent(Login.this, AdminPanel.class);
                            intentResident.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentResident);
                            finish();
                        }else if(userType.equals("User")){
                            Intent intentMain = new Intent(Login.this, UserPanel.class);
                            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentMain);
                            finish();
                        }
                        else{
                            Toast.makeText(Login.this, "Failed Login. Please Try Again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }else
                {
                    Toast.makeText(Login.this, " Email do not exit", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
