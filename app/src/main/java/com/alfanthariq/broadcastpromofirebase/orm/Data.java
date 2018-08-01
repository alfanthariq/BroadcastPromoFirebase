package com.alfanthariq.broadcastpromofirebase.orm;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by alfanthariq on 01/08/2018.
 */

public class Data extends SugarRecord {
    @Unique
    String kode;
    String nama;
    JenisBarang jenisBarang;
    String signaturePath;
    String fotoPath;

    public Data(){

    }

    public Data(String kode, String nama, JenisBarang jenisBarang, String signaturePath, String fotoPath) {
        this.kode = kode;
        this.nama = nama;
        this.jenisBarang = jenisBarang;
        this.signaturePath = signaturePath;
        this.fotoPath = fotoPath;
    }
}
