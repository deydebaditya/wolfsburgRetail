package com.wolfsburgsolutions.myapplication;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deba on 11/26/2017.
 */

public class ProductsListActivity extends AppCompatActivity{

    ListView products_list;
    ArrayList<DataModel> dataModels;
    HashMap<String,String> headers;
    boolean perm_grant=true;
    private static CustomListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_items);

        products_list = (ListView)findViewById(R.id.products_list);
        dataModels = new ArrayList<>();

        // DOWNLOAD DATA
        headers=new HashMap<>();
        headers.put("retailer_id","debaditya");
        ActivityCompat.requestPermissions(ProductsListActivity.this,
                new String[]{Manifest.permission.INTERNET},
                1);
        if(perm_grant) {
            try {
                RequestQueue reqQ = Volley.newRequestQueue(ProductsListActivity.this);
                String URL = "http://192.168.13.69/Hover/query_req.php";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("retailer_id", "debaditya");

                final String reqBody = jsonObject.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("RESPONSE", response);
                        try {
                            String toast="";
                            JSONObject jsonObj=new JSONObject(response);
                            String connection = jsonObj.getString("conn");
                            String insert_succ= jsonObj.getString("queryGet");
                            JSONArray products = jsonObj.getJSONArray("products");
                            for(int i=0;i<products.length();i++){
                                String name = products.getJSONObject(i).getString("name");
                                String brand = products.getJSONObject(i).getString("brand");
                                String mrp = products.getJSONObject(i).getString("mrp");
                                String availability = products.getJSONObject(i).getString("availability");
                                String description = products.getJSONObject(i).getString("description");
                                String category = products.getJSONObject(i).getString("category");
                                boolean avail=false;
                                if(availability.equals("1")){
                                    avail = true;
                                }
                                String price = products.getJSONObject(i).getString("price");

                                dataModels.add(new DataModel(name,brand,category,description,avail,mrp,price));
                            }
                            adapter = new CustomListAdapter(dataModels,getApplicationContext());
                            products_list.setAdapter(adapter);

                            if(connection.equals("CONN_SUCCESS")){
                                toast+="Connection to database successful!";
                            }
                            else{
                                toast+="Connection to database failed!";
                            }
                            if(insert_succ.equals("RET_SUCC")){
                                toast+=" Data has been successfully fetched!";
                            }
                            else {
                                toast += " Fetching of data has failed!";
                            }
                            Toast.makeText(getApplicationContext(),toast,Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RESPONSE", error.toString());

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = headers;

                        return params;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "text/plain; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return reqBody == null ? null : reqBody.getBytes("utf-8");

                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", reqBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {

                            try {
                                responseString = new String(response.data, "UTF-8");
                                Log.e("Response String:",responseString);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));

                    }
                };
                reqQ.add(stringRequest);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Some Bug", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Grant Internet permission first",Toast.LENGTH_SHORT).show();
        }

    }
}
