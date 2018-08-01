package com.alfanthariq.broadcastpromofirebase.orm;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by alfanthariq on 01/08/2018.
 */

public class JenisBarang extends SugarRecord {
    @Unique
    int id_jenis;
    String jenis;

    @Override
    public String toString() {
        return jenis;
    }

    public JenisBarang(){

    }

    public JenisBarang(int id, String jenis) {
        this.id_jenis = id;
        this.jenis = jenis;
    }

    public int getIdJenis() {
        return id_jenis;
    }

    public void setIdJenis(int id) {
        this.id_jenis = id;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }
}
