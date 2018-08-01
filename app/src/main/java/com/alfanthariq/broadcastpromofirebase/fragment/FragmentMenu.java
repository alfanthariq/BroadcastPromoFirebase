package com.alfanthariq.broadcastpromofirebase.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfanthariq.broadcastpromofirebase.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMenu extends Fragment {


    public FragmentMenu() {
        // Required empty public constructor
    }

    public static FragmentMenu newInstance(){
        FragmentMenu f = new FragmentMenu();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

}
