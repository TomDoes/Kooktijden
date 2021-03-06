package com.tomdoesburg.kooktijden;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by FrankD on 21-2-2015.
 * Aanroepen doe je zo vanuit een activity:
   KooktijdenDialog kDialog = new KooktijdenDialog(context, "timer klaar", "bericht");
   kDialog.show();
 */

public class KooktijdenDialog extends Dialog implements
        android.view.View.OnClickListener{

    private Button closeButton;
    private TextView titleTV;
    private TextView messageTV;
    private String titleText;
    private String messageText;

    public KooktijdenDialog(Context mContext, String title, String message) {
        super(mContext);
        this.setCanceledOnTouchOutside(false);
        //make background transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.titleText = title;
        this.messageText = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        closeButton = (Button) findViewById(R.id.btn_close);
        closeButton.setOnClickListener(this);

        titleTV = (TextView) findViewById(R.id.titleTV);
        messageTV = (TextView) findViewById(R.id.messageTV);

        //set text
        titleTV.setText(titleText);
        messageTV.setText(messageText);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
