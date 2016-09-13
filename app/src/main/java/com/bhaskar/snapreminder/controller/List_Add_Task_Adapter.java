package com.bhaskar.snapreminder.controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhaskar.snapreminder.R;
import com.bhaskar.snapreminder.model.Tasks;

import java.util.ArrayList;

/**
 * Created by bhaskar on 24/5/16.
 */
public class List_Add_Task_Adapter extends RecyclerView.Adapter<List_Add_Task_Adapter.Tasks_View_Holder> implements OnCheckedChangeListener {
    ArrayList<Tasks> list;
    Context context;
    long reminder_no;

    public List_Add_Task_Adapter(ArrayList<Tasks> list, Context context, long reminder_no) {
        this.list = list;
        this.context = context;
        this.reminder_no = reminder_no;
        Log.d("bhaskar", "list size=" + list.size());
    }


    public void add() {
        int task_number;
        if (list.isEmpty()) {
            task_number = 1;
        } else {
            task_number = list.get(list.size() - 1).getTasknumber() + 1;
        }
        Tasks data = new Tasks("", false, task_number);
        list.add(data);

        Log.d("bhaskar", "adding data" + list.size());
        ContentValues cv = new ContentValues();
        cv.put(RemindersContract.TasksEntry.COLUMN_REMINDER_ID, reminder_no);
        cv.put(RemindersContract.TasksEntry.COLUMN_SUBTASK_NO, task_number);
        cv.put(RemindersContract.TasksEntry.COLUMN_TASK_NAME, data.getTaskname());
        cv.put(RemindersContract.TasksEntry.COLUMN_SELECTED, data.getChecked() ? 1 : 0);

        context.getContentResolver().insert(RemindersContract.TasksEntry.CONTENT_URI, cv);
        this.notifyItemInserted(list.size() - 1);
    }

    public void changeList(ArrayList<Tasks> data) {
        list = data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Tasks_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.tasks_item, parent, false);
        Tasks_View_Holder holder = new Tasks_View_Holder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(Tasks_View_Holder holder, int position) {

        final int position_final = position;
        Log.d("bhaskar", "inside bind view holder list add taskadapter" + position);
        Tasks task = list.get(position);
        holder.checkBox.setChecked(task.getChecked());
        if (holder.checkBox.isChecked()) {
            holder.taskname.setPaintFlags(holder.taskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskname.setPaintFlags(0);

        }
        holder.taskname.setText(task.getTaskname());
    }

    public void updateTask(int subtask, String taskname, boolean selected) {
        String where = RemindersContract.TasksEntry.COLUMN_SUBTASK_NO + "=? AND " + RemindersContract.TasksEntry.COLUMN_REMINDER_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(subtask), String.valueOf(reminder_no)};
        ContentValues cv = new ContentValues();
        cv.put(RemindersContract.TasksEntry.COLUMN_REMINDER_ID, reminder_no);
        cv.put(RemindersContract.TasksEntry.COLUMN_SUBTASK_NO, subtask);
        cv.put(RemindersContract.TasksEntry.COLUMN_TASK_NAME, taskname);
        cv.put(RemindersContract.TasksEntry.COLUMN_SELECTED, selected ? 1 : 0);

        context.getContentResolver().update(RemindersContract.TasksEntry.CONTENT_URI, cv, where, whereArgs);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public class Tasks_View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CheckBox checkBox;
        public TextView taskname;
        public ImageView imageView;


        public Tasks_View_Holder(View view) {
            super(view);

            Log.d("bhaskar", "create new Tasks_view_holder");
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            taskname = (TextView) view.findViewById(R.id.task_text);
            imageView = (ImageView) view.findViewById(R.id.image_cancel_task);

            taskname.setOnClickListener(this);
            checkBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Tasks task = list.get(getAdapterPosition());
                                                task.setChecked(checkBox.isChecked());
                                                if (checkBox.isChecked()) {
                                                    Log.d("bhaskar", "checkbox checked" + getAdapterPosition());
                                                    taskname.setPaintFlags(taskname.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                                } else {
                                                    taskname.setPaintFlags(0);
                                                }
                                                updateTask(task.getTasknumber(), task.getTaskname(), task.getChecked());


                                            }
                                        }

            );
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("bhaskar", "removed" + getAdapterPosition());
                    Tasks task = list.get(getAdapterPosition());
                    int task_no = task.getTasknumber();
                    list.remove(getAdapterPosition());
                    String where = RemindersContract.TasksEntry.COLUMN_SUBTASK_NO + "=? AND " + RemindersContract.RemindersEntry.COLUMN_REMINDER_ID + "=?";
                    String[] whereArgs = new String[]{String.valueOf(task_no), String.valueOf(reminder_no)};
                    context.getContentResolver().delete(RemindersContract.TasksEntry.CONTENT_URI, where, whereArgs);
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            notifyDataSetChanged();
                        }
                    };

                    handler.post(r);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.d("bhaskar", "inside taskname on click");
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.update_task_message));

            final EditText input = new EditText(context);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);


            builder.setPositiveButton(context.getString(R.string.positive_message), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Tasks task = list.get(getAdapterPosition());
                    String taskname = input.getText().toString();
                    task.setTaskname(taskname);
                    list.set(getAdapterPosition(), task);
                    updateTask(task.getTasknumber(), taskname, task.getChecked());
                    Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            Log.d("bhaskar", "notify data set changed in builder");
                            notifyItemChanged(getAdapterPosition());
                        }
                    };

                    handler.post(r);
                }
            });
            builder.setNegativeButton(context.getString(R.string.negative_message), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }
}



