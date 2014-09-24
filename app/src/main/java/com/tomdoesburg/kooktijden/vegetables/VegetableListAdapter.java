package com.tomdoesburg.kooktijden.vegetables;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tomdoesburg.kooktijden.R;
import com.tomdoesburg.sqlite.MySQLiteHelper;

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
        TextView circleView;
        ImageButton timerButton;
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
        final VegetableListItem vegetableListItem = (VegetableListItem)getItem(position);
        View viewToUse = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            if (useList) {
                viewToUse = mInflater.inflate(R.layout.vegetable_list_item, null);
            } else {
                viewToUse = mInflater.inflate(R.layout.vegetable_grid_item, null);
            }
            TextView vegetableTitle = (TextView)viewToUse.findViewById(R.id.vegetableTitle);
            Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Thin.ttf");
            vegetableTitle.setTypeface(typeFace);
            holder = new ViewHolder();
            holder.vegetableName = (TextView)viewToUse.findViewById(R.id.vegetableTitle);
            holder.circleView = (TextView)viewToUse.findViewById(R.id.circleImage);
            holder.timerButton = (ImageButton)viewToUse.findViewById(R.id.list_timer_button);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        //set all information of the vegetable for this row
        String vegetableName = vegetableListItem.getVegetableName();
        holder.vegetableName.setText(vegetableName);
        holder.circleView.setText(vegetableName.substring(0, 1));
        holder.timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get time from database
                MySQLiteHelper db = new MySQLiteHelper(context);
                int timeMinutes = db.getVegetable(vegetableListItem.getId()).getCookingTimeMax();

                //return the result
                Intent intent = new Intent();
                intent.putExtra("timeSeconds",timeMinutes*60);
                ((Activity) context).setResult(Activity.RESULT_OK,intent);

                //finish the vegetable activity
                ((Activity) context).finish();
            }
        });


        int color = position % 4;
        int [] colorArray = {R.color.listcolor0, R.color.listcolor1, R.color.listcolor2, R.color.listcolor3};
        GradientDrawable circleShape = (GradientDrawable)holder.circleView.getBackground();
        circleShape.setColor(context.getResources().getColor(colorArray[color]));

        return viewToUse;
    }
}
