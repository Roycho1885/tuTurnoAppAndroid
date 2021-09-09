package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import models.DatosTurno;

public class ListViewAdaptadorLA extends BaseAdapter {
    private Context contexto;
    private ArrayList<DatosTurno> listaturnoscliente;

    public ListViewAdaptadorLA(Context contexto, ArrayList<DatosTurno> listaturnoscliente) {
        this.contexto = contexto;
        this.listaturnoscliente = listaturnoscliente;
    }

    @Override
    public int getCount() {
        return listaturnoscliente.size();
    }

    @Override
    public Object getItem(int i) {
        return listaturnoscliente.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        DatosTurno items = (DatosTurno) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.listtur_item_admin,null);
        TextView nombre = view.findViewById(R.id.txt_nombreyapecli);
        TextView direccion = view.findViewById(R.id.txt_direccioncli);
        TextView dni = view.findViewById(R.id.txt_dnicli);
        TextView disciplina = view.findViewById(R.id.txt_disciplinacli);
        TextView diaturno = view.findViewById(R.id.txt_diaturnocli);
        TextView horacom = view.findViewById(R.id.txt_horaturnocli);
        ImageView imgasis  = view.findViewById(R.id.imgasis);

        nombre.setText(items.getApellido() +" "+ items.getNombre());
        direccion.setText(items.getDireccionturno());
        dni.setText(items.getDniturno());
        disciplina.setText(items.getDisciplina());
        diaturno.setText(items.getFecha());
        horacom.setText(items.getTurno());
        imgasis.setImageResource(items.getIcono());



        return view;
    }
}
