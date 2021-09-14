package com.tuTurno.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import models.cliente;

public class ListViewAdaptadorCuotasAdmin extends BaseAdapter implements Filterable {

    private final Context contexto;
    private ArrayList<cliente> listacuotasadmin;
    CustomFilter filter;
    private final ArrayList<cliente> listafitro;

    public ListViewAdaptadorCuotasAdmin(Context contexto, ArrayList<cliente> listacuotasadmin) {
        this.contexto = contexto;
        this.listacuotasadmin = listacuotasadmin;
        this.listafitro = listacuotasadmin;
    }

    @Override
    public int getCount() {
        return listacuotasadmin.size();
    }

    @Override
    public Object getItem(int i) {
        return listacuotasadmin.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return super.getAutofillOptions();
    }

    @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        cliente items = (cliente) getItem(i);
        view = LayoutInflater.from(contexto).inflate(R.layout.list_item_cuotas_admin,null);
        TextView nombre = view.findViewById(R.id.txtnyacuotasadmin);
        TextView ultimopago = view.findViewById(R.id.txtultimopago);
        TextView fechavence = view.findViewById(R.id.txtfechavenc);
        TextView disciplina = view.findViewById(R.id.txtdiscielegida);
        TextView diasxsemana = view.findViewById(R.id.txtdiasxsemana);
        ImageView estadopago = view.findViewById(R.id.imgestado);

        nombre.setText(items.getApellido() +" "+ items.getNombre());
        ultimopago.setText(items.getUltimopago());
        fechavence.setText(items.getFechavencimiento());
        disciplina.setText(items.getDisciplinaelegida());
        diasxsemana.setText(items.getDiasporsemanaresg());
        estadopago.setImageResource(items.getEstadopago());

        return view;
    }

    @Override
    public Filter getFilter() {

        if(filter == null){
            filter=new CustomFilter();
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

                ArrayList<cliente> filters = new ArrayList<cliente>();
                for(int i=0;i<listafitro.size();i++){
                    if(listafitro.get(i).getApellido().toUpperCase().contains(charSequence)){
                        cliente c = new cliente(listafitro.get(i).getApellido(),listafitro.get(i).getNombre(),listafitro.get(i).getUltimopago(),
                                listafitro.get(i).getFechavencimiento(),listafitro.get(i).getEstadopago(),listafitro.get(i).getDisciplinaelegida(), listafitro.get(i).getDiasporsemana());
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
                listacuotasadmin = (ArrayList<cliente>) filterResults.values;
                notifyDataSetChanged();
        }
    }
}
