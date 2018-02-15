package com.wolfsburgsolutions.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Deba on 11/26/2017.
 */

public class CustomListAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    private static class ViewHolder{
        TextView name;
        TextView brand;
        TextView category;
        TextView description;
        Switch available;
        TextView mrp;
        TextView price;
        TextView cancelled;
    }

    public CustomListAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.list_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

    }
    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.prod_name);
            viewHolder.brand = (TextView) convertView.findViewById(R.id.prod_brand);
            viewHolder.category = (TextView) convertView.findViewById(R.id.prod_category);
            viewHolder.description = (TextView) convertView.findViewById(R.id.prod_description);
            viewHolder.available = (Switch) convertView.findViewById(R.id.prod_avail);
            viewHolder.mrp = (TextView) convertView.findViewById(R.id.prod_mrp);
            viewHolder.price = (TextView) convertView.findViewById(R.id.retailer_price);
            viewHolder.cancelled = (TextView) convertView.findViewById(R.id.mrp_cancelled);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.name.setText(dataModel.getName());
        viewHolder.brand.setText(dataModel.getBrand());
        viewHolder.category.setText(dataModel.getCategory());
        viewHolder.description.setText(dataModel.getDescription());
        viewHolder.available.setChecked(dataModel.getAvailable());
        String mrp = dataModel.getMRP();
        String price = dataModel.getPrice();
        if(price.equals("-999")){
            viewHolder.mrp.setText(mrp);
            viewHolder.price.setVisibility(View.INVISIBLE);
            viewHolder.cancelled.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.mrp.setText(mrp);
            viewHolder.cancelled.setVisibility(View.VISIBLE);
            viewHolder.price.setVisibility(View.VISIBLE);
            viewHolder.price.setText(price);
        }

        return convertView;
    }
}
