package mh.hedi.attendancemanager;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.BitmapFactory;
import androidx.core.text.TextUtilsCompat;
import android.os.Environment;
import android.print.PrintAttributes;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfExportUtil {

    public static void exportToPdf(Context context, Bitmap tableBitmap,
                                   String title, String subtitle, String fileName) {
        // Create PDF document
        PdfDocument document = new PdfDocument();

        // Calculate dimensions
        int width = tableBitmap.getWidth();
        int height = tableBitmap.getHeight() + 200;

        // Create page info
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1)
                .setContentRect(new Rect(0, 0, width, height))
                .create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw header background
        Paint headerPaint = new Paint();
        headerPaint.setColor(Color.LTGRAY);
        canvas.drawRect(0, 0, width, 150, headerPaint);

        // Draw logo
        try {
            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
            float scaleFactor = 60f / logo.getHeight();
            Bitmap scaledLogo = Bitmap.createScaledBitmap(logo,
                    (int)(logo.getWidth() * scaleFactor),
                    (int)(logo.getHeight() * scaleFactor),
                    true);
            canvas.drawBitmap(scaledLogo, width - scaledLogo.getWidth() - 30, 30, null);
        } catch (Exception e) {
            // Continue without logo
        }

        // Set up text paint (using TextPaint now)
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        // Draw title with ellipsizing
        String ellipsizedTitle = TextUtils.ellipsize(title, textPaint, width - 200,
                TextUtils.TruncateAt.END).toString();
        canvas.drawText(ellipsizedTitle, 40, 60, textPaint);

        // Draw subtitle
        textPaint.setTextSize(20);
        textPaint.setTypeface(Typeface.DEFAULT);
        canvas.drawText(subtitle, 40, 90, textPaint);

        // Draw date
        textPaint.setTextSize(14);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        float dateWidth = textPaint.measureText("Generated on: " + currentDate);
        canvas.drawText("Generated on: " + currentDate, width - dateWidth - 30, 100, textPaint);

        // Draw the table
        canvas.drawBitmap(tableBitmap, 0, 150, null);

        document.finishPage(page);

        // Save PDF
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }

    public static String generateFileName(String prefix) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return prefix + "_" + date;
    }
}