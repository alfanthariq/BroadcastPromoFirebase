package com.alfanthariq.broadcastpromofirebase.fragment;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alfanthariq.broadcastpromofirebase.MainActivity;
import com.alfanthariq.broadcastpromofirebase.R;
import com.alfanthariq.broadcastpromofirebase.helper.AfterCropListener;
import com.alfanthariq.broadcastpromofirebase.orm.Data;
import com.alfanthariq.broadcastpromofirebase.orm.JenisBarang;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.orm.SugarRecord;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.animatePagerTransition;
import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.getBitmpatFromUri;
import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.getResizedBitmap;
import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.isConnectToInternet;
import static com.alfanthariq.broadcastpromofirebase.helper.MyFunction.saveToInternalStorage;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCoomingPromo extends Fragment {

    private ImageView imgBack;
    private ViewPager pager;
    private ImageView img;
    private Spinner spin_jenis;
    private Button btn_simpan;
    private EditText edt_kode, edt_nama;
    private SignaturePad signaturePad;
    private Uri fotoUri;

    private List<JenisBarang> jenis;
    private JenisBarang selected_jenis;

    public FragmentCoomingPromo() {
        // Required empty public constructor
    }

    public static FragmentCoomingPromo newInstance(){
        FragmentCoomingPromo f = new FragmentCoomingPromo();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cooming_promo, container, false);
        imgBack = v.findViewById(R.id.imgBack);
        img = v.findViewById(R.id.img);
        spin_jenis = v.findViewById(R.id.spin_jenis);
        btn_simpan = v.findViewById(R.id.btn_simpan);
        edt_kode = v.findViewById(R.id.edt_kode);
        edt_nama = v.findViewById(R.id.edt_nama);
        signaturePad = v.findViewById(R.id.signature_pad);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pager = getActivity().findViewById(R.id.pager);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animatePagerTransition(false, pager);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(getActivity());
            }
        });

        MainActivity act = (MainActivity) getActivity();
        act.setAfterCropListener(new AfterCropListener() {
            @Override
            public void onAfterCrop(Uri resultUri) {
                img.setImageURI(resultUri);
                fotoUri = resultUri;
                //Log.d("FragmentCooming", "Uri : "+resultUri.toString());
            }
        });

        jenis = JenisBarang.listAll(JenisBarang.class);

        if (jenis.size()==0) {
            List<JenisBarang> jenisBarang = new ArrayList<>();
            jenisBarang.add(new JenisBarang(1, "Jenis Barang 1"));
            jenisBarang.add(new JenisBarang(2, "Jenis Barang 2"));
            jenisBarang.add(new JenisBarang(3, "Jenis Barang 3"));
            SugarRecord.saveInTx(jenisBarang);
            jenis = JenisBarang.listAll(JenisBarang.class);
        }

        ArrayAdapter<JenisBarang> adapter = new ArrayAdapter<JenisBarang>(getContext(), android.R.layout.simple_spinner_dropdown_item, jenis);
        spin_jenis.setAdapter(adapter);
        spin_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_jenis = jenis.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kode = edt_kode.getText().toString();
                String nama = edt_nama.getText().toString();
                Bitmap signature = signaturePad.getSignatureBitmap();
                File file = new File(fotoUri.getPath());
                Bitmap foto = getBitmpatFromUri(getContext(), fotoUri);
                Bitmap foto_resized = getResizedBitmap(foto, 360, 360);
                OutputStream os;
                try {
                    os = new FileOutputStream(file);
                    foto_resized.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
                if (isConnectToInternet(getContext())) {
                    Toast.makeText(getContext(), "Online", Toast.LENGTH_SHORT).show();
                } else {
                    String local_signature = saveToInternalStorage(getContext(), signature, "signature_".concat(kode).concat(".jpg"));
                    String local_foto = saveToInternalStorage(getContext(), foto_resized, "foto_".concat(kode).concat(".jpg"));
                    Data data = new Data(kode, nama, selected_jenis, local_signature, local_foto);
                    data.save();
                    reset();
                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reset(){
        edt_kode.setText("");
        edt_nama.setText("");
        spin_jenis.setSelection(0);
        signaturePad.clear();
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_camera));
    }
}
