package com.prospect.faisalrestorant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.prospect.faisalrestorant.Activities.Login;
import com.prospect.faisalrestorant.Classes.FoodOderValid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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


    public static File pFile;
    private File payfile;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payRef = FirebaseDatabase.getInstance().getReference().child("FoodReceipt");
        pdfView = findViewById(R.id.payment_pdf_viewer);


        Button reportButton = findViewById(R.id.button_disable_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewDisabledUsersReport();

            }
        });

        //
        paymentUsersList = new ArrayList<>();

        //create files in charity care folder
        //create folder
        File payfile = new File(Environment.getExternalStorageDirectory() + "/RestaurantReport");
        if (!payfile.mkdirs()) {
            payfile.mkdirs();
        }

        //check if they exist, if not create them(directory)
        if ( !payfile.exists()) {
            payfile.mkdirs();
        }
        pFile = new File(payfile, "RestorantTest.pdf");

        //fetch payment and disabled users details;
        fetchPaymentUsers();
    }
    //function to fetch payment data from the database
    private void fetchPaymentUsers()
    {

        payRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

//creating an object and setting to displlay
                    FoodOderValid pays = new FoodOderValid();
                    pays.setFoodname(snapshot.child("foodname").getValue().toString());
                    pays.setQuantity(snapshot.child("quantity").getValue().toString());
                    pays.setTotalPrice(snapshot.child("totalPrice").getValue(Long.class));

                    //this just log details fetched from db(you can use Timber for logging
                    Log.d("Payment", "Name: " + pays.getFoodname());
                    Log.d("Payment", "othername: " + pays.getQuantity());
                    Log.d("Payment", "phone: " + pays.getTotalPrice());

                    /* The error before was cause by giving incorrect data type
                    You were adding an object of type PaymentUsers yet the arraylist expects obejct of type DisabledUsers
                     */
                    paymentUsersList.add(pays);


                }
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

        Chunk nameText = new Chunk(" Name", white);
        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
        nameCell.setFixedHeight(50);
        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk phoneText = new Chunk("QUANTITY", white);
        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
        phoneCell.setFixedHeight(50);
        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk("TOTOAL", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);


        Chunk footerText = new Chunk("Moses Njoroge - Copyright @ 2020");
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
        document.add(new Paragraph(" How to generate real-time reports using Firebase\n\n", g));
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

    private void SendUserToStepTwoAuthentication()
    {
        Intent steptwoIntent = new Intent(MainActivity.this, Login.class);
        steptwoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(steptwoIntent);
        finish();
    }


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
        pdfView.fromFile(pFile)
                .pages(0,2,1,3,3,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();


    }
}