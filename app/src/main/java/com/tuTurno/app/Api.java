package com.tuTurno.app;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("send")
    Call<ResponseBody> enviarnotificacion(
            @Field("token") String token,
            @Field("title") String title,
            @Field("body") String body
    );
    @FormUrlEncoded
    @POST("send")
    Call<ResponseBody> enviarnotificaciontemas(
            @Field("topic") String topic,
            @Field("title") String title,
            @Field("body") String body
    );
}
