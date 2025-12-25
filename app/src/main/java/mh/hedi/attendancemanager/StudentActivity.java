package mh.hedi.attendancemanager;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

public class StudentActivity extends AppCompatActivity {

    Toolbar toolbar ;
    private String className ;
    private String subjectName ;
    private int position ;

    //declare variable bch t3bi b asami l students
    private RecyclerView recyclerView ;
    private StudentAdapter studentAdapter;
    private RecyclerView.LayoutManager layoutManager ;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();

    //base de donnees
    BaseDeDonnees Database ;
    private long cid ;

    // changement de Date
    private MyCalendar calendar;
    private TextView subtitle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        calendar = new MyCalendar();

        Database = new BaseDeDonnees(this);

        // t3yt ll donnees li b3ththom ml Main Activity
        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position",-1);
        cid = intent.getLongExtra("cid",-1);

        // t3yt ll fonction mt3 toolbar
        setToolbar();

        // bch donnees yo93dou ki t5rj ml application
        loadData();



        // bch t3bi les items b les noms d'etudiants
        recyclerView = findViewById(R.id.student_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        studentAdapter = new StudentAdapter(this, studentItems);
        recyclerView.setAdapter(studentAdapter);

        // bch tzid l status(absent,present)
        studentAdapter.setOnItemClickListener(position -> changeStatus(position));

        // fonction ll les options (EDIT),(DELETE)
        studentAdapter.setOnMenuItemClickListener(new StudentAdapter.OnMenuItemClickListener() {
            @Override
            public void onEditClick(int position) {
                // Edit Student
                showUpdateStudentDialog(position);
            }
            @Override
            public void onDeleteClick(int position) {
                // Delete Student
                deleteStudent(position);
            }
        });
        //bch donnees mt3 student status t93d
        loadStatusData();
    }

    private void loadData() {

        Cursor cursor = Database.getStudentTable(cid);
        Log.i("StudentActivity","loadData : "+cid);
        studentItems.clear();

        while (cursor.moveToNext()) {
            // lazmek temchi l BaseDeDonnees w t7othom public bch tnjm t5dm bihom
            long sid = cursor.getLong(cursor.getColumnIndex(BaseDeDonnees.S_ID));
            int id = cursor.getInt(cursor.getColumnIndex(BaseDeDonnees.STUDENT_ID_KEY));
            String name = cursor.getString(cursor.getColumnIndex(BaseDeDonnees.STUDENT_NAME_KEY));

            studentItems.add(new StudentItem(sid, id, name));
        }
        cursor.close();
    }

    private void changeStatus(int position) {

        String status = studentItems.get(position).getStatus();

        if(status.equals("P")) status = "A";
        else status = "P";

        studentItems.get(position).setStatus(status);
        studentAdapter.notifyItemChanged(position);
    }

    private void setToolbar() {

        //t3ml setup ll toolbar mt3k
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        //toolbar save Button ki tnzl
        save.setOnClickListener(v -> saveStatus());
        //bch t7ot l class w subject fl toolbar
        title.setText(className);
        subtitle.setText(subjectName+" | "+calendar.getDate());

        // ki tnzl 3al back button trj3 lteli 5otwa
        back.setOnClickListener(v -> onBackPressed());

        // l menu bech integrih
        //toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem->onMenuItemClick(menuItem));

        // Remove the menu inflation from here since we'll
        // handle it in onCreateOptionsMenu
        setSupportActionBar(toolbar);
    }

    // the icons menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);

        int customColor = Color.BLUE;

        // Apply color to "CHANGE DATE"
        MenuItem calendarItem = menu.findItem(R.id.show_Calendar);
        SpannableString calendarText = new SpannableString(calendarItem.getTitle());
        calendarText.setSpan(
                new ForegroundColorSpan(customColor),
                0,
                calendarText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        calendarItem.setTitle(calendarText);

        // Apply color to "ATTENDANCE SHEET"
        MenuItem sheetItem = menu.findItem(R.id.show_attendance_sheet);
        SpannableString sheetText = new SpannableString(sheetItem.getTitle());
        sheetText.setSpan(
                new ForegroundColorSpan(customColor),
                0,
                sheetText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        sheetItem.setTitle(sheetText);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        return onMenuItemClick(menuItem) || super.onOptionsItemSelected(menuItem);
    }


    // les icones tforcihom bech yodhhrou
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    // ------------------------------------------------
    private void saveStatus() {
        for(StudentItem studentItem : studentItems){
            String status = studentItem.getStatus();
            // ken mouch Present 7otou Absent Automatiquement
            //if(status != "P") status = "A" ;
            long value = Database.addStatus(studentItem.getSid(),cid,calendar.getDate(),status);

            if(value == -1)Database.updateStatus(studentItem.getSid(),calendar.getDate(),status);
        }
        Toast.makeText(this,"Status Saved",LENGTH_SHORT).show();
    }

    private void loadStatusData(){
        for(StudentItem studentItem : studentItems){
            String status = Database.getStatus(studentItem.getSid(),calendar.getDate());
            if(status != null) studentItem.setStatus(status);
            else studentItem.setStatus("");
        }
        studentAdapter.notifyDataSetChanged();
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        //option loula mt3 menu
        if(menuItem.getItemId()==R.id.add_student){
            showAddStudentDialog();
        }
        //option thenya mt3 menu
        else if(menuItem.getItemId()==R.id.show_Calendar){
            showCalendar();
        }
        else if(menuItem.getItemId()==R.id.show_attendance_sheet){
            openSheetList();
        }
        return true;
    }

    private void openSheetList() {

        long[] sidArray = new long[studentItems.size()];
        String[] nameArray = new String[studentItems.size()];
        int[] idArray = new int[studentItems.size()];

        for(int i=0 ; i < sidArray.length ; i++)
           sidArray[i] = studentItems.get(i).getSid();

        for(int i=0 ; i < idArray.length ; i++)
            idArray[i] = studentItems.get(i).getId();

        for(int i=0 ; i < nameArray.length ; i++)
            nameArray[i] = studentItems.get(i).getName();


        Intent intent = new Intent(this,SauvegardeAttendance.class);
        intent.putExtra("cid",cid);
        intent.putExtra("sidArray",sidArray);
        intent.putExtra("idArray",idArray);
        intent.putExtra("nameArray",nameArray);

        startActivity(intent);
    }

    private void showCalendar() {

        calendar.show(getSupportFragmentManager(),"");
        calendar.setOnCalendarOkClickListener((this::onCalendarOkClicked));
    }

    private void onCalendarOkClicked(int year, int month, int day) {
        calendar.setDate(year, month, day);
        subtitle.setText(subjectName+" | "+calendar.getDate());
        loadStatusData();

    }

    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_ADD_DIALOG);
        dialog.setListener((identif,name)-> CreateStudent(identif,name));
    }

    private void CreateStudent(String identifiant_chaine, String name) {
        int identifiant = Integer.parseInt(identifiant_chaine);

        // bch tsjl student fil base de donnees
        long sid = Database.CreateStudent((int) cid,identifiant,name);
        StudentItem studentItem = new StudentItem(sid,identifiant,name);

        //bch tzid fl RecycleView
        studentItems.add(studentItem);

        Log.d("StudentActivity", "Creating student - ID: " + identifiant + ", Name: " + name);
        Log.d("StudentActivity", "List size: " + studentItems.size());

        // kenet (notifyItemChanged) m7btch twrili l students y5i bdltha
        studentAdapter.notifyDataSetChanged();
    }

    //t3ti l fonction lkol Options mt3 Long Press
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0 :
                // Edit Student
                showUpdateStudentDialog(item.getGroupId());
                break;
            case 1 :
                // Delete Student
                deleteStudent(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    // fonction tdhhrlk dialog mt3 Edit informations ll option (EDIT)
    private void showUpdateStudentDialog(int position) {
        MyDialog dialog = new MyDialog(studentItems.get(position).getId(),studentItems.get(position).getName());
        dialog.show(getSupportFragmentManager(),MyDialog.STUDENT_UPDATE_DIALOG);
        dialog.setListener((id_string,name)->UpdateStudent(position,name));
    }

    // bech ki t3ml update ytbdl fl base de donnees
    private void UpdateStudent(int position, String name) {
        Database.updateStudent(studentItems.get(position).getSid(),name);

        studentItems.get(position).setName(name);
        studentAdapter.notifyItemChanged(position);
    }

    // fonction ll option (DELETE)
    private void deleteStudent(int position) {
        // ki tfs5 ttfs5 permanente ml base de donnees
        Database.deleteStudent(studentItems.get(position).getSid());

        studentItems.remove(position);
        studentAdapter.notifyItemRemoved(position);

    }
}