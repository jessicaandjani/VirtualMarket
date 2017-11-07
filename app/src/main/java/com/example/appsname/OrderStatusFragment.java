package com.example.appsname;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 4/5/2017.
 */
public class OrderStatusFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int order_status;
    private String order_time;
    private boolean is_success;
    private TextView notAvailableStatus, failedStatus;

    public OrderStatusFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_orderstatus_recycler, container, false);
        notAvailableStatus = (TextView) v.findViewById(R.id.not_available_status);
        failedStatus = (TextView) v.findViewById(R.id.failed_status);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.order_status_recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        setStatusByID(UrlList.ORDER_ID_URL);

        return v;
    }

    private void setStatusByID(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ORDERID", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String order_id = jsonObject.getString("id");
                            if (order_id != null) {
                                String ORDER_STATUS_URL = UrlList.ORDER_URL + order_id;
                                setOrderStatusId(ORDER_STATUS_URL);
                            } else {
                                notAvailableStatus.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", PreferencesController.getID(getActivity()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        requestQueue.add(stringRequest);
    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    private void getAllStatus(String URL, final int order_status, final String order_time){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        OrderStatusParser parser = new OrderStatusParser(response);
                        parser.parseJSON();
                        //Initialized state (ACTIVE, INACTIVE, COMPLETE)
                        String state[] = new String[OrderStatusParser.ids.length];
                        for(int i = 0; i < OrderStatusParser.ids.length; i++) {
                            state[i] = "INACTIVE";
                        }
                        //Change state of order
                        Log.d("order status", String.valueOf(order_status));
                        state[order_status-1] = "ACTIVE";
                        if(order_status > 1) {
                            for(int i = 1; i < order_status; i++) {
                                state[i-1] = "COMPLETE";
                            }
                        }
                        //Make Adapter of Order Status
                        ArrayList<OrderStatus> orderStatus = new ArrayList<OrderStatus>();
                        OrderStatus status;
                        for(int i = 0; i < OrderStatusParser.ids.length; i++) {
                            status = new OrderStatus(OrderStatusParser.ids[i], OrderStatusParser.names[i], state[i], order_time);
                            orderStatus.add(status);
                        }
                        mAdapter = new OrderStatusAdapter(orderStatus);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        requestQueue.add(stringRequest);
    }

    private void updateState(String URL,final VolleyCallback callback){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccessResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getActivity());
        requestQueue.add(stringRequest);
    }

    public void setOrderStatusId(String URL) {
        final String STATUS_URL = UrlList.STATUS_URL;
        updateState(URL, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                Log.d("RESULTSTATE", result);
                OrderParser parser = new OrderParser(result);
                parser.parseJSON();
                is_success = OrderParser.status;
                if (is_success) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    order_status = OrderParser.id;
                    order_time = OrderParser.timestamp;
                    Log.d("ORDERTIME", order_time);
                    Log.d("ORDERSTATUS", String.valueOf(order_status));
                    getAllStatus(STATUS_URL, order_status, order_time);
                } else {
                    //call textview
                    failedStatus.setText(OrderParser.statusName);
                    failedStatus.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
