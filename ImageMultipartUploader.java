package com.wolfsburgsolutions.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.request.JsonObjectRequest;
//import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by Debaditya on 12/14/2017.
 */

public class ImageMultipartUploader {

    private String url;
    private String image_name;
    private String image;
    private Context baseContext;
    private Bitmap imageBitmap;

    ImageMultipartUploader(String url, String image_name, String image, Bitmap imageBitmap, Context baseContext){
        this.url = url;
        this.image_name = image_name;
        this.image = image;
        this.baseContext = baseContext;
        this.imageBitmap = imageBitmap;
    }

    /* NOT USED CURRENTLY

    protected void uploadImage(){

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response_Image",response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(baseContext, "JSON Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(baseContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        smr.addFile(image_name,image);
        ApplicationHelper.getInstance().addToRequestQueue(smr);

    }

    */

    protected void startImageUpload(){

        final JSONObject jsonObject = new JSONObject();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try{
            jsonObject.put("name",image_name);
            jsonObject.put("image",encodedImage);
        } catch(JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response",response.toString());
                        try{
                            if(response.get("uploadDone").toString().equals("UPLOAD_SUCC"))
                                Toast.makeText(baseContext,"Image Uploaded successfully!",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(baseContext,"Image Upload failed!",Toast.LENGTH_SHORT).show();
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response",error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(baseContext).add(jsonObjectRequest);

    }

}
