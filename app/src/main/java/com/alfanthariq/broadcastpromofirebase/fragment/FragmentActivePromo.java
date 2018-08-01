package com.alfanthariq.broadcastpromofirebase.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfanthariq.broadcastpromofirebase.R;
import com.alfanthariq.broadcastpromofirebase.orm.Data;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.animatePagerTransition;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentActivePromo extends Fragment {

    private ImageView imgInfo;
    private CircleImageView imgProfile;
    private ViewPager pager;
    private RelativeLayout layoutModal;
    private AfterCreateListener afterCreateListener;
    private TextView txt;

    public void setAfterCreateListener(AfterCreateListener afterCreateListener) {
        this.afterCreateListener = afterCreateListener;
    }

    public FragmentActivePromo() {
        // Required empty public constructor
    }

    public static FragmentActivePromo newInstance(){
        FragmentActivePromo f = new FragmentActivePromo();
        return f;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            List<Data> data = Data.listAll(Data.class);
            if (txt!=null) {
                txt.setText("Jumlah data : " + Integer.toString(data.size()));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_active_promo, container, false);
        imgInfo = v.findViewById(R.id.imgInfo);
        imgProfile = v.findViewById(R.id.imgProfile);
        layoutModal = v.findViewById(R.id.layoutModal);
        txt = v.findViewById(R.id.txt);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pager = getActivity().findViewById(R.id.pager);

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatePagerTransition(true, pager);
                //pager.setCurrentItem(2, true);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatePagerTransition(false, pager);
                //pager.setCurrentItem(0, true);
            }
        });

        layoutModal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1, true);
                //animatePagerTransition(true, pager);
            }
        });

        afterCreateListener.onAfterCreate();

        List<Data> data = Data.listAll(Data.class);
        txt.setText("Jumlah data : "+Integer.toString(data.size()));
    }

    public RelativeLayout getLayoutModal(){
        return this.layoutModal;
    }

    public interface AfterCreateListener{
        void onAfterCreate();
    }
}
