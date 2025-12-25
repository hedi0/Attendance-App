package mh.hedi.attendancemanager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

public class SheetActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private String currentMonth;

    // bech t3rf sabit l pdf w le
    private boolean isPdfSaved = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);

        tableLayout = findViewById(R.id.tableLayout);
        currentMonth = getIntent().getStringExtra("month");

        //setup toolbar
        stToolbar();

        showTable();

    }

    private void stToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        // bdl l icon mt3 toolbar
        save.setImageResource(R.drawable.ic_save_pdf);
        title.setText("Attendance Sheet");
        subtitle.setText(currentMonth);

        back.setOnClickListener(v -> onBackPressed());
        save.setOnClickListener(v -> {
            if (isPdfSaved) {
                // Show message that PDF is already saved
                Toast.makeText(SheetActivity.this,
                        "PDF already downloaded",
                        Toast.LENGTH_SHORT).show();
            } else {
                exportToPdf();
            }
        });
    }


    // PDF fonction
    private void exportToPdf() {
        ImageButton saveButton = findViewById(R.id.save);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                saveButton.setImageResource(R.drawable.ic_saved);
                isPdfSaved = true;

                // Capture table as bitmap
                tableLayout.setDrawingCacheEnabled(true);
                Bitmap tableBitmap = Bitmap.createBitmap(tableLayout.getDrawingCache());
                tableLayout.setDrawingCacheEnabled(false);

                // Get title and subtitle from toolbar
                TextView titleView = findViewById(R.id.title_toolbar);
                TextView subtitleView = findViewById(R.id.subtitle_toolbar);

                // Generate PDF with header
                String fileName = PdfExportUtil.generateFileName("AppelTime");
                PdfExportUtil.exportToPdf(SheetActivity.this, tableBitmap,
                        titleView.getText().toString(),
                        subtitleView.getText().toString(),
                        fileName);

                // Restore animations
                Animation fadeIn = AnimationUtils.loadAnimation(SheetActivity.this, R.anim.fade_in);
                saveButton.startAnimation(fadeIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        saveButton.startAnimation(fadeOut);
    }

    private void showTable() {

        BaseDeDonnees Database = new BaseDeDonnees(this);

        TableLayout tableLayout = findViewById(R.id.tableLayout);

        long[] sidArray = getIntent().getLongArrayExtra("sidArray");
        int[] idArray = getIntent().getIntArrayExtra("idArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");

        int DAY_IN_MONTH = getDayInMonth(month);

        // setup mt3 les lignes
        int rowSize = sidArray.length + 1 ;

        TableRow[] rows = new TableRow[rowSize];
        TextView[] id_tvs = new TextView[rowSize];
        TextView[] name_tvs = new TextView[rowSize];
        TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH + 1];

        for ( int i = 0 ; i < rowSize ; i++ ){
            id_tvs[i] = new TextView(this);
            name_tvs[i] = new TextView(this);
            for ( int j = 1 ; j <= DAY_IN_MONTH ; j++ ){
                status_tvs[i][j] = new TextView(this);
            }
        }

        // header mt3 tableau d'appel
        id_tvs[0].setText("Identifiant");
        // style d'ecriture
        id_tvs[0].setTypeface(id_tvs[0].getTypeface(), Typeface.BOLD);

        name_tvs[0].setText("Name");
        name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);

        for ( int i =1 ; i <= DAY_IN_MONTH ; i++ ){
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
        }

        for ( int i = 1 ; i < rowSize ; i++ ){
            id_tvs[i].setText(String.valueOf(idArray[i-1]));
            name_tvs[i].setText(nameArray[i-1]);

            for (int j = 1 ; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if(day.length()==1) day = "0"+day;

                String date = day+"."+month;
                String status = Database.getStatus(sidArray[i-1],date);
                status_tvs[i][j].setText(status);
            }
        }

        for (int i = 0; i < rowSize; i++) {
            rows[i] = new TableRow(this);

            //
            if(i%2 == 0){
                rows[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
            }else{
                rows[i].setBackgroundColor(Color.parseColor("#E4E4E4"));
            }

            id_tvs[i].setPadding(16,16,16,16);
            name_tvs[i].setPadding(16,16,16,16);

            rows[i].addView(id_tvs[i]);
            rows[i].addView(name_tvs[i]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j].setPadding(16,16,16,16);
                rows[i].addView(status_tvs[i][j]);
            }
            tableLayout.addView(rows[i]);
        }
        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
    }

    private int getDayInMonth(String month) {
        int monthIndex = Integer.valueOf(month.substring(0 , 1));
        int year = Integer.valueOf(month.substring(4));

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH,monthIndex);
        calendar.set(Calendar.YEAR,year);

        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}