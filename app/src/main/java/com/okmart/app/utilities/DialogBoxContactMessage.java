package com.okmart.app.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.okmart.app.R;

public class DialogBoxContactMessage {

    private static Dialog dialogE;

    private String TAG = DialogBoxContactMessage.class.getSimpleName();

    public static void showContactMessage(Context context, EditText name, EditText email, EditText message) {

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_contact_message);

        TextView ok = dialogE.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.cancel();
                name.setText("");
                email.setText("");
                message.setText("");
            }
        });

        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();

    }

}
