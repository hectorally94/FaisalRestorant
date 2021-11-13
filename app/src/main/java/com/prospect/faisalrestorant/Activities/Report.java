package com.prospect.faisalrestorant.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.prospect.faisalrestorant.Classes.FoodOderValid;
import com.prospect.faisalrestorant.MainActivity;
import com.prospect.faisalrestorant.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Report extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    //
    TextView textViewtodayticketbuja,textViewtodaycourrierbuja,textViewtodayticketngozi,textViewtodaycourrierngozi,textViewtodayticketkayanza,textViewtodaycourrierkayanza;
    TextView textViewdeletefrom,textViewdeleteto,textViewiddatefrom,textViewiddateto;

    //firebase auth to check if user is authenticated.
    private DatabaseReference payRef;
    //creating a list of objects constants
    List<FoodOderValid> paymentUsersList;
    //List all permission required
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int PERMISSION_ALL = 12;


    public static File pFile,payfile;
    private PDFView pdfView;
    private AppCompatEditText pdfdescription;
    String getpdf="Restaurant_Report_of";
    String Date;
    Date date;
    TextView daate;
    DatePickerDialog datePickerDialog;
    int mMonth; // current month
    int mDay;
    final Long[] totalAmount = {0l};
    ProgressDialog progressDoalog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ///

        payRef = FirebaseDatabase.getInstance().getReference().child("FoodReceipt");
        pdfView = findViewById(R.id.payment_pdf_viewer);
        pdfdescription = findViewById(R.id.idpdfdecription);

        Button reportButton = findViewById(R.id.button_disable_report);
        payfile = new File(Environment.getExternalStorageDirectory() +"/RestaurantReport");
        if (!payfile.mkdirs()) {
            payfile.mkdirs();
        }

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(pdfdescription.getText()))
                {
                    Toast.makeText(Report.this, "Describe your pdf Report", Toast.LENGTH_SHORT).show();
                }else{
                    //create files in charity care folder
                    //create folder
                    payfile = new File(Environment.getExternalStorageDirectory() +"/RestaurantReport");
                    if (!payfile.mkdirs()) {
                        payfile.mkdirs();
                    }
                    getpdf = pdfdescription.getText().toString();
                    //
                    paymentUsersList = new ArrayList<>();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
                    //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    date = new Date();
                    // System.out.println(dateFormat.format(date));
                    Date = dateFormat.format(date);
                    pFile = new File(payfile, ""+getpdf+""+"_"+""+Date+""+"_"+".pdf");

                    //fetch payment and disabled users details;
                    fetchPaymentUsers();

                    previewDisabledUsersReport();

                }
            }
        });
       textViewdeletefrom=findViewById(R.id.iddeletefrom);
        textViewdeleteto=findViewById(R.id.iddeleteto);
        //
        textViewiddatefrom=findViewById(R.id.iddatefrom);
        textViewiddateto=findViewById(R.id.iddateto);
          daate =  findViewById(R.id.date);
        textViewdeletefrom=findViewById(R.id.iddeletefrom);
        textViewdeleteto=findViewById(R.id.iddeleteto);
        //
        textViewiddatefrom=findViewById(R.id.iddatefrom);
        textViewiddateto=findViewById(R.id.iddateto);
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

        //performing on click to the textview
        textViewdeletefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewiddatefrom.setText("");
                textViewiddateto.setText("");
                daate.setText("Get first Date");

            }
        });
        textViewdeleteto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewiddateto.setText("");
                daate.setText("Get Second Date");

                        }
        });

        daate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the values for day of month , month and year from a date picker
                if(textViewiddatefrom.getText().toString().isEmpty()){
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    mMonth = c.get(Calendar.MONTH); // current month
                    mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(Report.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // set day of month , month and year value in the edit text
                                    textViewiddatefrom.setText(
                                            year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                    daate.setText("Select Second Date");
                                }
                            }, mYear,mMonth , mDay);

                    datePickerDialog.show();
                }else if(textViewiddateto.getText().toString().isEmpty()){
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(Report.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    // set day of month , month and year value in
                                    textViewiddateto.setText(
                                            year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                    daate.setText("Press create pdf ");
                                }
                            }, mYear,mMonth, mDay);
                    datePickerDialog.show();

                }else{
                    Toast.makeText(Report.this,"no action selected",Toast.LENGTH_LONG);
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)

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
            case R.id.addproeduct:
                Intent intent1 = new Intent(getApplicationContext(), AddProduct.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.activity_login:
                Intent intent3 = new Intent(getApplicationContext(), Login.class);
                startActivity(intent3);
                finish();
                break;
            default:
                Toast.makeText(Report.this,"No Activity selected",Toast.LENGTH_LONG).show();
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
///
//function to fetch payment data from the database
private void fetchPaymentUsers()
{   progressDoalog = new ProgressDialog(Report.this);
    progressDoalog.setMax(100);
    progressDoalog.setMessage("loading....");
    progressDoalog.setTitle("Between Days Report");
    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDoalog.setCancelable(false);
    progressDoalog.show();
    final DatabaseReference gettotaduDate= FirebaseDatabase.getInstance().getReference();
    String getdatafrom=textViewiddatefrom.getText().toString().trim();
    String getdateTo=textViewiddateto.getText().toString().trim();

    final Query getDate= gettotaduDate.child("FoodReceipt").orderByChild("date").startAt(getdatafrom).endAt(getdateTo);
    getDate.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//creating an object and setting to displlay
                FoodOderValid pays = new FoodOderValid();
                pays.setFoodname(snapshot.child("foodname").getValue().toString());
                pays.setQuantity(snapshot.child("quantity").getValue().toString());
                pays.setTotalPrice(snapshot.child("totalPrice").getValue(Long.class));

                totalAmount[0] += snapshot.child("totalPrice").getValue(Long.class);
                //this just log details fetched from db(you can use Timber for logging
                Log.d("Payment", "Name: " + pays.getFoodname());
                Log.d("Payment", "othername: " + pays.getQuantity());
                Log.d("Payment", "phone: " + pays.getTotalPrice());

                    /* The error before was cause by giving incorrect data type
                    You were adding an object of type PaymentUsers yet the arraylist expects obejct of type DisabledUsers
                     */
                paymentUsersList.add(pays);

            }
            progressDoalog.dismiss();
            //create a pdf file and catch exception beacause file may not be created
            try {
                createPaymentReport(paymentUsersList);
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


}

    private void createPaymentReport(  List<FoodOderValid> paymentUsersList) throws DocumentException, FileNotFoundException{
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");



        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(pFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{6, 25, 20, 20});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk noText = new Chunk("No.", white);
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk nameText = new Chunk(" FOOD NAME", white);
        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
        nameCell.setFixedHeight(50);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk phoneText = new Chunk("QUANTITY", white);
        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
        phoneCell.setFixedHeight(50);
        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk("TOTAL", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);
        Long[] priceToal = {0l};
        for(int i=0;i<paymentUsersList.size();i++){

            priceToal[0] +=paymentUsersList.get(i).getTotalPrice();

        }
        Chunk footerText = new Chunk("TOTAL OF ALL: "+ priceToal[0]+" FBU");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(4);


        table.addCell(noCell);
        table.addCell(nameCell);
        table.addCell(phoneCell);
        table.addCell(amountCell);
        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(grayColor);
        }
        for (int i = 0; i < paymentUsersList.size(); i++) {
            FoodOderValid pay = paymentUsersList.get(i);

            String id = String.valueOf(i + 1);
            String name = pay.getFoodname();
            String sname = pay.getQuantity();
            Long phone = pay.getTotalPrice();


            table.addCell(id + ". ");
            table.addCell(name);
            table.addCell(sname);
            table.addCell(String.valueOf(phone));

        }

        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph(" RESTAURANT REPORT OF "+Date+"\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    //onstart method used to check if the user is registered or not

    public void previewDisabledUsersReport()
    {
        if (hasPermissions(this, PERMISSIONS)) {
            DisplayReport();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void DisplayReport()
    {
        payfile.mkdirs();
        pdfView.fromFile(pFile)
               .load();


    }
}
