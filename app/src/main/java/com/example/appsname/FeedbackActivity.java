package com.example.appsname;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 4/8/2017.
 */
public class FeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button sendButton;
    private TextView shopperName, product, orderList, price, reasonTitle;
    private RatingBar rating;
    NonScrollListView listView;
    private FeedbackAdapter adapter;
    private String orderID;
    private ProgressDialog progressDialog;
    private LinearLayout orderContainer;
    private TableLayout tableLayout;
    private int mExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        // Get Intent
        Intent intent = getIntent();
        orderID = intent.getStringExtra("order_id");
        Log.d("ORDERIDFEEBACK", orderID);
        //Toolbar Section
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ulasan");
        //Shopper Name
        shopperName = (TextView) findViewById(R.id.garendong_name);
        String GARENDONG_NAME_URL = UrlList.ORDER_URL + orderID;
        setGarendongName(GARENDONG_NAME_URL);
        //Order Line section
        tableLayout=(TableLayout)findViewById(R.id.order_table);
        orderContainer = (LinearLayout) findViewById(R.id.orderlist_container);
        price = (TextView) findViewById(R.id.total_price);
        orderList = (TextView) findViewById(R.id.orderlist_text);
        orderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpandedPosition == -1) {
                    mExpandedPosition = 0;
                    tableLayout.setVisibility(View.VISIBLE);
                } else {
                    mExpandedPosition = -1;
                    tableLayout.setVisibility(View.GONE);
                }
            }
        });
        getOrderLine(UrlList.ORDERLINE_URL + orderID);

        //Add Feedback to Database
        listView = (NonScrollListView) findViewById(R.id.reason_list);
        rating = (RatingBar) findViewById(R.id.ratingbar);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                reasonTitle = (TextView) findViewById(R.id.reason_title);
                if (rating > 0 && rating < 4) {
                    reasonTitle.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    setReasonCheckBox();
                } else {
                    reasonTitle.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                }
            }
        });
        sendFeedback();
    }

    public void setGarendongName(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        OrderParser parser = new OrderParser(response);
                        parser.parseJSON();
                        shopperName.setText(OrderParser.name);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FeedbackActivity.this, "Cek Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
        requestQueue.add(stringRequest);
    }

    public void getOrderLine(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPONSE", String.valueOf(response));
                        OrderLineParser parser = new OrderLineParser(response);
                        parser.parseJSON();
                        int total_price = 0;
                        for(int i = 0; i < OrderLineParser.ids.length; i++) {
                            View tableRow = LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.order_table,null,false);
                            total_price = total_price + Integer.parseInt(OrderLineParser.prices[i]);
                            product  = (TextView) tableRow.findViewById(R.id.product_name);
                            String productDetail = OrderLineParser.names[i] + " " + OrderLineParser.quantities[i] + " " + OrderLineParser.unit_names[i];
                            product.setText(productDetail);
                            tableLayout.addView(tableRow);
                        }
                        price.setText(" " + PreferencesController.formatRupiah(String.valueOf(total_price)));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FeedbackActivity.this, "Cek Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
        requestQueue.add(jsonArrayRequest);
    }

    public interface VolleyCallback {
        void onSuccessResponse(String result);
    }

    private void getReasonResponse(String URL, final VolleyCallback callback){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccessResponse(response);
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FeedbackActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(FeedbackActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void setReasonCheckBox() {
        String URL = UrlList.REASON_URL;
        final ArrayList<Reason> reasons = new ArrayList<>();
        getReasonResponse(URL, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                ReasonParser parser = new ReasonParser(result);
                parser.parseJSON();
                String[] ids = ReasonParser.ids;
                String[] names = ReasonParser.names;
                for (int i = 0; i < ids.length; i++) {
                    Reason reasonList = new Reason();
                    reasonList.setId(ids[i]);
                    reasonList.setName(names[i]);
                    reasonList.setSelected(false);
                    reasons.add(reasonList);
                }
                adapter = new FeedbackAdapter(getApplicationContext(), reasons);
                listView.setAdapter(adapter);
            }
        });
    }

    private void sendFeedback() {
        sendButton = (Button) findViewById(R.id.send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Reason> reasonList = adapter.reasonList;
                ArrayList selectedReason = new ArrayList();
                for (int i = 0; i < reasonList.size(); i++) {
                    Reason reason = reasonList.get(i);
                    if (reason.isSelected()) {
                        selectedReason.add(reason.getId());
                    }
                }
                if(selectedReason.size() != 0) {
                    addFeedback(selectedReason);
                }
                addRatingToOrder();
                //Back to home after finish give feedback
                Intent intentHome = new Intent(FeedbackActivity.this, MainActivity.class);
                startActivity(intentHome);
            }
        });
    }

    private void addFeedback(ArrayList selectedReason) {
        String URL = UrlList.ADD_FEEDBACK_URL;
        Gson gson = new Gson();
        final String data = gson.toJson(selectedReason); // change to json array

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                params.put("order_id", orderID);
                params.put("rating", String.valueOf((int)rating.getRating()));
                params.put("feedback", data);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
        requestQueue.add(stringRequest);

    }

    private void addRatingToOrder() {
        String URL = UrlList.ADD_RATING_URL;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(FeedbackActivity.this,response,Toast.LENGTH_LONG).show();
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
                params.put("order_id", orderID);
                params.put("rating", String.valueOf((int)rating.getRating()));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(FeedbackActivity.this);
        requestQueue.add(stringRequest);

    }
}