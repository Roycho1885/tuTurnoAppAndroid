package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import models.cliente;

public class ListViewAdaptadorDatosClientes extends BaseAdapter implements Filterable {
    private Context contexto;
    private ArrayList<cliente> listadatosclientes;
   CustomFilter filter;
   private final ArrayList<cliente> listafitro;



    public ListViewAdaptadorDatosClientes(Context contexto, ArrayList<cliente> listadatosclientes) {
        this.contexto = contexto;
        this.listadatosclientes = listadatosclientes;
        this.listafitro = listadatosclientes;
    }

    @Override
    public int getCount() {
        return listadatosclientes.size();
    }

    @Override
    public Object getItem(int i) {
        return listadatosclientes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return super.getAutofillOptions();
    }

    @SuppressLint({"SetTextI18n", "ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        cliente items = (cliente) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.list_item_clientes,null);
        TextView nombre = view.findViewById(R.id.txt_nombreyapellidocliente);
        nombre.setText(items.getApellido() +" "+ items.getNombre());

        return view;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter=new CustomFilter();
        }return filter;
    }

    //INNER CLAS
    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();

            if(charSequence != null && charSequence.length()>0) {
                charSequence = charSequence.toString().toUpperCase();

                ArrayList<cliente> filters = new ArrayList<cliente>();
                for(int i=0;i<listafitro.size();i++){
                    if(listafitro.get(i).getApellido().toUpperCase().contains(charSequence)){
                        cliente c = new cliente(listafitro.get(i).getApellido(),listafitro.get(i).getNombre());
                        filters.add(c);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = listafitro.size();
                results.values = listafitro;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listadatosclientes = (ArrayList<cliente>) filterResults.values;
            notifyDataSetChanged();
        }
    }

}
