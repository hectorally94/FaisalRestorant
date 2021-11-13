package com.prospect.faisalrestorant.UserActitities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prospect.faisalrestorant.Adapters.AdapterFoodOrder;
import com.prospect.faisalrestorant.Classes.FoodOrder;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.RecyclerItemTouchHelper.RecyclerItemTouchHelperOrder;

import java.util.ArrayList;
import java.util.List;

public class OderDelete extends CheckoutActivity implements  RecyclerItemTouchHelperOrder.RecyclerItemTouchHelperListener {
     List<FoodOrder> listData;
     RecyclerView rv;
     AdapterFoodOrder adapter;
    ProgressDialog progressDialog ;
    private static final String TAG ="" ;
    String Database_Path = "FoodOrder";
    //
    public FoodOrder deletedItem ;
    public int deletedIndex;
    String Deletekey ,pass;
    Long price;
    Snackbar snackbar;
    String Striname;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        relativeLayout = findViewById(R.id.RelativeLayout_layout);

        setTitle("Print Oder Receipt");
        //// loard data
        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(OderDelete.this);
        progressDialog.setTitle("Oder loading...");
        // Showing progressDialog.
        progressDialog.show();
        rv=(RecyclerView)findViewById(R.id.checkout_list);
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
                        FoodOrder l=npsnapshot.getValue(FoodOrder.class);
                        listData.add(l);
                    }
                    adapter=new AdapterFoodOrder(getApplicationContext(), listData);
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
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperOrder(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv);


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdapterFoodOrder.ViewHolder) {
            // get the removed item name to display it in snack bar
            Striname = listData.get(viewHolder.getAdapterPosition()).getFoodname();
            Deletekey = listData.get(viewHolder.getAdapterPosition()).getKey();
            price = listData.get(viewHolder.getAdapterPosition()).getPrice();
            // Toast.makeText(getApplicationContext(), ""+Deletekey, Toast.LENGTH_LONG).show();
            // backup of removed item for undo purpose
            deletedItem = listData.get(viewHolder.getAdapterPosition());
            deletedIndex = viewHolder.getAdapterPosition();
            // showing snack bar with Undo option
            adapter.removeItem(viewHolder.getAdapterPosition());
            snackbar = Snackbar
                    .make(relativeLayout, " Are you sure to Kill Oder:"+Striname+"?" , Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.parseColor("#DDA60B"));
            snackbar.show();
            //  snackbar.dismiss();
            adapter.restoreItem(deletedItem, deletedIndex);
        }
        if(snackbar.isShown()){
            DeleteOrder();
        }else{
            Toast.makeText(OderDelete.this,"  To Delete, Please  click to  Delete  ",Toast.LENGTH_SHORT).show();
            DeleteOrder();
        }

    }
    public void DeleteOrder() {
        snackbar.setAction("Delete Oder", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                Delete();
            }
        });
    }

    private void Delete() {
        final DatabaseReference deleteOrder= FirebaseDatabase.getInstance().getReference();

        deleteOrder.child("FoodOrder").child(Deletekey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OderDelete.this," Oder "+Striname+" deleted ",Toast.LENGTH_SHORT).show();

                // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
            }
        });
        deleteOrder.child("FoodReceipt").child(Deletekey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OderDelete.this," Oder "+Striname+" deleted ",Toast.LENGTH_SHORT).show();

                // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
