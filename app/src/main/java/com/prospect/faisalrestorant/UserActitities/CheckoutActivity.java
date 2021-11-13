package com.prospect.faisalrestorant.UserActitities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import com.prospect.faisalrestorant.Activities.AddUser;
import com.prospect.faisalrestorant.Activities.FoodDetails;
import com.prospect.faisalrestorant.Adapters.AdapterFoodOrder;
import com.prospect.faisalrestorant.Bluetooth.DeviceList;
import com.prospect.faisalrestorant.Bluetooth.UnicodeFormatter;
import com.prospect.faisalrestorant.Classes.Food;
import com.prospect.faisalrestorant.Classes.FoodOrder;
import com.prospect.faisalrestorant.Classes.User;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.RecyclerItemTouchHelper.RecyclerItemTouchHelper;
import com.prospect.faisalrestorant.RecyclerItemTouchHelper.RecyclerItemTouchHelperOrder;
import com.prospect.faisalrestorant.helpers.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity implements Runnable{
    private List<FoodOrder> listData;
    private RecyclerView rv;
    private AdapterFoodOrder adapter;
    ProgressDialog progressDialog ;
    private static final String TAG ="" ;
    String Database_Path = "FoodOrder";
    final Long[] totalAmount = {0l};

    //
public FoodOrder deletedItem ;
    public int deletedIndex;
    String Deletekey ,pass;
    Long price;
    Snackbar snackbar;
    String Striname;
    RelativeLayout relativeLayout;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    public UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    public ProgressDialog mBluetoothConnectProgressDialog;
    public BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        relativeLayout = findViewById(R.id.RelativeLayout_layout);

        setTitle("Print Oder Receipt");
        Button addToCartButton = (Button)findViewById(R.id.checkout);

        Button addBackButton = (Button)findViewById(R.id.idback);
        addBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gooBack( );
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               goo();
            }
        });

        //// loard data
        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(CheckoutActivity.this);
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
        Q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    listData.clear();
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        FoodOrder l=npsnapshot.getValue(FoodOrder.class);
                        listData.add(l);
                      //  totalAmount[0] += npsnapshot.child("totalPrice").getValue(Long.class);

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


    }

    private void gooBack() {


    }

    /////////printing code project

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkke ", e);
        }
    }
    @Override
    public void onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvExe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }
    @SuppressLint("WrongConstant")
    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(CheckoutActivity.this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(CheckoutActivity.this, "Message", 2000).show();
                }
                break;
        }
    }
    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }
    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mHandler.sendEmptyMessage(0);
            Log.d(TAG, "yesyeys yesy eys yes yes yes uyes yes yesy yes yes yes yes yes yes yesy");
            mBluetoothSocket.connect();
        } catch(IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket shida",eConnectException);
            Log.d(TAG, "  nno nno nno nno nnonnonno nno nno nnonno nnonno  nno nno nno nno nno");
            closeSocket(mBluetoothSocket);
            return;
        }

    }

    private void closeSocket(BluetoothSocket nOpenSocket) {

        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        }catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressLint({"WrongConstant", "HandlerLeak"})
        @Override
        public void handleMessage(Message msg) {

            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(CheckoutActivity.this, "DeviceConnected", 5000).show();

            Thread t = new Thread() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @SuppressLint("HandlerLeak")
                public void run() {
                    try {
                        //mBluetoothAdapter.enable();
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        OutputStream osd = mBluetoothSocket.getOutputStream();

                        String name = "null";
                        Long price;
                        Long[] priceToal = {0l};
                        String Quantity="";
                        String BILL = "";
                        String description=" ";

                        Calendar c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH);
                        int year = c.get(Calendar.YEAR);
                        String date = day + "-" + (month+1) + "-" + year;
                       SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        description = "\nIstanbul Restaurant Invoice\n" + " "
                                + date+"\n"
                                + "Monday"+"\n"
                                + currentDateandTime+"\n"
                                + "Contact Us"+"\n";
                        description = description
                                + "-----------------------------\n";
                        description = description
                                 +"FOOD'S NAME  ***PRICE ********" ;

                        description = description + "\n\n";
                        osd.write(description.getBytes());
                        for(int i=0;i<listData.size();i++){
                         //   System.out.println(listData.get(i));
                            // FoodOrder ld=listData.get(i);
                           // BILL = BILL + "\n\n";
                            name=listData.get(i).getFoodname();
                            price=listData.get(i).getTotalPrice();
                            priceToal[0] +=listData.get(i).getTotalPrice();
                            Quantity=listData.get(i).getQuantity();
                            BILL = " " ;
                            BILL = BILL + Quantity + " " +name+ "===>>"  + price + "\n";
                            BILL = BILL + "\n";
                            os.write(BILL.getBytes());
                        }
                        description = "  " ;
                        description = description +"TOTAL " +priceToal[0]+" FBU" ;
                        description = description + "\n\n\n";
                        osd.write(description.getBytes());
                        //This is printer specific code you can comment ==== > Start
                        // Setting height
                        int gs = 29;
                        os.write(intToByteArray(gs));
                        int h = 104;
                        os.write(intToByteArray(h));
                        int n = 162;
                        os.write(intToByteArray(n));
                        // Setting Width
                        int gs_width = 29;
                        os.write(intToByteArray(gs_width));
                        int w = 119;
                        os.write(intToByteArray(w));
                        int n_width = 2;
                        os.write(intToByteArray(n_width));
                        os.close();
                        mBluetoothSocket.close();
                    } catch (Exception e) {
                        Log.e("Main", "Exe ", e);
                    }
                }
            };
            t.start();

            SharedPreferences sh = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor eeditor = sh.edit();
            eeditor.clear();
            eeditor.commit();
            CleenRecyclerview();
        }
    };

    private void CleenRecyclerview() {
        final DatabaseReference deleteOrder= FirebaseDatabase.getInstance().getReference();

        deleteOrder.child("FoodOrder").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CheckoutActivity.this," Thank you ",Toast.LENGTH_SHORT).show();
                listData.clear();
                // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }
    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    @SuppressLint("WrongConstant")
    void goo(){
       // SharedPreferences sh = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
       // SharedPreferences.Editor eeditor = sh.edit();
        //eeditor.clear();
       // eeditor.commit();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(CheckoutActivity.this, "Message1", 2000).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(CheckoutActivity.this, DeviceList.class);
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
            }
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
                Toast.makeText(CheckoutActivity.this," Oder "+Striname+" deleted ",Toast.LENGTH_SHORT).show();

                // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
            }
        });
         deleteOrder.child("FoodReceipt").child(Deletekey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CheckoutActivity.this," Oder "+Striname+" deleted ",Toast.LENGTH_SHORT).show();

                // Toast.makeText(AddUser.this," User deleted dataase ",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
