package com.okmart.app.utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.R;
import com.okmart.app.adapters.StateListAdapter;
import com.okmart.app.model.StateListModel;

import java.util.List;

public class DialogBoxState {

    private StateListAdapter stateListAdapter;

    private RecyclerView recyclerView;

    private static Dialog dialogE;

    private String TAG = DialogBoxState.class.getSimpleName();

    public void state(final Context context, final TextView stateName, List<StateListModel> stateLists, String type) {

        dialogE = new Dialog(context);
        dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogE.setCancelable(false);
        dialogE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogE.setContentView(R.layout.dialog_state);

        recyclerView = dialogE.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        stateListAdapter = new StateListAdapter(context, stateLists, stateName, type);
        recyclerView.setAdapter(stateListAdapter);

        if (dialogE != null && dialogE.isShowing())
            return;
        dialogE.show();
        
    }

    public static void hideDialog(Context context) {
        if (dialogE != null)
            dialogE.dismiss();
    }

}