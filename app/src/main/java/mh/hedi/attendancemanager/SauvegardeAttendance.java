package mh.hedi.attendancemanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SauvegardeAttendance extends AppCompatActivity {

    private ListView sheetList ;
    private ArrayAdapter adapter ;
    private ArrayList<String> listItems = new ArrayList();
    private long cid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sauvegarde_attendance);

        cid = getIntent().getLongExtra("cid",-1);
        Log.i("SauvegardeAttendance","onCreate : "+cid);

        setToolbar();

        loadListItems();
        sheetList = findViewById(R.id.sheetList);
        adapter = new ArrayAdapter(this,R.layout.sheet_list,R.id.date_list_item,listItems);
        sheetList.setAdapter(adapter);

        sheetList.setOnItemClickListener((parent, view, position, id) -> openSheetActivity(position));
    }

    private void setToolbar() {

            Toolbar toolbar = findViewById(R.id.toolbar);
            TextView title = toolbar.findViewById(R.id.title_toolbar);
            TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
            ImageButton back = toolbar.findViewById(R.id.back);
            ImageButton save = toolbar.findViewById(R.id.save);

            title.setText("Attendance Months");
            subtitle.setVisibility(View.GONE);
            save.setVisibility(View.GONE);

            back.setOnClickListener(v -> onBackPressed());
    }

    private void openSheetActivity(int position) {

        long[] sidArray = getIntent().getLongArrayExtra("sidArray");
        int[] idArray = getIntent().getIntArrayExtra("idArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");

        Intent intent = new Intent(this, SheetActivity.class);

        intent.putExtra("sidArray",sidArray);
        intent.putExtra("idArray",idArray);
        intent.putExtra("nameArray",nameArray);
        intent.putExtra("month",listItems.get(position));

        startActivity(intent);
    }

    private void loadListItems() {
        Cursor cursor = new BaseDeDonnees(this).getDistinctMonths(cid);

        Log.i("SauvegardeAttendance","LoadListItems: "+cursor.getCount());
        while (cursor.moveToNext()){ //05.05.2025
            String date = cursor.getString(cursor.getColumnIndex(BaseDeDonnees.DATE_KEY));
            listItems.add(date.substring(3));
        }
    }
}