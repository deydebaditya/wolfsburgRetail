package com.wolfsburgsolutions.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deba on 11/26/2017.
 */

public class MainActivity extends AppCompatActivity {

    EditText name,brand,mrp,category,description,price,retailer_id;
    ToggleButton avail;
    boolean perm_grant=false;
    HashMap<String,String> headers;
    Button show_procuts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Button post_button = (Button)findViewById(R.id.post_button);
        show_procuts = (Button)findViewById(R.id.gotoListButton);
        name = (EditText)findViewById(R.id.name);
        brand = (EditText)findViewById(R.id.brand);
        mrp = (EditText)findViewById(R.id.mrp);
        category = (EditText)findViewById(R.id.category);
        description = (EditText)findViewById(R.id.description);
        price = (EditText)findViewById(R.id.price);
        retailer_id = (EditText)findViewById(R.id.retailer_id);
        avail = (ToggleButton) findViewById(R.id.availability);

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checked = avail.isChecked()?1:0;
                headers=new HashMap<>();
                headers.put("name",name.getText().toString());
                headers.put("brand",brand.getText().toString());
                headers.put("mrp",mrp.getText().toString());
                headers.put("category",category.getText().toString());
                headers.put("description",description.getText().toString());
                headers.put("availability",String.valueOf(checked));
                headers.put("price",price.getText().toString().equals("")?String.valueOf(-999):price.getText().toString());
                headers.put("retailer_id",retailer_id.getText().toString());
                headers.put("retailer_comment","");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        1);
                if(perm_grant) {
                    try {
                        RequestQueue reqQ = Volley.newRequestQueue(MainActivity.this);
                        String URL = "http://192.168.13.69/Hover/query_put.php";
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("name", name.getText().toString());
                        jsonObject.put("brand", brand.getText().toString());
                        jsonObject.put("mrp", mrp.getText().toString());
                        jsonObject.put("category", category.getText().toString());
                        jsonObject.put("description", description.getText().toString());
                        jsonObject.put("availability",String.valueOf(checked));
                        jsonObject.put("price",price.getText().toString().equals("")?String.valueOf(-999):price.getText().toString());
                        jsonObject.put("retailer_id",retailer_id.getText().toString());
                        jsonObject.put("retailer_comment","");

                        final String reqBody = jsonObject.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("RESPONSE", response);
                                try {
                                    String toast="";
                                    JSONObject jsonObj=new JSONObject(response);
                                    String connection = jsonObj.getString("conn");
                                    String insert_succ= jsonObj.getString("queryInsert");
                                    if(connection.equals("CONN_SUCCESS")){
                                        toast+="Connection to database successful!";
                                    }
                                    else{
                                        toast+="Connection to database failed!";
                                    }
                                    if(insert_succ.equals("IN_SUCC")){
                                        toast+=" Data has been successfully inserted!";
                                    }
                                    else {
                                        toast += " Insertion of data has failed!";
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

        });

        show_procuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductsListActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    perm_grant =true;
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to access Internet", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
