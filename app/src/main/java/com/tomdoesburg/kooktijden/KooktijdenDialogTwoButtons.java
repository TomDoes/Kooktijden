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

public class KooktijdenDialogTwoButtons extends Dialog implements
        View.OnClickListener{

    ActivityCommunicator mCallback;
    private Button yesButton;
    private Button noButton;
    private TextView titleTV;
    private TextView messageTV;
    private String titleText;
    private String messageText;

    public interface ActivityCommunicator{

       public void resetDialogYesClicked();

    }

    public KooktijdenDialogTwoButtons(Context mContext, String title, String message) {
        super(mContext);

        mCallback = (ActivityCommunicator) mContext;

        //make background transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        this.titleText = title;
        this.messageText = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout_two_buttons);

        yesButton = (Button) findViewById(R.id.btn_yes);
        yesButton.setOnClickListener(this);

        noButton = (Button) findViewById(R.id.btn_no);
        noButton.setOnClickListener(this);

        titleTV = (TextView) findViewById(R.id.titleTV);
        messageTV = (TextView) findViewById(R.id.messageTV);

        //set text
        titleTV.setText(titleText);
        messageTV.setText(messageText);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                break;
            case R.id.btn_yes:
                mCallback.resetDialogYesClicked();
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
