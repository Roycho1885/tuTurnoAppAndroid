package models;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MisFunciones {

    public String cargarDatosNav(Context contexto, String direccion, String urldire, String urllogo,
                                  String urlfondo, TextView txtdire, ImageView logo, ImageView fondo){
        txtdire.setText(direccion);
        Picasso.with(contexto).load(urllogo).into(logo);
        Picasso.with(contexto).load(urlfondo).into(fondo);

        return urldire;
    }

}
