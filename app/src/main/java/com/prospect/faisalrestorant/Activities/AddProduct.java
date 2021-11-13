package com.prospect.faisalrestorant.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.Adapters.AdapterFood;
import com.prospect.faisalrestorant.Classes.Food;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.RecyclerItemTouchHelper.RecyclerItemTouchHelperFood;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProduct extends AppCompatActivity implements RecyclerItemTouchHelperFood.RecyclerItemTouchHelperListener {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    //
    private static final String color ="#FF6200EE";
    public Food deletedItem ;
    public int deletedIndex;
    String Deletekey;
    Long pass;
    Snackbar snackbar;
    String Striname;
    //////
    private List<Food> listData;
    private RecyclerView rv;
    private AdapterFood adapter;
    ProgressDialog progressDialog ;
    private static final String TAG ="" ;
    Button save;
    EditText name;
    EditText price;
    String Database_Path = "Foods";
    RelativeLayout relativeLayout;
    DatabaseReference databaseReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

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
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
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
///////////
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference(Database_Path);
        save = (Button)findViewById(R.id.btnajouter);
        // Assign ID's to EditText.
        name = (EditText)findViewById(R.id.boisson);
        price = (EditText)findViewById(R.id.prix);
        relativeLayout=findViewById(R.id.RelativeLayout_layout);
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                AddData();
            }
        });
//// loard data
        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(AddProduct.this);
        progressDialog.setTitle("Loading...");
        // Showing progressDialog.
        progressDialog.show();
        rv=(RecyclerView)findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ///ItemTouchHelper
        listData=new ArrayList<>();
        final DatabaseReference nm= FirebaseDatabase.getInstance().getReference();
        Query Q=nm.child(Database_Path);
        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listData.clear();
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Food l=npsnapshot.getValue(Food.class);
                        listData.add(l);
                    }
                    adapter=new AdapterFood(getApplicationContext(), listData);
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
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperFood(0, ItemTouchHelper.LEFT, this);
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
            case R.id.adduser:
                Intent intent = new Intent(getApplicationContext(), AddUser.class);
                startActivity(intent);
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
                Toast.makeText(AddProduct.this,"No Activity selected",Toast.LENGTH_LONG).show();
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
    //
    @RequiresApi(api = Build.VERSION_CODES.N)
    void AddData(){
        // Getting image name from EditText and store into string variable.
        String key =databaseReferenceUser.push().getKey();
        String ame = name.getText().toString().trim();
        Long prix = Long.valueOf(price.getText().toString().trim());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        String Date = dateFormat.format(date);
        // Hiding the progressDialog after done uploading.
        Food Data = new Food(
                key,
                ame,
                prix,
                Date
        );
        // Getting image upload ID.
        // String ImageAdminId = databaseReferenceUser.push().getKey();
        // Adding image upload id s child element into databaseReference.
        databaseReferenceUser.child(key).setValue(Data);
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdapterFood.ViewHolder) {
            // get the removed item name to display it in snack bar
            Striname = listData.get(viewHolder.getAdapterPosition()).getFoodname();
            Deletekey = listData.get(viewHolder.getAdapterPosition()).getKey();
            pass = listData.get(viewHolder.getAdapterPosition()).getPrice();
            // Toast.makeText(getApplicationContext(), ""+Deletekey, Toast.LENGTH_LONG).show();
            // backup of removed item for undo purpose
            deletedItem = listData.get(viewHolder.getAdapterPosition());
            deletedIndex = viewHolder.getAdapterPosition();
            // showing snack bar with Undo option
            adapter.removeItem(viewHolder.getAdapterPosition());
            snackbar = Snackbar
                    .make(relativeLayout, " Are you sure to Delete food:"+Striname+"?" , Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.parseColor("#FFFF9800"));
            snackbar.show();
            //  snackbar.dismiss();
            adapter.restoreItem(deletedItem, deletedIndex);
        }
        if(snackbar.isShown()){
            DeleteUser();
        }else{
            Toast.makeText(AddProduct.this,"  To Delete, Please  click   Delete ",Toast.LENGTH_SHORT).show();
            DeleteUser();
        }
    }
    public void DeleteUser() {
        snackbar.setAction("Delete ", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                deleteUserInfo();
            }
        });
    }
    public void deleteUserInfo()  {
        // FirebaseAuth.getInstance().deleteUser(Deletekey);
        // remove the item from recycler view
        databaseReferenceUser.child(Deletekey).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddProduct.this," User "+Striname+" deleted ",Toast.LENGTH_SHORT).show();
                        // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.actionSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }}





