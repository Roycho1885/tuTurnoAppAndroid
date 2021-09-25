package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import models.ingresosextras;

public class ListViewAdaptadorDetallesIngyGas extends BaseAdapter {

    private Context contexto;
    private ArrayList<ingresosextras> listadetallesingygas;

    public ListViewAdaptadorDetallesIngyGas(Context contexto, ArrayList<ingresosextras> listadetallesingygas) {
        this.contexto = contexto;
        this.listadetallesingygas = listadetallesingygas;
    }

    @Override
    public int getCount() {
        return listadetallesingygas.size();
    }

    @Override
    public Object getItem(int i) {
        return listadetallesingygas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ingresosextras items = (ingresosextras) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.list_item_detalles_ingygas, null);
        TextView descripcion = view.findViewById(R.id.txt_detalledescripcion);
        TextView descripcionfecha = view.findViewById(R.id.txt_detallefecha);
        TextView monto = view.findViewById(R.id.txt_detallemonto);

        descripcion.setText(items.getDescripcion());
        if(items.getTipo().equals("ingreso")){
            monto.setTextColor(Color.parseColor("#a5c500"));
        }else{
            monto.setTextColor(Color.parseColor("#ff0006"));
        }
        descripcionfecha.setText(items.getFecha());
        monto.setText(items.getMontoingreso());

        return view;
    }
}

