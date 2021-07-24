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

import models.cuotas;

public class ListViewAdaptadorVerPagosAdmin extends BaseAdapter implements Filterable {
    private Context contexto;
    private ArrayList<cuotas> listacuotaspagas;
    private final ArrayList<cuotas> listafitro;
    ListViewAdaptadorVerPagosAdmin.CustomFilter filter;

    public ListViewAdaptadorVerPagosAdmin(Context contexto, ArrayList<cuotas> listacuotaspagas) {
        this.contexto = contexto;
        this.listacuotaspagas = listacuotaspagas;
        this.listafitro = listacuotaspagas;
    }

    @Override
    public int getCount() {
        return listacuotaspagas.size();
    }

    @Override
    public Object getItem(int i) {
        return listacuotaspagas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public CharSequence[] getAutofillOptions() {
        return super.getAutofillOptions();
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        cuotas items = (cuotas) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.list_item_cuotas_verpagos_admin,null);
        TextView nombrecliente = view.findViewById(R.id.txt_nombreyapellidocliente);
        TextView fechapago = view.findViewById(R.id.txtfechadepago);
        TextView fechavenc = view.findViewById(R.id.txtfechavenc);
        TextView montocuota = view.findViewById(R.id.txtmontocuota);
        TextView disciplina = view.findViewById(R.id.txtdisciele);

        nombrecliente.setText(items.getClientenombre());
        fechapago.setText(items.getFechapago());
        fechavenc.setText(items.getFechavenc());
        montocuota.setText(items.getMonto());
        disciplina.setText(items.getDisciplinaele());

        return view;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new CustomFilter();
        }
        return filter;
    }

    //INNER CLAS
    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();

            if(charSequence != null && charSequence.length()>0) {
                charSequence = charSequence.toString().toUpperCase();

                ArrayList<cuotas> filters = new ArrayList<cuotas>();
                for(int i=0;i<listafitro.size();i++){
                    if(listafitro.get(i).getClientenombre().toUpperCase().contains(charSequence)){
                        cuotas c = new cuotas(listafitro.get(i).getClientenombre(),
                                listafitro.get(i).getFechapago(),listafitro.get(i).getFechavenc(),listafitro.get(i).getMespago(),listafitro.get(i).getDisciplinaele());
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
            listacuotaspagas = (ArrayList<cuotas>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
