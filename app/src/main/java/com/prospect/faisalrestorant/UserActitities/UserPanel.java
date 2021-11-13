package com.prospect.faisalrestorant.UserActitities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.Adapters.AdapterFoodUser;
import com.prospect.faisalrestorant.Classes.Food;
import com.prospect.faisalrestorant.Classes.User;
import com.prospect.faisalrestorant.R;

import java.util.ArrayList;
import java.util.List;

public class UserPanel extends AppCompatActivity {
    private List<Food> listData;
    private RecyclerView rv;
    private AdapterFoodUser adapter;
    ProgressDialog progressDialog ;
    private static final String TAG ="" ;
    String Database_Path = "Foods";
    DatabaseReference databaseReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);
        //// loard data
        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(UserPanel.this);
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
                    adapter=new AdapterFoodUser(getApplicationContext(), listData);
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
    }

}