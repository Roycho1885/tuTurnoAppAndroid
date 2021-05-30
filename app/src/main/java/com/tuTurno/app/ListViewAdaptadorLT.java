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


public class ListViewAdaptadorLT extends BaseAdapter {
    private  Context contexto;
    private ArrayList<DatosTurno> listadatosturnos;


    public ListViewAdaptadorLT(Context contexto, ArrayList<DatosTurno> listadatosturnos) {
        this.contexto = contexto;
        this.listadatosturnos = listadatosturnos;
    }

    @Override
    public int getCount() {
        return listadatosturnos.size();
    }

    @Override
    public Object getItem(int i) {
        return listadatosturnos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DatosTurno items = (DatosTurno) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.listtur_item,null);
        TextView nombre = view.findViewById(R.id.txt_nombreyape);
        TextView disciplina = view.findViewById(R.id.txt_disciplina);
        TextView diaturno = view.findViewById(R.id.txt_diaturno);
        TextView horacom = view.findViewById(R.id.txt_horaturno);

        nombre.setText(items.getApellido() +" "+ items.getNombre());
        disciplina.setText(items.getDisciplina());
        diaturno.setText(items.getFecha());
        horacom.setText(items.getTurno());


        return view;
    }
}
