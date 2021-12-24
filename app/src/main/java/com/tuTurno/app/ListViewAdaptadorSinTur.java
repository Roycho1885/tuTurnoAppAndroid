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

import models.DatosSinTurno;
import models.DatosTurno;

public class ListViewAdaptadorSinTur extends BaseAdapter {

    private Context contexto;
    private ArrayList<DatosSinTurno> listaturnoscliente;

    public ListViewAdaptadorSinTur(Context contexto, ArrayList<DatosSinTurno> listaturnoscliente) {
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

    @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        DatosSinTurno items = (DatosSinTurno) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.listtur_item_sintur,null);
        TextView nombre = view.findViewById(R.id.txt_nombreyapecli);
        TextView disciplina = view.findViewById(R.id.txt_disciplinacli);
        TextView diaturno = view.findViewById(R.id.txt_diaturnocli);
        TextView horacom = view.findViewById(R.id.txt_horaturnocli);
        ImageView imgasis  = view.findViewById(R.id.imgasis);

        nombre.setText(items.getApellido() +" "+ items.getNombre());
        disciplina.setText(items.getDisciplina());
        diaturno.setText(items.getFecha());
        horacom.setText(items.getHora());
        imgasis.setImageResource(R.drawable.ic_baseline_check_circle_24);

        return view;
    }
}
