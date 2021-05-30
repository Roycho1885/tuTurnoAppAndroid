package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import models.DatosTurno;
import models.turno;

public class ListViewAdaptadorAdEliTur extends BaseAdapter {
    private Context contexto;
    private ArrayList<turno> listaturnoeliminar;

    public ListViewAdaptadorAdEliTur(Context contexto, ArrayList<turno> listaturnoeliminar) {
        this.contexto = contexto;
        this.listaturnoeliminar = listaturnoeliminar;
    }

    @Override
    public int getCount() {
        return listaturnoeliminar.size();
    }

    @Override
    public Object getItem(int i) {
        return listaturnoeliminar.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        turno items = (turno) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.listtureliminar_item,null);

        TextView hora = view.findViewById(R.id.txt_horaeli);
        TextView cupo = view.findViewById(R.id.txt_cupoeli);
        TextView dias = view.findViewById(R.id.txt_diaseli);

        hora.setText(items.getHoracomienzo());
        cupo.setText(items.getCupo());
        dias.setText(items.getDias());

        return view;
    }
}
