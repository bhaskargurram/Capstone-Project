package com.bhaskar.snapreminder.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhaskar.snapreminder.MainActivity;
import com.bhaskar.snapreminder.R;
import com.bhaskar.snapreminder.model.ReminderCards;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bhaskar on 25/5/16.
 */
public class ReminderCardsAdapter extends RecyclerView.Adapter<ReminderCardsAdapter.Cards_View_Holder> {
    Context context;
    ArrayList<ReminderCards> arrayList;

    OnItemClickListener mItemClickListener;


    public ReminderCardsAdapter(ArrayList<ReminderCards> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
        Log.d("bhaskar", "inside on create reminders card adapter" + arrayList.size());
    }

    @Override
    public Cards_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("bhaskar", "inside on create view holder");
        View root = LayoutInflater.from(context).inflate(R.layout.recycler_main_item, parent, false);
        Cards_View_Holder holder = new Cards_View_Holder(root);

        return holder;
    }

    @Override
    public void onBindViewHolder(Cards_View_Holder holder, int position) {
        Log.d("bhaskar", "on bind view holder");
        ReminderCards card = arrayList.get(position);
        String card_desc = card.getDescription();

        if (card_desc.length() > 20) {
            holder.description.setText(card_desc.substring(0, 20) + "...");
        } else {
            holder.description.setText(card_desc);
        }

        if (!card.getDate().equals("NA")) {
            String[] date_array = card.getDate().split(",");
            String year = date_array[0];
            int month = Integer.parseInt(date_array[1]);
            String d = date_array[2];
            String month_string = "";
            switch (month) {
                case 1:
                    month_string = "JAN";
                    break;
                case 2:
                    month_string = "FEB";
                    break;
                case 3:
                    month_string = "MAR";
                    break;
                case 4:
                    month_string = "APR";
                    break;
                case 5:
                    month_string = "MAY";
                    break;
                case 6:
                    month_string = "JUNE";
                    break;
                case 7:
                    month_string = "JULY";
                    break;
                case 8:
                    month_string = "AUG";
                    break;
                case 9:
                    month_string = "SEPT";
                    break;
                case 10:
                    month_string = "OCT";
                    break;
                case 11:
                    month_string = "NOV";
                    break;
                case 12:
                    month_string = "DEC";
                    break;
            }
            holder.date.setText(d + " " + month_string + " " + year);
        } else {
            holder.date.setText(card.getDate());
        }


        if (!card.getImage_path().equals("NA")) {
            File imageFile = new File(card.getImage_path());
            Log.d("bhaskar", Uri.fromFile(imageFile).toString());
            Picasso.with(context)
                    .load(imageFile)
                    .error(R.drawable.reminder_footer)
                    .into(holder.imageView);

        } else {
            Picasso.with(context)
                    .load(R.drawable.reminder_footer)
                    .error(R.drawable.reminder_footer)
                    .into(holder.imageView);

        }
        //holder.imageView.setImageBitmap(picture);


    }

    public void removeItem(int position) {
        ReminderCards card = arrayList.get(position);
        long rem_id = card.getRem_id();
        String where = RemindersContract.TasksEntry.COLUMN_REMINDER_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(rem_id)};
        context.getContentResolver().
                delete(RemindersContract.TasksEntry.CONTENT_URI, where, whereArgs);
        where = RemindersContract.RemindersEntry.COLUMN_REMINDER_ID + "=?";

        context.getContentResolver().
                delete(RemindersContract.RemindersEntry.CONTENT_URI, where, whereArgs);
        arrayList.remove(position);
        Intent myIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        myIntent.putExtra(context.getString(R.string.count), -1);
        myIntent.putExtra(context.getString(R.string.until), "NA");
        myIntent.putExtra(context.getString(R.string.id), 0);
        myIntent.putExtra(context.getString(R.string.days_array), "NA");
        myIntent.putExtra(context.getString(R.string.type), 0);
        myIntent.putExtra(context.getString(R.string.desc_extra), "NA");
        myIntent.putExtra(context.getString(R.string.interval), -1);
        PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), (int) rem_id, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Intent intent = new Intent(MainActivity.WIDGET_UPDATE_ACTION);
        context.getApplicationContext().sendBroadcast(intent);
        Log.d("bhaskar", "broadcast sent");
        this.notifyItemRemoved(position);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Cards_View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView description;
        public TextView date;
        public ImageView imageView;


        public Cards_View_Holder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date_main_card);
            description = (TextView) view.findViewById(R.id.desc_main_card);
            imageView = (ImageView) view.findViewById(R.id.image_card);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition()); //OnItemClickListener mItemClickListener;

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}