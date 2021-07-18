package models;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.tuTurno.app.Api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MisFunciones {

    public String cargarDatosNav(Context contexto, String direccion, String urldire, String urllogo,
                                  String urlfondo, TextView txtdire, ImageView logo, ImageView fondo){
        txtdire.setText(direccion);
        Picasso.with(contexto).load(urllogo).into(logo);
        Picasso.with(contexto).load(urlfondo).into(fondo);

        return urldire;
    }

    public void enviarnotificacionapi(String token, String titulo , String detalle, final View view, ProgressDialog cargando){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tuturno-91997.web.app/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> call = api.enviarnotificacion(token,titulo,detalle);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assert response.body() != null;
                cargando.dismiss();
                Snackbar.make(view,"Notificación enviada con exito",Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cargando.dismiss();
                Snackbar.make(view,"No se pudo enviar la notificación, revise su conexión a internet e intente nuevamente",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void enviarnotificacionapitemas(String topic, String titulo , String detalle, final View view, ProgressDialog cargando){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://tuturno-91997.web.app/api1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> call = api.enviarnotificaciontemas(topic,titulo,detalle);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assert response.body() != null;
                cargando.dismiss();
                Snackbar.make(view,"Notificación enviada con exito",Snackbar.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cargando.dismiss();
                Snackbar.make(view,"No se pudo enviar la notificación, revise su conexión a internet e intente nuevamente",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}
