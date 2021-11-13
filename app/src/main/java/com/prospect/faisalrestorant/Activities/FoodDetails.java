package com.prospect.faisalrestorant.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prospect.faisalrestorant.Bluetooth.DeviceList;
import com.prospect.faisalrestorant.Bluetooth.UnicodeFormatter;
import com.prospect.faisalrestorant.Classes.Food;
import com.prospect.faisalrestorant.Classes.FoodOderValid;
import com.prospect.faisalrestorant.Classes.FoodOrder;
import com.prospect.faisalrestorant.R;
import com.prospect.faisalrestorant.UserActitities.CheckoutActivity;
import com.prospect.faisalrestorant.UserActitities.OderDelete;
import com.prospect.faisalrestorant.UserActitities.UserPanel;
import com.prospect.faisalrestorant.helpers.MySharedPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FoodDetails extends AppCompatActivity implements Runnable{
    String foodname,getKey;
    Long pricena;
    TextView textfood,textprice,texttotal,textquantity;
    TextView quantity1 ;
    String getOldQuantity,FoodReceiptkey;
    Long getOldTotal;
    int quantity;
    private int cartProductNumber = 0;
    DatabaseReference databaseReferenceUser,databaseReference;

    ////////////////////////
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    public UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    public ProgressDialog mBluetoothConnectProgressDialog;
    public BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    //////
    private Gson gson;
    AlertDialog.Builder builder;
    private MySharedPreference sharedPreference;
    TextView tot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textfood=findViewById(R.id.TextViewname);
        textprice=findViewById(R.id.TextViewprice);
        textquantity=findViewById(R.id.quantitychange_value);
        texttotal=findViewById(R.id.Texttotal);

        Intent intent = getIntent();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("FoodOrder");
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodReceipt");

        foodname = intent.getStringExtra("getname");
        pricena = intent.getLongExtra("getprice",0);
        String getprix=String.valueOf(pricena);
        getKey = intent.getStringExtra("getKey");
        textfood.setText(foodname);
        textprice.setText(getprix);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
        sharedPreference = new MySharedPreference(FoodDetails.this);

        Button addToCart = (Button)findViewById(R.id.idaddlist);
        AlertDialog.Builder buildezr = new AlertDialog.Builder(this);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String total=tot.getText().toString().trim();
                if (haveNetworkConnection()) {
                    if(TextUtils.isEmpty(total))
                    {
                        tot.setError("Total missing");
                        tot.requestFocus();
                        return ;
                    }else{
                        buildezr.setMessage("Do you want to Add this "+foodname+" to Cart ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @SuppressLint("WrongConstant")
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (haveNetworkConnection()) {


                                            ///////
                                            final DatabaseReference nm= FirebaseDatabase.getInstance().getReference();
                                            Query Q=nm.child("FoodOrder").orderByChild("key").equalTo(getKey);
                                            Q.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot.exists()){

                                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                            getOldQuantity=snapshot.child("quantity").getValue(String.class);
                                                            getOldTotal=snapshot.child("totalPrice").getValue(Long.class);
                                                        }
                                                        Toast.makeText(FoodDetails.this, getOldQuantity, Toast.LENGTH_SHORT).show();
                                                        String f=String.valueOf(getOldTotal);
                                                        Toast.makeText(FoodDetails.this, f, Toast.LENGTH_SHORT).show();
                                                        updateOderDate(getKey);

                                                        //  Toast.makeText(FoodDetails.this, "YOU CANT NOT ADD, THE "+foodname.toUpperCase()+"EXIST IN CART", Toast.LENGTH_SHORT).show();

                                                    }else{
                                                        OderData();
                                                        // Toast.makeText(FoodDetails.this, "YOU CANT NOT ADD, THE SOO", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(FoodDetails.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        } else {
                                            Toast.makeText(FoodDetails.this, "connect your phone", 3000).show();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Action for 'NO' Button
                                        dialog.cancel();
                                        Toast.makeText(getApplicationContext(),"verify well",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        //Creating dialog box
                        AlertDialog alert = buildezr.create();
                        //Setting the title manually
                        alert.setTitle("Adding to Cart");
                        alert.show();
                    }
                }
                else {
                    Toast.makeText(FoodDetails.this, "connect your phone", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button print = (Button)findViewById(R.id.idprint);
        print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                String total=tot.getText().toString().trim();
                if (haveNetworkConnection()) {
                      if(TextUtils.isEmpty(total))
                    {
                        tot.setError("Total missing");
                        tot.requestFocus();
                        return ;
                    }else{
                              buildezr.setMessage("Do you want to Print this "+foodname+" Receipt ?")
                                      .setCancelable(false)
                                      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                          @SuppressLint("WrongConstant")
                                          @RequiresApi(api = Build.VERSION_CODES.N)
                                          public void onClick(DialogInterface dialog, int id) {
                                              if (haveNetworkConnection()) {

                                                    Simpleprint();

                                              } else {
                                                  Toast.makeText(FoodDetails.this, "connect your phone", 3000).show();
                                              }
                                          }
                                      })
                                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int id) {
                                              //  Action for 'NO' Button
                                              dialog.cancel();
                                              Toast.makeText(getApplicationContext(),"verify well",
                                                      Toast.LENGTH_SHORT).show();
                                          }
                                      });
                              //Creating dialog box
                              AlertDialog alert = buildezr.create();
                              //Setting the title manually
                              alert.setTitle("Printing Receipt");
                              alert.show();
                      }
                    }
                else {
                    Toast.makeText(FoodDetails.this, "connect your phone", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private List<Food> convertObjectArrayToListObject(Food[] allProducts){
        List<Food> mProduct = new ArrayList<Food>();
        Collections.addAll(mProduct, allProducts);
        return mProduct;
    }

    public void incrementquantity(View view) {
        quantity = quantity + 1;
        if (quantity > 50) {
            Toast.makeText(getApplicationContext(), "more than 50 not valid", Toast.LENGTH_SHORT).show();
        } else {
            displayquantity(quantity);
            total();
        }

    }
    private void displayquantity(int quantity) {
        quantity1 = (TextView) findViewById(R.id.quantitychange_value);
        quantity1.setText(""+quantity);

    }

    public void decrementquantity(View view) {
        quantity = quantity - 1;
        if (quantity < 0) {
            Toast.makeText(getApplicationContext(), "less than 1 not valid", Toast.LENGTH_SHORT).show();
        } else {
            displayquantity(quantity);
            total();
        }
    }
    public  void total(){
         tot = (TextView) findViewById(R.id.Texttotal);
        String StringQ=quantity1.getText().toString();
        String StringP=textprice.getText().toString();
        int total;
        int intQuanty=Integer.valueOf(StringQ);
        int intprice= Integer.valueOf(StringP);
        total=intQuanty*intprice;
        tot.setText(total+"");

    }

@RequiresApi(api = Build.VERSION_CODES.N)
public void OderData(){
    // Getting image name from EditText and store into string variable.
    String key =databaseReferenceUser.push().getKey();
    String foodname = textfood.getText().toString().trim();
    Long prix = Long.valueOf(textprice.getText().toString().trim());
    Long Total = Long.valueOf(texttotal.getText().toString().trim());
    String Quantity = textquantity.getText().toString().trim();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    // System.out.println(dateFormat.format(date));
    String Date = dateFormat.format(date);
    // Hiding the progressDialog after done uploading.
    FoodOrder Data = new FoodOrder(
            getKey,
            foodname,
            prix,
            Total,
            Quantity,
            Date
    );
    databaseReferenceUser.child(getKey).setValue(Data);
    FoodOderValid DataValid = new FoodOderValid(
            key,
            getKey,
            foodname,
            prix,
            Total,
            Quantity,
            Date
    );
    databaseReference.child(key).setValue(DataValid);

    final Food singleProduct = new Food();
    //increase product count
    String productsFromCart = sharedPreference.retrieveProductFromCart();
    if(productsFromCart.equals("")){
        List<Food> cartProductList = new ArrayList<Food>();
        cartProductList.add(singleProduct);
        String cartValue = gson.toJson(cartProductList);
        sharedPreference.addProductToTheCart(cartValue);
        cartProductNumber = cartProductList.size();
    }else{
        String productsInCart = sharedPreference.retrieveProductFromCart();
        Food[] storedProducts = gson.fromJson(productsInCart, Food[].class);

        List<Food> allNewProduct = convertObjectArrayToListObject(storedProducts);
        allNewProduct.add(singleProduct);
        String addAndStoreNewProduct = gson.toJson(allNewProduct);
        sharedPreference.addProductToTheCart(addAndStoreNewProduct);
        cartProductNumber = allNewProduct.size();
    }
    sharedPreference.addProductCount(cartProductNumber);
    invalidateCart();
}

@RequiresApi(api = Build.VERSION_CODES.N)
public  void sendReciept() {
    // Getting image name from EditText and store into string variable.
    String key =databaseReferenceUser.push().getKey();
    String foodname = textfood.getText().toString().trim();
    Long prix = Long.valueOf(textprice.getText().toString().trim());
    Long Total = Long.valueOf(texttotal.getText().toString().trim());
    String Quantity = textquantity.getText().toString().trim();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    // System.out.println(dateFormat.format(date));
    String Date = dateFormat.format(date);
    // Hiding the progressDialog after done uploading.
    FoodOderValid DataValid = new FoodOderValid(
            key,
            getKey,
            foodname,
            prix,
            Total,
            Quantity,
            Date
    );
    databaseReference.child(key).setValue(DataValid);


}

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateOderDate(String id)
    {
     // String key =databaseReferenceUser.push().getKey();
        String foodname = textfood.getText().toString().trim();
        Long prix = Long.valueOf(textprice.getText().toString().trim());
        Long Quantity=Long.valueOf( textquantity.getText().toString().trim());

        Long sum=Long.valueOf(getOldQuantity)+Quantity;
        String StringUpdateQuanty=String.valueOf(sum);

        String Total = texttotal.getText().toString().trim();
        Long Qt1=Long.valueOf(Total);
        Long sumt=Qt1+getOldTotal;
        Long UpdateTotal=Long.valueOf(sumt);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        String Date = dateFormat.format(date);
        if(TextUtils.isEmpty(Total))
        {
            texttotal.setError("product name Required");
            texttotal.requestFocus();
            return;
        }else{
            FoodOrder UpdateData = new FoodOrder(
                    id,
                    foodname,
                    prix,
                    UpdateTotal,
                    StringUpdateQuanty,
                    Date

            );
            databaseReferenceUser.child(id).setValue(UpdateData).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(FoodDetails.this, " Food Updated", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(),AdminDrower.class));
                           // finish();
                        }
                    });

            final DatabaseReference nm= FirebaseDatabase.getInstance().getReference();

            Query Q=nm.child("FoodReceipt").orderByChild("keyorder").equalTo(id);

            Q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            FoodReceiptkey=snapshot.child("key").getValue(String.class);

                        }
                        FoodOderValid DataValid = new FoodOderValid(
                                FoodReceiptkey,
                                getKey,
                                foodname,
                                prix,
                                UpdateTotal,
                                StringUpdateQuanty,
                                Date
                        );
                        databaseReference.child(FoodReceiptkey).setValue(DataValid).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FoodDetails.this, " Food Updated", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(getApplicationContext(),AdminDrower.class));
                                        // finish();
                                    }
                                });

                    }else
                    {
                        Toast.makeText(FoodDetails.this, " Error", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }
    /////////printing code project
    @SuppressLint("WrongConstant")
    public void Simpleprint() {

        // SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        // settings.edit().clear().commit();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(FoodDetails.this, "Message1", 2000).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(FoodDetails.this, DeviceList.class);
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
            }
        }
    }
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
                    Intent connectIntent = new Intent(FoodDetails.this,
                            DeviceList.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(FoodDetails.this, "Message", 2000).show();
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
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint({"WrongConstant", "HandlerLeak"})
        @Override
        public void handleMessage(Message msg) {

            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(FoodDetails.this, "DeviceConnected", 5000).show();
            Thread t = new Thread() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void run() {
                    try {
                        //mBluetoothAdapter.enable();
                        OutputStream os = mBluetoothSocket.getOutputStream();
                        String BILL = "";
                                String afoodname=textfood.getText().toString();
                                String aquantity=textquantity.getText().toString();
                                String atotal=texttotal.getText().toString();

                        Calendar c = Calendar.getInstance();
                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH);
                        int year = c.get(Calendar.YEAR);
                        String date = day + "-" + (month+1) + "-" + year;
                        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        BILL = "\nIstanbul Restaurant Invoice\n" + " "
                                + date+"\n"
                                + "Monday"+"\n"
                                + currentDateandTime+"\n"
                                + "Contact Us"+"\n";
                        BILL = BILL
                                + "-------------------------";
                        BILL = BILL + "\n\n";
                        BILL = BILL + aquantity+ "      "+afoodname+"\n";
                        BILL = BILL
                                + "--------------------\n";
                        BILL = BILL + "TOTAL" + "       "+atotal+"\n";

                        BILL = BILL + "\n\n";
                        os.write(BILL.getBytes());
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
            sendReciept();

        }
    };
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
/////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_shop);
        int mCount = sharedPreference.retrieveProductCount();
        menuItem.setIcon(buildCounterDrawable(mCount, R.drawable.cart));
        return true;
    }
    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.shopping_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_shop) {
            Intent checkoutIntent = new Intent(FoodDetails.this, CheckoutActivity.class);
            startActivity(checkoutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void invalidateCart() {
        invalidateOptionsMenu();
    }
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;

    }
}
