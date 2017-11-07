package com.example.appsname;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by asus on 6/16/2017.
 */
public class FeedbackAdapter extends ArrayAdapter<Reason> {
    public ArrayList<Reason> reasonList;
    private Context mContext;

    public FeedbackAdapter(Context context, ArrayList<Reason> reason) {
        super(context, R.layout.activity_feedback_list, reason);
        this.reasonList = reason;
        this.mContext = context;
    }

    private static class ViewHolder {
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return reasonList.size();
    }

    @Override
    public Reason getItem(int position) {
        return reasonList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_feedback_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.reasons);
            convertView.setTag(viewHolder);

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    CheckBox cBox = (CheckBox) v;
                    Reason reason = (Reason) cBox.getTag();
                    reason.setSelected(cBox.isChecked());
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Reason reason = reasonList.get(position);
        Reason item = getItem(position);
        viewHolder.checkBox.setText(item.getName());
        viewHolder.checkBox.setChecked(reason.isSelected());
        viewHolder.checkBox.setTag(reason);

        return convertView;
    }
}
