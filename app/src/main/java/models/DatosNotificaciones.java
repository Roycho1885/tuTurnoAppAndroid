package models;

public class DatosNotificaciones {
    String fecha;

    public DatosNotificaciones(){

    }

    public DatosNotificaciones(String fecha){
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
