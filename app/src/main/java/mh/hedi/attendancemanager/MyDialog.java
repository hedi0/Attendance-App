package mh.hedi.attendancemanager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MyDialog extends DialogFragment {
    public static final String CLASS_ADD_DIALOG="CreateClass" ;
    public static final String CLASS_UPDATE_DIALOG="UpdateClass" ;
    public static final String STUDENT_ADD_DIALOG="CreateStudent" ;
    public static final String STUDENT_UPDATE_DIALOG = "UpdateStudent";
    private OnClickListener listener ;

    private int id;
    private String name;
    public MyDialog(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public MyDialog() {

    }

    public interface OnClickListener {
        void onClick(String text1,String text2);
    }
    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = null ;
        if(getTag().equals(CLASS_ADD_DIALOG))dialog = getAddClassDialog();
        if(getTag().equals(STUDENT_ADD_DIALOG))dialog = getAddStudentDialog();
        if(getTag().equals(CLASS_UPDATE_DIALOG))dialog = getUpdateClassDialog();
        if(getTag().equals(STUDENT_UPDATE_DIALOG))dialog = getUpdateStudentDialog();

        // bech trod l dialog background TRANSPARENT
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    private Dialog getUpdateStudentDialog() {
        // t3ml show ll dialog.xml fou9 l activity_main.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Update Student");

        // ta5ou l id mt3 editText
        EditText id_edt = view.findViewById(R.id.edt01);
        id_edt.setHint("Student ID");

        EditText name_edt = view.findViewById(R.id.edt02);
        name_edt.setHint("Student NAME");

        // te5ou l id mt3 buttonet
        Button cancel = view.findViewById(R.id.cancel_button);
        Button add = view.findViewById(R.id.create_button);
        add.setText("Update Student");

        // champ mt3 id tji far8a
        id_edt.setText(id+"");
        // l champ twli non accessible
        id_edt.setEnabled(false);
        //champ mt3 nom tji far8a
        name_edt.setText(name);

        // ki tnzl 3l buttonet
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String identif = id_edt.getText().toString();
            String name = name_edt.getText().toString();
            listener.onClick(identif,name);
            dismiss();
        });

        return builder.create();
    }

    private Dialog getUpdateClassDialog() {
        // t3ml show ll dialog.xml fou9 l activity_main.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Update Class");

        // ta5ou l id mt3 editText
        EditText class_given = view.findViewById(R.id.edt01);
        class_given.setHint("Class Name");

        EditText subject_given = view.findViewById(R.id.edt02);
        subject_given.setHint("Subject Name");

        // te5ou l id mt3 buttonet
        Button cancel = view.findViewById(R.id.cancel_button);
        Button create = view.findViewById(R.id.create_button);
        create.setText("Update Class");

        // ki tnzl 3l buttonet
        cancel.setOnClickListener(v -> dismiss());
        create.setOnClickListener(v -> {
            String newClassName = class_given.getText().toString();
            String newSubName = subject_given.getText().toString();
            if (newClassName.isEmpty()) {
                class_given.setError("Class name cannot be empty");
                class_given.requestFocus();
                return;
            }

            if (newSubName.isEmpty()) {
                subject_given.setError("Subject name cannot be empty");
                subject_given.requestFocus();
                return;
            }
            listener.onClick(newClassName,newSubName);
            dismiss();
        });

        return builder.create();
    }

    private Dialog getAddStudentDialog() {
        // t3ml show ll xml fou9 l activity_main.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Add New Student");

        // ta5ou l id mt3 editText
        EditText id_edt = view.findViewById(R.id.edt01);
        id_edt.setHint("Student ID");

        EditText name_edt = view.findViewById(R.id.edt02);
        name_edt.setHint("Student Name");

        // te5ou l id mt3 buttonet
        Button cancel = view.findViewById(R.id.cancel_button);
        Button add = view.findViewById(R.id.create_button);
        add.setText("Add Student");

        // ki tnzl 3l buttonet
        cancel.setOnClickListener(v -> dismiss());
        add.setOnClickListener(v -> {
            String identif = id_edt.getText().toString();
            String name = name_edt.getText().toString();
            // kol mayzid student y3ml increment ll id mt3 student li b3dou
            id_edt.setText(String.valueOf(Integer.parseInt(identif)+1));
            // tfr8 l champ mt3 nom ki l id y3ml +1
            name_edt.setText("");
            listener.onClick(identif,name);
        });
        return builder.create();
    }

    private Dialog getAddClassDialog() {
        // t3ml show ll dialog.xml fou9 l activity_main.xml
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        builder.setView(view);

        TextView title = view.findViewById(R.id.titleDialog);
        title.setText("Add New Class");

        // ta5ou l id mt3 editText
        EditText class_given = view.findViewById(R.id.edt01);
        class_given.setHint("Class Name");

        EditText subject_given = view.findViewById(R.id.edt02);
        subject_given.setHint("Subject Name");

        // te5ou l id mt3 buttonet
        Button cancel = view.findViewById(R.id.cancel_button);
        Button create = view.findViewById(R.id.create_button);
        create.setText("Create Class");

        // ki tnzl 3l buttonet
        cancel.setOnClickListener(v -> dismiss());
        create.setOnClickListener(v -> {
            String className = class_given.getText().toString();
            String subName = subject_given.getText().toString();
            if (className.isEmpty()) {
                class_given.setError("Class name cannot be empty");
                class_given.requestFocus();
                return;
            }

            if (subName.isEmpty()) {
                subject_given.setError("Subject name cannot be empty");
                subject_given.requestFocus();
                return;
            }
           listener.onClick(className, subName);
            dismiss();
        });

        return builder.create();
    }
}
