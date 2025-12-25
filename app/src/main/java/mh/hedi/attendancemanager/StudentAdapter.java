package mh.hedi.attendancemanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    ArrayList<StudentItem> studentItems;
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

    public StudentAdapter(Context context, ArrayList<StudentItem> studentItems) {
        this.studentItems = studentItems;
        this.context = context;
    }

    //View.OnCreateContextMenuListener : tzidha ki bch t3ml (LONG PRESS) options
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView identif;
        TextView name;
        TextView status;
        CardView cardView;
        Context context;

        public StudentViewHolder(@NonNull View itemView, OnItemClickListener clickListener,
                                 OnMenuItemClickListener menuListener) {
            super(itemView);
            context = itemView.getContext();
            identif = itemView.findViewById(R.id.identifiant);
            name = itemView.findViewById(R.id.nom);
            status = itemView.findViewById(R.id.status);
            cardView = itemView.findViewById(R.id.cardview);

            // bch tzid l status(absent,present)
            itemView.setOnClickListener(v -> {
                if (clickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    clickListener.onClick(getAdapterPosition());
                }
            });

            //Long Press Options
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

            // l menu mt3 (Long Press) Options
            MenuItem edit = popupMenu.getMenu().add(0, 0, 0, "Edit");
            edit.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_edit));

            MenuItem delete = popupMenu.getMenu().add(0, 1, 0, "Delete");
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
                    case 0: // Edit
                        listener.onEditClick(position);
                        return true;
                    case 1: // Delete
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
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(itemView, onItemClickListener, onMenuItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        // lehna lwech 3mlna (+"") 3la 5atar hiya String w e7na 3mlinha INT
        holder.identif.setText(studentItems.get(position).getId() + "");
        holder.name.setText(studentItems.get(position).getName());
        holder.status.setText(studentItems.get(position).getStatus());
        // t7ot l couleur "A" -> red , "P" -> green
        holder.cardView.setCardBackgroundColor(getColor(position));
    }

    // t7ot l Color ll Absent w Present
    private int getColor(int position) {
        String status = studentItems.get(position).getStatus();
        if (status.equals("P"))
            return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.present)));
        else if (status.equals("A"))
            return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.absent)));

        return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.normal)));
    }

    @Override
    public int getItemCount() {
        return studentItems.size();
    }
}