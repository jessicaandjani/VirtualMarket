package com.example.appsname;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by asus on 6/19/2017.
 */
public class FeedbackHistoryAdapter extends
        RecyclerView.Adapter<FeedbackHistoryAdapter.OrderViewHolder> {

    private ArrayList<Order> orderList;
    private Context mContext;

    public FeedbackHistoryAdapter(Context context, ArrayList<Order> orderList) {
        this.mContext = context;
        this.orderList = orderList;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderTime;
        Button feedbackButton;

        public OrderViewHolder(View itemView) {
            super(itemView);

            orderTime = (TextView)itemView.findViewById(R.id.order_time);
            feedbackButton = (Button)itemView.findViewById(R.id.feedback_button);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.activity_feedback_history, viewGroup, false);

        OrderViewHolder holder = new OrderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, final int i) {
        holder.orderTime.setText(orderList.get(i).getOrderTime());
        holder.feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FeedbackActivity.class);
                intent.putExtra("order_id", orderList.get(i).getOrderId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
