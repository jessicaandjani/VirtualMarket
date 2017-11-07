package com.example.appsname;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 3/29/2017.
 */
public class ShoppingListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ShoppingList> shoppingList ;
    private ArrayList<UnitList> unitList ;
    private int mExpandedPosition = -1;
    private String unit_id, unit_type, quantity, unit, subTotal, unitQuantity, unitPrice;

    private static final int TYPE_HEADER = 0, TYPE_ITEM = 1, TYPE_FOOTER = 2;
    private static Context mContex;
    private ImageLoader imageLoader;

    public ShoppingListAdapter(Context c, ArrayList<ShoppingList> shoppingList, String subTotal, ArrayList<UnitList> units) {
        this.mContex = c;
        this.shoppingList = shoppingList;
        this.unitList = units;
        this.subTotal = subTotal;
        imageLoader = AppSingleton.getInstance(c).getmImageLoader();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_HEADER;
        } else if(position > shoppingList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    //Get Unit List for Spinner in Edit
    private String[] getUnitName() { //retrieve unit name
        String names[] = new String[unitList.size()];
        for(int i = 0; i < unitList.size(); i++) {
            names[i] = unitList.get(i).getName();
        }
        return names;
    }

    private String[] getUnitId() { //retrieve unit id
        String ids[] = new String[unitList.size()];
        for(int i = 0; i < unitList.size(); i++) {
            ids[i] = unitList.get(i).getId();
        }
        return ids;
    }

    private String[] getUnitTypes() { //retrieve unit type
        String types[] = new String[unitList.size()];
        for(int i = 0; i < unitList.size(); i++) {
            types[i] = unitList.get(i).getType();
        }
        return types;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.activity_shoppinglist_header, viewGroup, false);
            return new VHHeader(view);
        } else if (viewType == TYPE_ITEM) {
            View view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.activity_shoppinglist, viewGroup, false);
            return new ViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            Log.d("FOOTER", "footer");
            View view = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(R.layout.activity_shoppinglist_footer, viewGroup, false);
            return new VHFOOTER(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        if(holder instanceof VHHeader) { // header section
            final VHHeader vHeader = (VHHeader)holder;
            vHeader.subTotal.setText("Subtotal: " + subTotal);
            vHeader.helpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder confirmDialog = new AlertDialog.Builder(mContex);
                    confirmDialog.setMessage(mContex.getString(R.string.detail_priority_info));
                    confirmDialog.setNegativeButton("Baik",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.cancel();
                                }
                            });
                    AlertDialog alertDialog = confirmDialog.create();
                    alertDialog.show();
                }
            });
        } else if (holder instanceof ViewHolder) { // body section
            final ViewHolder vItem = (ViewHolder)holder;
            final boolean isExpanded = i==mExpandedPosition;
            final ShoppingList sList = shoppingList.get(i-1);
            vItem.details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            vItem.productName.setText(sList.productName);
            unitQuantity = sList.quantity + " " + sList.unit;
            vItem.unitQuantity.setText(unitQuantity);
            unitPrice = PreferencesController.formatRupiahRange(sList.price_min, sList.price_max);
            vItem.unitPrice.setText(unitPrice);
            Log.d("image", sList.productImage);
            vItem.orderImage.setImageUrl(sList.productImage, imageLoader);
            //Spinner
            if (sList.unitType.equals("common")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContex, android.R.layout.simple_spinner_item, getUnitName());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                vItem.editUnit.setAdapter(adapter);
                vItem.editUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        unit_id = getUnitId()[position];
                        unit_type = getUnitTypes()[position];
                        final String CONVERTER_URL = UrlList.CONVERTER_URL + unit_id;
                        getConverter(CONVERTER_URL);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        //nothing to do
                    }
                });
            } else {
                String[] uncommonUnit = new String[]{sList.unit};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContex, android.R.layout.simple_spinner_item, uncommonUnit);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                vItem.editUnit.setAdapter(adapter);
                vItem.editUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String[] unit_ids = new String[]{sList.unitId};
                        unit_id = unit_ids[position];
                        String[] unit_types = new String[]{sList.unitType};
                        unit_type = unit_types[position];
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        //nothing to do
                    }
                });
            }
            //Checkbox for Priority
            vItem.isPriority.setOnCheckedChangeListener(null);
            Boolean isChecked = Boolean.valueOf(sList.getIsPriority());
            vItem.isPriority.setChecked(isChecked);
            vItem.isPriority.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheckded) {
                    String ID = sList.getID();
                    editPriority(UrlList.EDIT_PRIORITY_URL, ID, String.valueOf(isCheckded));
                }
            });
            vItem.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpandedPosition = isExpanded ? -1 : i;
                    notifyDataSetChanged();
                }
            });
            vItem.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //remove from cart in database
                    String ID = shoppingList.get(i - 1).getID();
                    removeList(UrlList.DELETE_CART_URL, ID);
                    //remove from RecyclerView
                    shoppingList.remove(i -1);
                    notifyItemRemoved(i);
                    notifyItemRangeChanged(i, shoppingList.size());
                    ProductActivity.countItem = ProductActivity.countItem - 1;
                }
            });
            vItem.editQuantityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unit = vItem.editUnit.getSelectedItem().toString();
                    quantity = vItem.editQuantity.getText().toString().trim();
                    if(!validateQuantity(vItem.editQuantity, unit)) { //not valid
                        return;
                    } else { //valid
                        String ID = sList.getID();
                        String selected_unit_id = unit_id;
                        int total_quantity = 0;
                        if(unit_type.equals("common")) {
                            total_quantity = Integer.parseInt(quantity) * ConverterParser.in_gram;
                        } else if (unit_type.equals("uncommon")) {
                            total_quantity = Integer.parseInt(quantity);
                        }
                        int quantity_now = Integer.parseInt(sList.quantity);
                        int price_min_now = Integer.parseInt(sList.price_min);
                        int price_max_now = Integer.parseInt(sList.price_max);
                        int price_min = getPriceMin(total_quantity, quantity_now, price_min_now);
                        int price_max = getPriceMax(total_quantity, quantity_now, price_max_now);
                        editList(UrlList.EDIT_CART_URL, ID, quantity, unit, selected_unit_id,
                                String.valueOf(price_min), String.valueOf(price_max)); //update database
                        //update view in recyclerview
                        sList.quantity = quantity;
                        sList.unit = unit;
                        sList.price_min = String.valueOf(price_min);
                        sList.price_max = String.valueOf(price_max);
                        notifyItemChanged(i);
                        vItem.editQuantity.setText(null); //set unit edit text empty
                    }
                }
            });
        } else if(holder instanceof VHFOOTER) { //bottom section
            VHFOOTER vFotter = (VHFOOTER) holder;
            vFotter.shopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentShop = new Intent(mContex, MainActivity.class);
                    mContex.startActivity(intentShop);
                }
            });
            vFotter.checkoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentCheckOut = new Intent(mContex, CheckOutActivity.class);
                    mContex.startActivity(intentCheckOut);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size()+2;
    }

    private int getPriceMin(int quantity, int default_quantity, int priceMin) {
        return (quantity * priceMin)/default_quantity;
    }

    private int getPriceMax(int quantity, int default_quantity, int priceMax) {
        return (quantity * priceMax)/default_quantity;
    }

    public void removeList(String URL, final String ID) {
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
                params.put("id", ID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContex);
        requestQueue.add(stringRequest);
    }

    public void editList(String URL, final String ID, final String quantity, final String unit_name, final String unit_id,
                         final String price_min, final String price_max) {
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
                params.put("id", ID);
                params.put("quantity", quantity);
                params.put("unit_id", unit_id);
                params.put("unit_name", unit_name);
                params.put("price_min", price_min);
                params.put("price_max", price_max);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContex);
        requestQueue.add(stringRequest);
    }

    public void editPriority(String URL, final String ID, final String is_priority) {
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
                params.put("id", ID);
                params.put("is_priority", is_priority);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContex);
        requestQueue.add(stringRequest);
    }

    private void getConverter(String URL){
        StringRequest stringRequest = new StringRequest(URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ConverterParser parser = new ConverterParser(response);
                        parser.parseJSON();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContex, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mContex);
        requestQueue.add(stringRequest);
    }

    // Validation Section
    private boolean validateQuantity(EditText inputQuantity, String inputUnit) {
        if (inputQuantity.getText().toString().trim().isEmpty()) {
            inputQuantity.setError(mContex.getString(R.string.err_msg_quantity));
            requestFocus(inputQuantity);
            return false;
        }

        int input_quantity = Integer.parseInt(inputQuantity.getText().toString().trim());
        if (unit.equals("gram")) {
            if ((input_quantity < 100) || (input_quantity > 5000)) {
                inputQuantity.setError(mContex.getString(R.string.err_msq_valid_gram));
                requestFocus(inputQuantity);
                return false;
            }
        } else if (unit.equals("kilogram")) {
            if ((input_quantity < 1) || (input_quantity > 5)) {
                inputQuantity.setError(mContex.getString(R.string.err_msq_valid_kilogram));
                requestFocus(inputQuantity);
                return false;
            }
        } else if (unit.equals("ons")) {
            if ((input_quantity < 1) || (input_quantity > 50)) {
                inputQuantity.setError(mContex.getString(R.string.err_msq_valid_ons));
                requestFocus(inputQuantity);
                return false;
            }
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) mContex).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView unitQuantity;
        TextView unitPrice;
        NetworkImageView orderImage;
        Button editButton, deleteButton, editQuantityBtn;
        CheckBox isPriority;
        LinearLayout details;
        EditText editQuantity;
        Spinner editUnit;

        public ViewHolder(View itemView) {
            super(itemView);

            productName = (TextView)itemView.findViewById(R.id.productName);
            unitQuantity = (TextView)itemView.findViewById(R.id.unitQuantity);
            unitPrice = (TextView)itemView.findViewById(R.id.unitPrice);
            orderImage = (NetworkImageView)itemView.findViewById(R.id.orderImage);
            editButton = (Button)itemView.findViewById(R.id.edit);
            deleteButton = (Button)itemView.findViewById(R.id.delete);
            editQuantityBtn = (Button)itemView.findViewById(R.id.edit_quantity);
            details = (LinearLayout)itemView.findViewById(R.id.details);
            isPriority = (CheckBox)itemView.findViewById(R.id.priority);
            editQuantity = (EditText)itemView.findViewById(R.id.quantity);
            editUnit = (Spinner) itemView.findViewById(R.id.mass_list);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder{
        TextView subTotal;
        ImageButton helpButton;

        public VHHeader(View itemView) {
            super(itemView);

            this.subTotal = (TextView)itemView.findViewById(R.id.subtotal);
            this.helpButton = (ImageButton)itemView.findViewById(R.id.ic_help);
        }
    }

    class VHFOOTER extends RecyclerView.ViewHolder{
        Button shopButton, checkoutButton;

        public VHFOOTER(View itemView) {
            super(itemView);

            this.shopButton = (Button)itemView.findViewById(R.id.shop_button);
            this.checkoutButton = (Button)itemView.findViewById(R.id.check_out_button);
        }
    }
}
