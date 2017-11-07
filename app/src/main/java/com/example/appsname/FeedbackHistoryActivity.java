package com.example.appsname;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

/**
 * Created by asus on 6/19/2017.
 */
public class FeedbackHistoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private FeedbackHistoryAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView notAvailableFeedback;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_card);
        //Toolbar Section
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Riwayat Ulasan");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Feedback Section
        notAvailableFeedback = (TextView) findViewById(R.id.not_available_feedback);
        mRecyclerView = (RecyclerView) findViewById(R.id.feedback_history_card);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getOrderHistory(UrlList.FEEDBACK_HISTORY_URL + PreferencesController.getID(FeedbackHistoryActivity.this));
    }

    private void getOrderHistory(String URL) {
        final ArrayList<Order> orders = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        OrderListParser parser = new OrderListParser(response);
                        parser.parseJSON();
                        String[] ids = OrderListParser.ids;
                        String[] times = OrderListParser.times;
                        String[] prices = OrderListParser.prices;
                        for (int i = 0; i < ids.length; i++) {
                            Order order = new Order();
                            order.setOrderId(ids[i]);
                            order.setOrderTime(times[i]);
                            orders.add(order);
                        }
                        if (orders.size() != 0) {
                            notAvailableFeedback.setVisibility(View.GONE);
                            mAdapter = new FeedbackHistoryAdapter(FeedbackHistoryActivity.this, orders);
                            mRecyclerView.setAdapter(mAdapter);
                        } else {
                            notAvailableFeedback.setVisibility(View.VISIBLE);
                        }
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FeedbackHistoryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackHistoryActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(FeedbackHistoryActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }
}
