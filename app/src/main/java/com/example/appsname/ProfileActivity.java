package com.example.appsname;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 6/19/2017.
 */
public class ProfileActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private String name, address, phone, addressNote;
    private EditText inputName, inputPhone, inputAddressNote;
    private AutoCompleteTextView inputAddress;
    private Toolbar toolbar;
    private Button saveButton;
    private LinearLayout profileForm;
    private ProgressDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_PAYAKUMBUH = new LatLngBounds(
            new LatLng(-0.2749049, 100.5795769), new LatLng(-0.184086, 100.6859289));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Toolbar Section
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle("Profil Diri");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Form Section
        profileForm = (LinearLayout) findViewById(R.id.profile_layout);
        profileForm.setFocusableInTouchMode(true);
        profileForm.requestFocus();
        //get user address
        mGoogleApiClient = new GoogleApiClient.Builder(ProfileActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        inputAddress = (AutoCompleteTextView) findViewById(R.id
                .input_address);
        inputAddress.setThreshold(3);
        inputAddress.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_PAYAKUMBUH, null);
        inputAddress.setAdapter(mPlaceArrayAdapter);
        inputName = (EditText) findViewById(R.id.input_name);
        inputPhone = (EditText) findViewById(R.id.input_phone);
        inputAddressNote = (EditText) findViewById(R.id.input_address_note);
        saveButton = (Button) findViewById(R.id.save_btn);
        //get user profile
        getUserProfile(UrlList.USER_PROFILE_URL);
        // Update User to Database
        saveButton = (Button) findViewById(R.id.save_btn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile(UrlList.USER_URL);
            }
        });
    }

    private void getUserProfile(String URL) {
        Log.d("USER", "user profile");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("PROFILERES", response);
                        UserParser parser = new UserParser(response);
                        parser.parseJSON();
                        inputName.setText(UserParser.name);
                        inputAddress.setText(UserParser.address);
                        inputAddressNote.setText(UserParser.addressNote);
                        inputPhone.setText(UserParser.phone_number);
                        progressDialog.dismiss(); //Dismiss progress Dialog
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
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            address = item.description.toString();
            Log.d("ADDDDD", address);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    private void updateUserProfile(String URL) {
        name = inputName.getText().toString().trim();
        phone = inputPhone.getText().toString().trim();
        address = inputAddress.getText().toString().trim();
        addressNote = inputAddressNote.getText().toString().trim();
        Log.d("NOTE", addressNote);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("PROFILE", response);
                        Toast.makeText(ProfileActivity.this,response,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss(); //Dismiss progress Dialog
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
                params.put("id", PreferencesController.getID(getApplication()));
                params.put("name", name);
                params.put("address", address);
                params.put("address_note", addressNote);
                params.put("phone", phone);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        requestQueue.add(stringRequest);
        //show progress dialog
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }
}
