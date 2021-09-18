package models;

public class DatosNotificaciones {
    String fecha;
    int control;

    public DatosNotificaciones(){

    }

    public DatosNotificaciones(String fecha, int control){
        this.fecha = fecha;
        this.control = control;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
    }
}
