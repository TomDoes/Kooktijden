package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.R;

import java.util.List;

/**
 * Created by Tom on 17/8/14.
 */
public class VegetableListAdapter extends ArrayAdapter {

    private Context context;
    private boolean useList = true;

    public VegetableListAdapter(Context context, List vegetables) {
        super(context, android.R.layout.simple_list_item_1, vegetables);
        this.context = context;
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder {
        TextView vegetableName;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        VegetableListItem vegetableListItem = (VegetableListItem)getItem(position);
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            if (useList) {
                viewToUse = mInflater.inflate(R.layout.vegetable_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.vegetable_grid_item, null);
            }
            holder = new ViewHolder();
            holder.vegetableName = (TextView)viewToUse.findViewById(R.id.vegetableTitle);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.vegetableName.setText(vegetableListItem.getVegetableName());
        return viewToUse;
    }
}
