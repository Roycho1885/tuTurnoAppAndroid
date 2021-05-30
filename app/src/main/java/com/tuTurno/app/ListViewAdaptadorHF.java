package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import models.turno;

public class ListViewAdaptadorHF extends BaseAdapter {
    private final Context contexto;
    private final ArrayList<turno> listaturnos;

    public ListViewAdaptadorHF(Context contexto, ArrayList<turno> listaturnos) {
        this.contexto = contexto;
        this.listaturnos = listaturnos;
    }

    @Override
    public int getCount() {
        return listaturnos.size();
    }

    @Override
    public Object getItem(int i) {
        return listaturnos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        turno items = (turno) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.lisdeturnositems,null);
        TextView hora = view.findViewById(R.id.txt_hora);
        TextView cupo = view.findViewById(R.id.txt_cupo);
        TextView dias = view.findViewById(R.id.txt_dias);
        ImageView checkcupo = view.findViewById(R.id.imagencupo);

        hora.setText(items.getHoracomienzo());
        cupo.setText(items.getCupo());
        dias.setText(items.getDias());
        checkcupo.setImageResource(items.getFoto());

        return view ;
    }
}
