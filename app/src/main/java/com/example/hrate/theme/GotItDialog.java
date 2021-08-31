package com.example.hrate.theme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.hrate.R;

public class GotItDialog extends DialogFragment {
    int resTitle, resMessage;
    Runnable onClick;

    public GotItDialog(int resTitle, int resMessage, Runnable onClick) {
        this.resTitle = resTitle;
        this.resMessage = resMessage;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        TextView tvMessage = new TextView(getContext());
        tvMessage.setText(resMessage);
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        TypedValue typedValue = new TypedValue();
        int paddingHorizontal = getContext().getTheme().resolveAttribute(R.attr.dialogPreferredPadding,typedValue, true)?TypedValue.complexToDimensionPixelSize(typedValue.data, getContext().getResources().getDisplayMetrics()):0;
        tvMessage.setPadding(paddingHorizontal,32,paddingHorizontal,24);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(resTitle)
                .setView(tvMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_got_it, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int id) {
                        onClick.run();
                    }
                }).create();
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_radius_full_white);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.white));
                positiveButton.setBackgroundResource(R.drawable.btn_raised);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
                positiveButton.setLayoutParams(params);
                positiveButton.invalidate();
            }
        });
        return dialog;
    }
}