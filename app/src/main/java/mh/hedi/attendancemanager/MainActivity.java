package mh.hedi.attendancemanager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton plus ;
    RecyclerView recyclerView;
    ClassAdapter classAdapter;
    RecyclerView.LayoutManager layoutManager ;
    ArrayList<ClassItem> classItems = new ArrayList<>();
    Toolbar toolbar ;

    BaseDeDonnees Database ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database = new BaseDeDonnees(this);

        plus = findViewById(R.id.plus_btn);
        plus.setOnClickListener(v -> {
            // Pulse animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.2f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.2f, 1f);
            AnimatorSet pulse = new AnimatorSet();
            pulse.playTogether(scaleX, scaleY);
            pulse.setDuration(300);
            pulse.start();
            // fonction mt3 show dialog
            showDialog();
        });

        // bch donnees yo93dou ki t5rj ml application
        loadData();

        // bch t3bi les items b les classes
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        classAdapter = new ClassAdapter(this, classItems);
        recyclerView.setAdapter(classAdapter);

        // bch ki tenzel 3al class thezek l StudentActivity
        classAdapter.setOnItemClickListener(position -> gotoItemActivity(position));

        // fonction ll les options (EDIT),(DELETE)
        classAdapter.setOnMenuItemClickListener(new ClassAdapter.OnMenuItemClickListener() {
            @Override
            public void onEditClick(int position) {
                // EDIT CLASS
                showUpdateDialog(position);
            }

            @Override
            public void onDeleteClick(int position) {
                // DELETE CLASS
                deleteClass(position);
            }
        });

        setToolbar();
    }

    private void loadData() {

        Cursor cursor = Database.getClassTable();
        classItems.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // lazmek temchi l BaseDeDonnees w t7othom public bch tnjm t5dm bihom
                int id = cursor.getInt(cursor.getColumnIndex(BaseDeDonnees.C_ID));
                String className = cursor.getString(cursor.getColumnIndex(BaseDeDonnees.CLASS_NAME_KEY));
                String subjectName = cursor.getString(cursor.getColumnIndex(BaseDeDonnees.SUBJECT_NAME_KEY));

                classItems.add(new ClassItem(id, className, subjectName));
            }
        }
    }

    private void setToolbar() {

        //t3ml setup ll toolbar mt3k
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("Attendance App");
        subtitle.setVisibility(View.GONE); // 'GONE' tn7iha jemla w l title ywli fl wst vertical et horizontal
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
    }
    private void gotoItemActivity(int position) {

        //bech ki tenzel 3al class thezek l StudentActivity
        Intent intent = new Intent(this,StudentActivity.class);

        // tb3th l donnees ll StudentActivity
        intent.putExtra("className",classItems.get(position).getClassName());
        intent.putExtra("subjectName",classItems.get(position).getSubjectName());
        intent.putExtra("position",position);
        intent.putExtra("cid",classItems.get(position).getCid());
        startActivity(intent);

    }

    private void showDialog(){

        //t3yt ll dialog mt3 CreateClass
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.CLASS_ADD_DIALOG);
        dialog.setListener((className,SubjectName)-> CreateClass(className,SubjectName));

    }
    // fonction tesna3 class
    private void CreateClass(String className,String subjectName) {

        //ll base de donnees bch t3ml id ll class li ttzad
        long cid = Database.CreateClass(className,subjectName);

        ClassItem classItem = new ClassItem(cid,className,subjectName);
        Log.d("MainActivity", "Creating Class - Name: " + className + " ,Subject : " + subjectName);
        classItems.add(classItem);
        classAdapter.notifyDataSetChanged();
    }

    // fonction ll les options (EDIT),(DELETE)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Get the position from the menu item
        int position = item.getGroupId();

        switch(item.getItemId()) {
            case 0: // EDIT
                showUpdateDialog(position);
                return true;
            case 1: // DELETE
                deleteClass(position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // fonction ll option (EDIT)
    private void showUpdateDialog(int position) {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(),MyDialog.CLASS_UPDATE_DIALOG);
        dialog.setListener((className,subjectName)->updateClass(position,className,subjectName));
    }

    // fonction ll option (EDIT)
    private void updateClass(int position, String className, String subjectName) {
        // ki t3ml EDIT tt3ml fl database zeda
        Database.UpdateClass(classItems.get(position).getCid(),className,subjectName);

        classItems.get(position).setClassName(className);
        classItems.get(position).setSubjectName(subjectName);
        classAdapter.notifyItemChanged(position);
    }


    // fonction ll option (DELETE)
    private void deleteClass(int position) {
        // ki tfs5 ttfs5 permanente ml base de donnees

        Database.deleteClass(classItems.get(position).getCid());

        classItems.remove(position);
        classAdapter.notifyItemRemoved(position);
    }
    @Override
    protected void onDestroy() {
        if (Database != null) {
            Database.close();
        }
        super.onDestroy();
    }
}