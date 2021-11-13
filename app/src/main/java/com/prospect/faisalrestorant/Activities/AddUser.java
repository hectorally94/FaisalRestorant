package com.prospect.faisalrestorant.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.Adapters.AdapterAddUser;
import com.prospect.faisalrestorant.Classes.User;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.RecyclerItemTouchHelper.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class AddUser extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG ="" ;
    Button save;
    EditText emailUser,Usermail;
    EditText passwordUser;
    TextView getvaluetext;
    Spinner areaSpinnerC;
    String Database_Path = "User";
    String Database_PathroleC = "Role";

    /////
    public User deletedItem ;
    public int deletedIndex;
    String Deletekey ,pass;
    String deteleEmail;
    Snackbar snackbar;
    String Striname;
    //////
    private List<User> listData;
    private RecyclerView rv;
    private RelativeLayout relativeLayout;
    private AdapterAddUser adapter;
    ProgressDialog progressDialog ;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReferenceUser,databaseReferenceRoleC;
    private FirebaseAuth auth;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.syncState();
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
        drawerToggle.setHomeAsUpIndicator(R.drawable.menu);
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        // Lookup navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
// Inflate the header view at runtime
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        /////////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseReferenceRoleC = FirebaseDatabase.getInstance().getReference(Database_PathroleC);
        //trying to unitialise the email and password
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Assign ID'S to button.
        save = (Button)findViewById(R.id.btnResetPassword);
        // Assign ID's to EditText.
        relativeLayout = findViewById(R.id.RelativeLayout_layout);

        emailUser = (EditText)findViewById(R.id.resetEmail);
        Usermail = (EditText)findViewById(R.id.userloginEmail);
        emailUser = (EditText)findViewById(R.id.loginEmail);
        passwordUser = (EditText)findViewById(R.id.loginPass);
        getvaluetext=findViewById(R.id.idSpinnervalue);
        areaSpinnerC = (Spinner) findViewById(R.id.spinner);

        // Assign ID's to EditText.
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getfullmmail=Usermail.getText().toString();
                String getemail=emailUser.getText().toString();
                String password=passwordUser.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if(TextUtils.isEmpty(getfullmmail))
                {
                    Usermail.setError("fill info");
                    Usermail.requestFocus();
                    return ;
                }
                if(TextUtils.isEmpty(getemail))
                {
                    emailUser.setError("fill info");
                    emailUser.requestFocus();
                    return ;
                }
                if(TextUtils.isEmpty(password))
                {
                    passwordUser.setError("fill info");
                    passwordUser.requestFocus();
                    return ;
                }

                if(passwordUser.length()<8)
                {
                    passwordUser.setError("min 8 characters required");
                    passwordUser.requestFocus();
                    return;
                }// Check if email id is valid or not
                if (emailUser.getText().toString().trim().matches(emailPattern)) {
                    ///Toast.makeText(getApplicationContext(),"valid email address",Toast.LENGTH_SHORT).show();
                } else {
                    emailUser.setError("Wrong Email Address");
                    emailUser.requestFocus();
                }
                if (TextUtils.isEmpty(getfullmmail) && TextUtils.isEmpty(password)) {
                    Toast.makeText(AddUser.this, "All the fields cannot be empty", Toast.LENGTH_SHORT).show();
                    emailUser.requestFocus();
                    passwordUser.requestFocus();
                }
                else {
                    SaveUser();
                }

            }
        });

        databaseReferenceRoleC.addValueEventListener(new ValueEventListener() {
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
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(AddUser.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinnerC.setAdapter(areasAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        areaSpinnerC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String prov = areaSpinnerC.getSelectedItem().toString();
                getvaluetext.setText(prov);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //// loard data
        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(AddUser.this);
        progressDialog.setTitle("Loading...");
        ///progressDialog.setCancelable(false);

        // Showing progressDialog.
        progressDialog.show();
        rv=(RecyclerView)findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ///ItemTouchHelper
        listData=new ArrayList<>();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference();
        Query Q=nm.child("User");
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listData.clear();
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        User l=npsnapshot.getValue(User.class);
                        listData.add(l);
                    }
                    adapter=new AdapterAddUser(getApplicationContext(), listData);
                    rv.setAdapter(adapter);
                    // Hiding the progressDialog after done uploading.
                    progressDialog.dismiss();
                    // Showing toast message after done uploading.
                    // Toast.makeText(getApplicationContext(), "Welcome  ", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "issue with firebase database", Toast.LENGTH_LONG).show();
            }
        });

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);



    }






    private ActionBarDrawerToggle setupDrawerToggle() {

        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it

        // and will not render the hamburger icon without it.

        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);

    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override

                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.dashbord:
                Intent intentdash = new Intent(getApplicationContext(), AdminPanel.class);
                startActivity(intentdash);
                finish();
                break;
            case R.id.addproeduct:
                Intent intent1 = new Intent(getApplicationContext(), AddProduct.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.report:
                Intent intent2 = new Intent(getApplicationContext(), Report.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.activity_login:
                Intent intent3 = new Intent(getApplicationContext(), Login.class);
                startActivity(intent3);
                finish();
                break;
            default:
                Toast.makeText(AddUser.this,"No Activity selected",Toast.LENGTH_LONG).show();
        }
        // Highlight the selected item has been done by NavigationView

        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();

    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;

        }
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // `onPostCreate` called when activity start-up is complete after `onStart()`

    // NOTE 1: Make sure to override the method with only a single `Bundle` argument

    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.

    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.

    @Override

    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.

        drawerToggle.syncState();

    }

    @Override

    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles

        drawerToggle.onConfigurationChanged(newConfig);

    }
/////////////
private void SaveUser() {
    progressDialog = new ProgressDialog(AddUser.this);
    progressDialog.setMax(100);
    progressDialog.setMessage("Saving....");
    progressDialog.setTitle(" User"+" "+Usermail.getText().toString());
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setCancelable(false);
    progressDialog.show();
    //getting email and password from edit texts emailUser
    String Athemail = emailUser.getText().toString().trim();
    String Athpassword =  passwordUser.getText().toString().trim();

    firebaseAuth.createUserWithEmailAndPassword(Athemail, Athpassword)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //checking if success
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        //display some message here
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String RegisteredUserID = currentUser.getUid();

                        Toast.makeText(AddUser.this,"firebaseAuth registered",Toast.LENGTH_LONG).show();
                        updateUI(RegisteredUserID);
                    }else{
                        //display some message here
                        Toast.makeText(AddUser.this,"firebaseAuth Error",Toast.LENGTH_LONG).show();
                    }

                }
            });

}

    void updateUI(String id){
        // Getting image name from EditText and store into string variable.
        String email = emailUser.getText().toString().trim();
        String updUser= Usermail.getText().toString();
        String password = passwordUser.getText().toString().trim();
        String role = getvaluetext.getText().toString();
        String key=id;
        // Hiding the progressDialog after done uploading.
        User user = new User(
                key,
                updUser,
                email,
                password,
                role
        );
        // Getting image upload ID.
        //String ImageAdminId = databaseReferenceUser.push().getKey();
        // Adding image upload id s child element into databaseReference.
        databaseReferenceUser.child(id).setValue(user);

    }

    void deletecuretUser(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential("momoli@gmail.com", "OSOldqFNV8P5jkt13DdTZs8cYj42");

        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ///  Toast.makeText(AddUser.this," deleted ",Toast.LENGTH_SHORT).show();
                            deleteUserInfo();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdapterAddUser.ViewHolder) {
            // get the removed item name to display it in snack bar
            Striname = listData.get(viewHolder.getAdapterPosition()).getFullname();
            Deletekey = listData.get(viewHolder.getAdapterPosition()).getKey();
            deteleEmail = listData.get(viewHolder.getAdapterPosition()).getEmail();
            pass = listData.get(viewHolder.getAdapterPosition()).getPassword();
            // Toast.makeText(getApplicationContext(), ""+Deletekey, Toast.LENGTH_LONG).show();
            // backup of removed item for undo purpose
            deletedItem = listData.get(viewHolder.getAdapterPosition());
            deletedIndex = viewHolder.getAdapterPosition();
            // showing snack bar with Undo option
            adapter.removeItem(viewHolder.getAdapterPosition());
            snackbar = Snackbar
                    .make(relativeLayout, " Are you sure to Delete User:"+Striname+"?" , Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.parseColor("#DDA60B"));
            snackbar.show();
            //  snackbar.dismiss();
            adapter.restoreItem(deletedItem, deletedIndex);
        }
        if(snackbar.isShown()){
            DeleteUser();
        }else{
            Toast.makeText(AddUser.this,"  To Delete, Please  click to  Delete User ",Toast.LENGTH_SHORT).show();
            DeleteUser();
        }

    }
    public void DeleteUser() {
        snackbar.setAction("Delete User", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                Logout();
            }
        });
    }

    public void deleteUserInfo()  {
        // FirebaseAuth.getInstance().deleteUser(Deletekey);
        // remove the item from recycler view
        databaseReferenceUser.child(Deletekey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddUser.this," User "+Striname+" deleted ",Toast.LENGTH_SHORT).show();

                // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Logout() {
        //show the progress bar
        progressDialog = new ProgressDialog(AddUser.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Deleting....");
        progressDialog.setTitle(" User"+ " "+Striname);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();        //the method to signin a registered user on firebase project
        auth.signInWithEmailAndPassword(deteleEmail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    // replace toast with the sweet alert
                    Toast.makeText(AddUser.this, "Sorry check the credentials", Toast.LENGTH_SHORT).show();
                } else {
                    deletecuretUser();
                }
            }
        });
    }


}
