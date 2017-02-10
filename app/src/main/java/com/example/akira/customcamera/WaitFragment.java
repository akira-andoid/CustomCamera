package com.example.akira.customcamera;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class WaitFragment extends DialogFragment {

    MainActivity mainActivity;

    public static WaitFragment newInstance(String title,String message) {
        WaitFragment waitFragment = new WaitFragment();
        Bundle args = new Bundle();
        args.putString("Title",title);
        args.putString("Message",message);
        waitFragment.setArguments(args);
        return waitFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof MainActivity)) {
            throw new UnsupportedOperationException("許可されていません");
        }
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(mainActivity);
        String title = getArguments().getString("Title",null);
        String message = getArguments().getString("Message",null);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return progressDialog;
    }
}
