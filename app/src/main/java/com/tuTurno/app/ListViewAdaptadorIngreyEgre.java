package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import models.ingresosextras;

public class ListViewAdaptadorIngreyEgre extends BaseAdapter {

    private final Context contexto;
    private final ArrayList<ingresosextras> listaingreygas;

    public ListViewAdaptadorIngreyEgre(Context contexto, ArrayList<ingresosextras> listaingreygas) {
        this.contexto = contexto;
        this.listaingreygas = listaingreygas;
    }

    @Override
    public int getCount() {
        return listaingreygas.size();
    }

    @Override
    public Object getItem(int i) {
        return listaingreygas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ingresosextras items = (ingresosextras) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.list_items_gasyegre,null);
        TextView ingreso = view.findViewById(R.id.txt_ingreso);
        TextView ingresoextra = view.findViewById(R.id.txt_ingresoextra);
        TextView egreso = view.findViewById(R.id.txt_egreso);

        ingreso.setText(items.getMontoingreso());
        ingresoextra.setText(items.getMontoingresoextra());
        egreso.setText(items.getMontogasto());

        return view;
    }
}
