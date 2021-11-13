package com.prospect.faisalrestorant.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.prospect.faisalrestorant.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public final class PDFUtility
{
    private static final String TAG = PDFUtility.class.getSimpleName();
    private static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static Font FONT_SUBTITLE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    private static Font FONT_CELL = new Font(Font.FontFamily.TIMES_ROMAN,  12, Font.NORMAL);
    private static Font FONT_COLUMN = new Font(Font.FontFamily.TIMES_ROMAN,  14, Font.NORMAL);

    public interface OnDocumentClose
    {
        void onPDFDocumentClose(File file);
    }

    public static void createPdf(@NonNull Context mContext, OnDocumentClose mCallback, List<FoodOderValid> items, @NonNull String filePath, boolean isPortrait) throws Exception
    {
        if(filePath.equals(""))
        {
            throw new NullPointerException("PDF File Name can't be null or blank. PDF File can't be created");
        }

        File file = new File(filePath);

        if(file.exists())
        {
            file.delete();
            Thread.sleep(50);
        }

        Document document = new Document();
        document.setMargins(24f,24f,32f,32f);
        document.setPageSize(isPortrait? PageSize.A4:PageSize.A4.rotate());

        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        pdfWriter.setFullCompression();
        pdfWriter.setPageEvent(new PageNumeration());

        document.open();

        setMetaData(document);

        addHeader(mContext,document);
        addEmptyLine(document, 3);

        document.add(createDataTable(items));

        addEmptyLine(document,2);
        document.add(createSignBox());

        document.close();

        try
        {
            pdfWriter.close();
        }
        catch (Exception ex)
        {
            Log.e(TAG,"Error While Closing pdfWriter : "+ex.toString());
        }

        if(mCallback!=null)
        {
            mCallback.onPDFDocumentClose(file);
        }
    }

    private static  void addEmptyLine(Document document, int number) throws DocumentException
    {
        for (int i = 0; i < number; i++)
        {
            document.add(new Paragraph(" "));
        }
    }

    private static void setMetaData(Document document)
    {
        document.addCreationDate();
        //document.add(new Meta("",""));
        document.addAuthor( "");
        document.addCreator("");
        document.addHeader("","");
    }

    private static void addHeader(Context mContext, Document document) throws Exception
    {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2,7,2});
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            /*LEFT TOP LOGO*/
            Drawable d= ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher);
            Bitmap bmp=((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);

            Image logo=Image.getInstance(stream.toByteArray());
            logo.setWidthPercentage(80);
            logo.scaleToFit(105,55);

            cell = new PdfPCell(logo);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setUseAscender(true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(2f);
            table.addCell(cell);
        }

        {
            /*MIDDLE TEXT*/
            cell = new PdfPCell();
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(8f);
            cell.setUseAscender(true);

            Paragraph temp = new Paragraph("I AM TITLE" ,FONT_TITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            temp = new Paragraph("I am Subtitle" ,FONT_SUBTITLE);
            temp.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(temp);

            table.addCell(cell);
        }
        /* RIGHT TOP LOGO*/
        {
            PdfPTable logoTable=new PdfPTable(1);
            logoTable.setWidthPercentage(100);
            logoTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            logoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);

            Drawable drawable= ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher_round);
            Bitmap bmp =((BitmapDrawable)drawable).getBitmap();

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
            Image logo=Image.getInstance(stream.toByteArray());
            logo.setWidthPercentage(80);
            logo.scaleToFit(38,38);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);

            logoTable.addCell(logoCell);

            logoCell = new PdfPCell(new Phrase("Logo Text",FONT_CELL));
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setBorder(PdfPCell.NO_BORDER);
            logoCell.setPadding(4f);
            logoTable.addCell(logoCell);

            cell = new PdfPCell(logoTable);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setUseAscender(true);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(2f);
            table.addCell(cell);
        }

        //Paragraph paragraph=new Paragraph("",new Font(Font.FontFamily.TIMES_ROMAN, 2.0f, Font.NORMAL, BaseColor.BLACK));
        //paragraph.add(table);
        //document.add(paragraph);
        document.add(table);
    }

    private static PdfPTable createDataTable(List<FoodOderValid> dataTable) throws DocumentException
    {
        PdfPTable table1 = new PdfPTable(2);
        table1.setWidthPercentage(100);
        table1.setWidths(new float[]{1f,2f});
        table1.setHeaderRows(1);
        table1.getDefaultCell().setVerticalAlignment(Element.ALIGN_CENTER);
        table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cell;
        {
            cell = new PdfPCell(new Phrase("FOOD NAME", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase("FOOD  QUANTITY", FONT_COLUMN));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(4f);
            table1.addCell(cell);
        }

        float top_bottom_Padding = 8f;
        float left_right_Padding = 4f;
        boolean alternate = false;

        BaseColor lt_gray = new BaseColor(221,221,221); //#DDDDDD
        BaseColor cell_color;

        int size = dataTable.size();

        for (int i = 0; i < size; i++)
        {
            cell_color = alternate ? lt_gray : BaseColor.WHITE;
            FoodOderValid tempA = dataTable.get(i);

            cell = new PdfPCell(new Phrase(tempA.foodname, FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            cell = new PdfPCell(new Phrase(tempA.quantity, FONT_CELL));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPaddingLeft(left_right_Padding);
            cell.setPaddingRight(left_right_Padding);
            cell.setPaddingTop(top_bottom_Padding);
            cell.setPaddingBottom(top_bottom_Padding);
            cell.setBackgroundColor(cell_color);
            table1.addCell(cell);

            alternate = !alternate;
        }

        return table1;
    }

    private static PdfPTable createSignBox() throws DocumentException
    {
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);
        outerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        PdfPTable innerTable = new PdfPTable(2);
        {
            innerTable.setWidthPercentage(100);
            innerTable.setWidths(new float[]{1,1});
            innerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            //ROW-1 : EMPTY SPACE
            PdfPCell cell = new PdfPCell();
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setFixedHeight(60);
            innerTable.addCell(cell);

            //ROW-2 : EMPTY SPACE
            cell = new PdfPCell();
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setFixedHeight(60);
            innerTable.addCell(cell);

            //ROW-3 : Content Left Aligned
            cell = new PdfPCell();
            Paragraph temp = new Paragraph(new Phrase("Signature of Supervisor",FONT_SUBTITLE));
            cell.addElement(temp);

            temp = new Paragraph(new Phrase("( RAVEESH G S )",FONT_SUBTITLE));
            temp.setPaddingTop(4f);
            temp.setAlignment(Element.ALIGN_LEFT);
            cell.addElement(temp);

            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(4f);
            innerTable.addCell(cell);

            //ROW-4 : Content Right Aligned
            cell = new PdfPCell(new Phrase("Signature of Staff ",FONT_SUBTITLE));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(4f);
            innerTable.addCell(cell);
        }

        PdfPCell signRow = new PdfPCell(innerTable);
        signRow.setHorizontalAlignment(Element.ALIGN_LEFT);
        signRow.setBorder(PdfPCell.NO_BORDER);
        signRow.setPadding(4f);

        outerTable.addCell(signRow);

        return outerTable;
    }

    private static Image getImage(byte[] imageByte, boolean isTintingRequired) throws Exception
    {
        Paint paint=new Paint();
        if(isTintingRequired)
        {
            paint.setColorFilter(new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN));
        }
        Bitmap input  = BitmapFactory.decodeByteArray(imageByte, 0,imageByte.length);
        Bitmap output = Bitmap.createBitmap(input.getWidth(),input.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input,0,0,paint);

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        output.compress(Bitmap.CompressFormat.PNG,100,stream);
        Image image=Image.getInstance(stream.toByteArray());
        image.setWidthPercentage(80);
        return image;
    }

    private static Image getBarcodeImage(PdfWriter pdfWriter, String barcodeText)
    {
        Barcode128 barcode=new Barcode128();
        //barcode.setBaseline(-1); //BARCODE TEXT ABOVE
        barcode.setFont(null);
        barcode.setCode(barcodeText);
        barcode.setCodeType(Barcode128.CODE128);
        barcode.setTextAlignment(Element.ALIGN_BASELINE);
        return barcode.createImageWithBarcode(pdfWriter.getDirectContent(),BaseColor.BLACK,null);
    }
}
