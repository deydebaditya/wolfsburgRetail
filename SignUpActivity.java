package com.wolfsburgsolutions.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;

/**
 * Created by Abhishek on 12/1/2017.
 */


//TODO: Upload pictures and COMPLETE SIGNUP AND LOGIN!

public class SignUpActivity extends AppCompatActivity {

    EditText user_id, password, enterprise_name, proprieter, aadhar_num, mob_num, address_line1, area,
            city, state, shop_category;
    Button shop_act, profile_pic, sign_up;

    byte[] encodedShopAct, encodedProfilePic;

    int PICK_IMAGE_REQUEST_PROFILE_PIC = 1;
    int PICK_IMAGE_REQUEST_SHOPACT_PIC = 2;

    boolean SHOP_ACT_SET = false;
    boolean PROFILE_PICTURE_SET = false;
    boolean done;

    static String latitude="", longitude="";

    String hashedPassword = "";

    HashMap<String, String> headers,imageHeaders;

    Bitmap shopActBitmap, profilePictureBitmap;

    static final int SHOP_ACT = 403;
    static final int PROFILE_PIC = 405;

    static Uri shopActUri, profilePicUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        user_id = (EditText) findViewById(R.id.user_id_signup);
        password = (EditText) findViewById(R.id.password);
        enterprise_name = (EditText) findViewById(R.id.enterprise_name);
        proprieter = (EditText) findViewById(R.id.proprieter);
        aadhar_num = (EditText) findViewById(R.id.aadhar_no);
        mob_num = (EditText) findViewById(R.id.mobile_no);
        address_line1 = (EditText) findViewById(R.id.address);
        area = (EditText) findViewById(R.id.area);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        shop_category = (EditText) findViewById(R.id.shop_category);
        shop_act = (Button) findViewById(R.id.shop_act);
        profile_pic = (Button) findViewById(R.id.profile);
        sign_up = (Button) findViewById(R.id.sign_up);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new myLocationListener();
        if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(SignUpActivity.this)
                    .setTitle("Allow Location?")
                    .setMessage("Allow Location feature for Sign up")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SignUpActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
                        }
                    })
                    .create()
                    .show();
            return;
        }
        else{
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

        //Request STORAGE permissions

        if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Storage perm","Asking for permissions in if block");
            new AlertDialog.Builder(SignUpActivity.this)
                    .setTitle("Allow Storage permissions?")
                    .setMessage("Allow for Sign Up")
                    .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SignUpActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},99);
                            ActivityCompat.requestPermissions(SignUpActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},99);
                        }
                    })
                    .create()
                    .show();
        }
        else{
            Log.i("Storage perm","Asking for permissions in else block");
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},99);
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},99);
        }

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_PROFILE_PIC);
            }
        });

        shop_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_SHOPACT_PIC);
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PasswordHasher hashMyPass = new PasswordHasher(password.getText().toString(), 12);
                hashedPassword = hashMyPass.hashPassword();

                if (SHOP_ACT_SET && PROFILE_PICTURE_SET) {
                    headers = new HashMap<>();

                    // PASSWORD ENCRYPTION //

                    // PASSWORD ENCRYPTION BY RSA DISCARDED //
                    //TODO : Think of a better way than hashing

                    //BUILDING HEADERS//
                    headers.put("user_id", user_id.getText().toString());
                    headers.put("password", hashedPassword);
                    headers.put("enterprise_name", enterprise_name.getText().toString());
                    headers.put("shop_category", shop_category.getText().toString());
                    headers.put("proprieter", proprieter.getText().toString());
                    headers.put("aadhar_num", aadhar_num.getText().toString());
                    headers.put("mob_num", mob_num.getText().toString());
                    //headers.put("shop_act_license_image", shop_act_license);
                    headers.put("address_line_1", address_line1.getText().toString());
                    headers.put("address_line_2", area.getText().toString());
                    headers.put("city", city.getText().toString());
                    headers.put("state", state.getText().toString());

                    Toast locationToast = null;
                    do{
                        if(locationToast == null){
                            locationToast = Toast.makeText(getApplicationContext(),"Obtaining Location!",Toast.LENGTH_LONG);
                            Log.e("Location","Obtaining location");
                        }
                    }while(latitude=="" && longitude=="");
                    locationToast.cancel();
                    Log.e("Location","Obtained location");
                    headers.put("gps_lat",latitude);
                    headers.put("gps_long",longitude);
                    //headers.put("profile_picture",prof_pic);

                    //HEADERS BUILD COMPLETE//
                    boolean perm_grant = true;
                    if(perm_grant) {
                        try {
                            RequestQueue reqQ = Volley.newRequestQueue(SignUpActivity.this);
                            String URL = "http:/ec2-18-216-157-229.us-east-2.compute.amazonaws.com/Hover/query_signup.php";
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("user_id", user_id.getText().toString());
                            jsonObject.put("password", hashedPassword);
                            jsonObject.put("enterprise_name", enterprise_name.getText().toString());
                            jsonObject.put("shop_category", shop_category.getText().toString());
                            jsonObject.put("proprieter", proprieter.getText().toString());
                            jsonObject.put("aadhar_num", aadhar_num.getText().toString());
                            jsonObject.put("mob_num", mob_num.getText().toString());
                            //jsonObject.put("shop_act_license_image", shop_act_license);
                            jsonObject.put("address_line_1", address_line1.getText().toString());
                            jsonObject.put("address_line_2", area.getText().toString());
                            jsonObject.put("city", city.getText().toString());
                            jsonObject.put("state", state.getText().toString());
                            jsonObject.put("gps_lat",latitude);
                            jsonObject.put("gps_long",longitude);
                            //jsonObject.put("profile_picture",prof_pic);

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

                        //PHOTOS UPLOAD
                        ImageMultipartUploader newUploader = new ImageMultipartUploader(ImageUploadConstants.UPLOAD_URL_SHOPACT,user_id.getText().toString()+".jpeg",getImagePath(shopActUri),shopActBitmap,SignUpActivity.this);
                        newUploader.startImageUpload();
                        newUploader = new ImageMultipartUploader(ImageUploadConstants.UPLOAD_URL_PROPIC,user_id.getText().toString()+".jpeg",getImagePath(profilePicUri),profilePictureBitmap,SignUpActivity.this);
                        newUploader.startImageUpload();
                        new UpdateImageUrlInDatabase().updateShopAct(user_id.getText().toString());
                        new UpdateImageUrlInDatabase().updateProfilePic(user_id.getText().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Grant Internet permission first",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(SHOP_ACT_SET){
                    headers = new HashMap<>();

                    // PASSWORD ENCRYPTION //
                    if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle("Allow Storage permissions?")
                                .setMessage("Allow for Sign Up")
                                .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},99);
                                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},99);
                                    }
                                })
                                .create()
                                .show();
                    }
                    else{
                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},99);
                        ActivityCompat.requestPermissions(SignUpActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},99);
                    }


                    // PASSWORD ENCRYPTION BY RSA DISCARDED //

                    //TODO : Think of a better way than hashing

                    //BUILDING HEADERS//
                    headers.put("user_id", user_id.getText().toString());
                    headers.put("password", hashedPassword);
                    headers.put("enterprise_name", enterprise_name.getText().toString());
                    headers.put("shop_category", shop_category.getText().toString());
                    headers.put("proprieter", proprieter.getText().toString());
                    headers.put("aadhar_num", aadhar_num.getText().toString());
                    headers.put("mob_num", mob_num.getText().toString());
                    //headers.put("shop_act_license_image", shop_act_license);
                    headers.put("address_line_1", address_line1.getText().toString());
                    headers.put("address_line_2", area.getText().toString());
                    headers.put("city", city.getText().toString());
                    headers.put("state", state.getText().toString());

                    Toast locationToast = null;
                    do{
                        if(locationToast == null){
                            locationToast = Toast.makeText(getApplicationContext(),"Obtaining Location!",Toast.LENGTH_LONG);
                            Log.e("Location","Obtaining location");
                        }
                    }while(latitude=="" && longitude=="");
                    locationToast.cancel();
                    Log.e("Location","Obtained location");
                    headers.put("gps_lat",latitude);
                    headers.put("gps_long",longitude);

                    //HEADERS BUILD COMPLETE//
                    boolean perm_grant = true;
                    if(perm_grant) {
                        try {
                            RequestQueue reqQ = Volley.newRequestQueue(SignUpActivity.this);
                            String URL = "http://ec2-18-216-157-229.us-east-2.compute.amazonaws.com/Hover/query_signup.php";
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("user_id", user_id.getText().toString());
                            jsonObject.put("password", hashedPassword);
                            jsonObject.put("enterprise_name", enterprise_name.getText().toString());
                            jsonObject.put("shop_category", shop_category.getText().toString());
                            jsonObject.put("proprieter", proprieter.getText().toString());
                            jsonObject.put("aadhar_num", aadhar_num.getText().toString());
                            jsonObject.put("mob_num", mob_num.getText().toString());
                            //jsonObject.put("shop_act_license_image", shop_act_license);
                            jsonObject.put("address_line_1", address_line1.getText().toString());
                            jsonObject.put("address_line_2", area.getText().toString());
                            jsonObject.put("city", city.getText().toString());
                            jsonObject.put("state", state.getText().toString());
                            jsonObject.put("gps_lat",latitude);
                            jsonObject.put("gps_long",longitude);
                            //jsonObject.put("profile_picture","");

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

                        //PHOTO UPLOAD
                        ImageMultipartUploader newUploader = new ImageMultipartUploader(ImageUploadConstants.UPLOAD_URL_SHOPACT,user_id.getText().toString()+".jpeg",getImagePath(shopActUri),shopActBitmap,SignUpActivity.this);
                        newUploader.startImageUpload();
                        new UpdateImageUrlInDatabase().updateShopAct(user_id.getText().toString());
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Grant Internet permission first",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Upload Shop Act picture!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String doc_id = cursor.getString(0);
        doc_id = doc_id.substring(doc_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,MediaStore.Images.Media._ID + " = ? ",new String[]{doc_id},null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST_PROFILE_PIC && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            shopActUri = data.getData();

            try{
                profilePictureBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), shopActUri); //can be set as an ImageView

                PROFILE_PICTURE_SET = true;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        if(requestCode == PICK_IMAGE_REQUEST_SHOPACT_PIC && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            profilePicUri = data.getData();

            try{
                shopActBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profilePicUri); //can be set as an ImageView

                SHOP_ACT_SET = true;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    // DATABASE UPDATE FOR IMAGES //
    class UpdateImageUrlInDatabase{

        private boolean updateShopAct(String retailer_id){
            HashMap<String,String> headers_image = new HashMap<>();
            headers_image.put("retailer_id",retailer_id);
            done = false;
            try {
                RequestQueue reqQ = Volley.newRequestQueue(SignUpActivity.this);
                String URL = "http://ec2-18-216-157-229.us-east-2.compute.amazonaws.com/Hover/save_shop_act_db.php";
                JSONObject jsonObject_image = new JSONObject();
                jsonObject_image.put("retailer_id",retailer_id);

                final String reqBody = jsonObject_image.toString();

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
                                Log.e("DB_SAVE","CONN_SUCESS");
                                toast+="Connection to database successful!";
                            }
                            else{
                                Log.e("DB_SAVE","CONN_FAIL");
                                toast+="Connection to database failed!";
                            }
                            if(insert_succ.equals("IN_SUCC")){
                                Log.e("DB_SAVE","IN_SUCESS");
                                toast+=" Data has been successfully inserted!";
                                done = true;
                            }
                            else {
                                Log.e("DB_SAVE","IN_FAIL");
                                toast += " Insertion of data has failed!";
                                done = false;
                            }
                            Toast.makeText(SignUpActivity.this,toast,Toast.LENGTH_SHORT).show();

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
                        Map<String, String>  params = imageHeaders;

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
            return done;
        }

        private boolean updateProfilePic(String retailer_id){
            HashMap<String,String> headers = new HashMap<>();
            headers.put("retailer_id",retailer_id);
            done = false;
            try {
                RequestQueue reqQ = Volley.newRequestQueue(SignUpActivity.this);
                String URL = "http://ec2-18-216-157-229.us-east-2.compute.amazonaws.com/Hover/save_profile_pic_db.php";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("retailer_id",retailer_id);

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
                                done = true;
                            }
                            else {
                                toast += " Insertion of data has failed!";
                                done = false;
                            }
                            Toast.makeText(SignUpActivity.this,toast,Toast.LENGTH_SHORT).show();

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
                        Map<String, String>  params = imageHeaders;

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
            return done;
        }
    }
}
class myLocationListener implements LocationListener{
    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            double lat = location.getLatitude();
            double longi = location.getLongitude();
            SignUpActivity.latitude = String.valueOf(lat);
            SignUpActivity.longitude = String.valueOf(longi);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}