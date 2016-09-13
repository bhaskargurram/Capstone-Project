package com.bhaskar.snapreminder.model;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.bhaskar.snapreminder.R;

/**
 * Created by bhaskar on 24/5/16.
 */
public class Tasks_View_Holder extends RecyclerView.ViewHolder {
    public CheckBox checkBox;
    public EditText editText;
    public ImageView imageView;

    public Tasks_View_Holder(View view) {
        super(view);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        //editText = (EditText) view.findViewById(R.id.edit_task_text);
        imageView = (ImageView) view.findViewById(R.id.image_cancel_task);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
           //     list.get(position).setTaskname(s.toString());
             //   notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // list.remove(position);
               // notifyDataSetChanged();
            }
        });
    }
}
