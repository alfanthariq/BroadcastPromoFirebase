package com.alfanthariq.broadcastpromofirebase.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfanthariq.broadcastpromofirebase.MainActivity;
import com.alfanthariq.broadcastpromofirebase.R;
import com.alfanthariq.broadcastpromofirebase.orm.Data;
import com.alfanthariq.broadcastpromofirebase.orm.JenisBarang;
import com.alfanthariq.broadcastpromofirebase.pojo.CallPojo;
import com.alfanthariq.broadcastpromofirebase.rest.ApiHelper;
import com.alfanthariq.broadcastpromofirebase.rest.ApiListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.animatePagerTransition;
import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.showProgress;


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
    private ImageButton btn_sync;
    private TextView txt_location;

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
        btn_sync = v.findViewById(R.id.btn_sync);
        txt_location = v.findViewById(R.id.txt_location);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity act = (MainActivity) getActivity();
        final ApiHelper apiHelper = new ApiHelper(act.getApiLibrary());

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
        final TextView txt_info = txt;

        btn_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Data> data = Data.listAll(Data.class);
                for (int i=0; i<data.size(); i++) {
                    Data d = data.get(i);
                    final String kode = d.getKode();
                    String nama = d.getNama();
                    JenisBarang jenis = d.getJenisBarang();
                    String sign = d.getSignaturePath();
                    String foto = d.getFotoPath();
                    Map<String, String> param = new HashMap<>();
                    param.put("kode", kode);
                    param.put("nama", nama);
                    param.put("jenis", Integer.toString(jenis.getIdJenis()));
                    param.put("sign_path", "123");
                    param.put("foto_path", "123");

                    apiHelper.storeData(param, new ApiListener() {
                        @Override
                        public void onBeforeCall() {
                            txt_info.setText("Sedang mengunggah data");
                        }

                        @Override
                        public void onAfterCall() {
                            List<Data> data = Data.listAll(Data.class);
                            txt_info.setText("Jumlah data : "+Integer.toString(data.size()));
                        }

                        @Override
                        public void onSuccessCall(Response<CallPojo> response) {
                            if (response.body()!=null) {
                                boolean err = response.body().getError();
                                if (!err) {
                                    List<Data> del = Data.find(Data.class, "kode = ?", kode);
                                    del.get(0).delete();
                                }
                            }
                            List<Data> data = Data.listAll(Data.class);
                            if (data.size()==0) {
                                Toast.makeText(getContext(), "Synced", Toast.LENGTH_SHORT).show();
                            }
                            txt_info.setText("Jumlah data : "+Integer.toString(data.size()));
                        }

                        @Override
                        public void onFailedCall() {
                            List<Data> data = Data.listAll(Data.class);
                            txt_info.setText("Jumlah data : "+Integer.toString(data.size()));
                        }
                    });
                }
            }
        });
    }

    public RelativeLayout getLayoutModal(){
        return this.layoutModal;
    }

    public interface AfterCreateListener{
        void onAfterCreate();
    }

    public TextView getTxt_location() {
        return txt_location;
    }
}
