package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import models.CuotaConfig;

public class ListViewAdaptadorMonto extends BaseAdapter {

    private final Context contexto;
    private final ArrayList<CuotaConfig> listacuota;

    public ListViewAdaptadorMonto(Context contexto, ArrayList<CuotaConfig> listacuota) {
        this.contexto = contexto;
        this.listacuota = listacuota;
    }

    @Override
    public int getCount() {
        return listacuota.size();
    }

    @Override
    public Object getItem(int i) {
        return listacuota.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CuotaConfig items = (CuotaConfig) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.lisadminmontoitems,null);
        TextView disci = view.findViewById(R.id.txt_disciplina);
        TextView monto = view.findViewById(R.id.txt_monto);

        disci.setText(items.getDisciplina());
        monto.setText(items.getMonto());

        return view ;
    }
}
