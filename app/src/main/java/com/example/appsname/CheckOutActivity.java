package com.example.appsname;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by asus on 4/7/2017.
 */
public class CheckOutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView inputName, inputAddress, inputPhone;
    private TextView subtotal, total, shipping, productName, productPrice;
    private TableLayout tableLayout;
    private ImageView orderList, editIcon;
    private Button confirmBtn, backBtn;
    private LinearLayout details;
    private String totalProduct;
    private ProgressDialog progressDialog;
    private int mExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        //Toolbar Section
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Konfirmasi");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //profile section
        inputName = (TextView) findViewById(R.id.input_name);
        inputAddress = (TextView) findViewById(R.id.input_address);
        inputPhone = (TextView) findViewById(R.id.input_phone);
        getUserProfile(UrlList.USER_PROFILE_URL); //get user profile
        //edit user profile
        editIcon = (ImageView) findViewById(R.id.edit_profile);
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        //Cost Information
        details = (LinearLayout) findViewById(R.id.expand_details);
        orderList = (ImageView) findViewById(R.id.order_list);
        tableLayout=(TableLayout)findViewById(R.id.order_table);
        subtotal = (TextView) findViewById(R.id.subtotal);
        shipping = (TextView) findViewById(R.id.delivery_cost);
        total = (TextView) findViewById(R.id.total);
        if (PreferencesController.getID(CheckOutActivity.this) != null) {
            String SHIPPING_URL = UrlList.SHIPPING_URL + PreferencesController.getID(CheckOutActivity.this);
            getTarif(SHIPPING_URL);
        }
        orderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExpandedPosition == -1) {
                    mExpandedPosition = 0;
                    details.setVisibility(View.VISIBLE);
                    orderList.setImageResource(R.drawable.ic_keyboard_arrow_up);
                } else {
                    mExpandedPosition = -1;
                    details.setVisibility(View.GONE);
                    orderList.setImageResource(R.drawable.ic_keyboard_arrow_down);
                }
            }
        });

        //Back Button
        backBtn = (Button) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckOutActivity.this, ShoppingListActivity.class);
                startActivity(intent);
            }
        });

        //Add Order to Database
        confirmBtn = (Button) findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
    }

    private void confirmDialog() {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setMessage("Apakah Anda yakin untuk melanjutkan pesanan ini ?");
        confirmDialog.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        submitForm();
                    }
                });
        confirmDialog.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                });
        AlertDialog alertDialog = confirmDialog.create();
        alertDialog.show();
    }

    //submit Form
    private void submitForm() {
        // add order and shopping list to database
        if(PreferencesController.getID(CheckOutActivity.this) != null) {
            addOrder(UrlList.ADD_ORDER_URL);
        }
    }

    private void getUserProfile(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("register response", response);
                        UserParser parser = new UserParser(response);
                        parser.parseJSON();
                        inputName.setText(UserParser.name);
                        inputAddress.setText(UserParser.address);
                        inputPhone.setText(UserParser.phone_number);
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
                params.put("id", PreferencesController.getID(getApplicationContext()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getTarif(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shipping.setText(PreferencesController.formatRupiah(response));
                        setPrice(UrlList.CART_URL, Integer.valueOf(response));
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss(); //Dismiss progress Dialog
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
        //Show progress dialog
        progressDialog = new ProgressDialog(CheckOutActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void addOrder(final String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ORDER_ID", response);
                        dataOrder(UrlList.CART_URL, response);
                        Intent intentHome = new Intent(CheckOutActivity.this, MainActivity.class);
                        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentHome);
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
                params.put("total_product", totalProduct);
                params.put("customer_id", PreferencesController.getID(getApplicationContext()));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
    }

    private void dataOrder(String URL, final String orderID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ORDERLINE_ID", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String shopping_list = jsonObject.getString("shopping_list");
                            addOrderLine(UrlList.ADD_ORDERLINE_URL, shopping_list, orderID);
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
                params.put("user_id", PreferencesController.getID(CheckOutActivity.this));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
    }

    private void addOrderLine (String URL, final String dataOrder, final String orderID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CheckOutActivity.this,response,Toast.LENGTH_LONG).show();
                        removeCart(UrlList.REMOVE_CART_URL); // remove order line after checkout
                        ProductActivity.countItem = 0;
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
                params.put("order_line", dataOrder);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
    }

    public void setPrice(String URL, final Integer shippingCost) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("cart response", response);
                        ShoppingListParser parser = new ShoppingListParser(response);
                        parser.parseJSON();
                        totalProduct = String.valueOf(ShoppingListParser.ids.length);
                        for(int i = 0; i < ShoppingListParser.ids.length; i++) {
                            View tableRow = LayoutInflater.from(CheckOutActivity.this).inflate(R.layout.order_table,null,false);
                            productName  = (TextView) tableRow.findViewById(R.id.product_name);
                            productPrice = (TextView) tableRow.findViewById(R.id.product_price);
                            productName.setText(ShoppingListParser.product_names[i]);
                            String price_min = ShoppingListParser.prices_min[i];
                            String price_max = ShoppingListParser.prices_max[i];
                            productPrice.setText(PreferencesController.formatRupiahRange(price_min, price_max));
                            tableLayout.addView(tableRow);
                        }
                        subtotal.setText(ShoppingListParser.subTotal);
                        int totalMin = Integer.parseInt(ShoppingListParser.subTotalMin) + shippingCost;
                        int totalMax = Integer.parseInt(ShoppingListParser.subTotalMax) + shippingCost;
                        String totalPrice = PreferencesController.formatRupiahRange(String.valueOf(totalMin), String.valueOf(totalMax));
                        total.setText(totalPrice);
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
                params.put("user_id", PreferencesController.getID(CheckOutActivity.this));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
    }

    public void removeCart(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("cart response", response);
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
                params.put("user_id", PreferencesController.getID(CheckOutActivity.this));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(CheckOutActivity.this);
        requestQueue.add(stringRequest);
    }
}
