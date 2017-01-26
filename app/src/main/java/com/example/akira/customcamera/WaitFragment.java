package com.example.akira.customcamera;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WaitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WaitFragment extends DialogFragment {

    static WaitFragment newInstance(int num) {
        // Required empty public constructor
        WaitFragment wf = new WaitFragment();
        Bundle args = new Bundle();
        args.putInt("num",num);
        wf.setArguments(args);
        return wf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mNum = savedInstanceState.getInt("num");
        int style = DialogFragment.STYLE_NORMAL,theme = android.R.style.Theme_Holo;
        switch (mNum) {
            case 1: style = DialogFragment.STYLE_NO_FRAME; break;
            case 2: style = DialogFragment.STYLE_NO_TITLE; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            default: break;
        }
        setStyle(style,theme);
    }


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wait, container, false);
    }
}
