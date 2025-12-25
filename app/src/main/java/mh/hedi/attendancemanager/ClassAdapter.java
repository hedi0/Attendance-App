package mh.hedi.attendancemanager;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    ArrayList<ClassItem> classItems;
    Context context;

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnMenuItemClickListener onMenuItemClickListener;
    public interface OnMenuItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.onMenuItemClickListener = listener;
    }

    public ClassAdapter(Context context, ArrayList<ClassItem> classItems) {
        this.classItems = classItems;
        this.context = context;
    }

    // implements View.OnCreateContextMenuListener : ttzad ki t5dm 3l faza mt3 long Press
    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView subjectName;
        Context context;

        public ClassViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener,
                               OnMenuItemClickListener menuListener) {
            super(itemView);

            // ll icon mt3 EDIT w DELETE
            context = itemView.getContext();

            className = itemView.findViewById(R.id.class_nom);
            subjectName = itemView.findViewById(R.id.subject_nom);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onClick(getAdapterPosition());
                }
            });

            // ki to93od nazel 3l classe yetla3lek 2 options EDIT , DELETE
            itemView.setOnLongClickListener(v -> {
                if (menuListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    showPopupMenu(v, getAdapterPosition(), menuListener);
                }
                return true;
            });
        }

        // l menu mt3 (Long Press) Options
        private void showPopupMenu(View anchor, final int position, OnMenuItemClickListener listener) {
            PopupMenu popupMenu = new PopupMenu(context, anchor);

            // tzid les options mt3 long Press
            // Add edit option with icon
            MenuItem edit = popupMenu.getMenu().add(0, 0, 0, "EDIT");
            edit.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_edit));

            // Add delete option with icon
            MenuItem delete = popupMenu.getMenu().add(0, 1, 0, "DELETE");
            delete.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_delete));

            // Force icons to show using reflection
            try {
                Field[] fields = popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceShowIcon = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceShowIcon.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 0: // EDIT
                        listener.onEditClick(position);
                        return true;
                    case 1: // DELETE
                        listener.onDeleteClick(position);
                        return true;
                    default:
                        return false;
                }
            });

            popupMenu.show();
        }
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false);
        return new ClassViewHolder(itemView, onItemClickListener, onMenuItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        holder.className.setText(classItems.get(position).getClassName());
        holder.subjectName.setText(classItems.get(position).getSubjectName());
    }

    @Override
    public int getItemCount() {
        return classItems.size();
    }
}