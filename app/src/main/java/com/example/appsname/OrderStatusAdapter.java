package com.example.appsname;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

/**
 * Created by asus on 4/7/2017.
 */
public class OrderStatusAdapter
        extends RecyclerView.Adapter<OrderStatusAdapter.TimeLineViewHolder> {

    private ArrayList<OrderStatus> orderStatus;
    private Context mContext;

    public static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        TextView orderStatusText;
        TextView statusTimeText;
        TimelineView timelineView;

        public TimeLineViewHolder(View itemView) {
            super(itemView);

            orderStatusText = (TextView)itemView.findViewById(R.id.order_status);
            statusTimeText = (TextView)itemView.findViewById(R.id.status_time);
            timelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        }

    }

    public OrderStatusAdapter(ArrayList<OrderStatus> orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.activity_order_status, viewGroup, false);

        TimeLineViewHolder holder = new TimeLineViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int i) {
        OrderStatus status = orderStatus.get(i);
        if(status.getOrderStatus().equals("ACTIVE")) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else if(status.getOrderStatus().equals("INACTIVE")) {
            holder.timelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else {
            holder.timelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        holder.orderStatusText.setText(orderStatus.get(i).getStatusName());
        holder.statusTimeText.setText(orderStatus.get(i).getTimeStamp());

    }

    @Override
    public int getItemCount() {
        return orderStatus.size();
    }

}
