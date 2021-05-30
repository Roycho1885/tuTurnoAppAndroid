package models;

public class gimnasios {
    private String nombre;
    private String logo;
    public String codigoacceso;

    public gimnasios() {
    }

    public gimnasios(String nombre, String logo, String codigoacceso) {
        this.nombre = nombre;
        this.logo = logo;
        this.codigoacceso = codigoacceso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    public String getCodigoacceso() {
        return codigoacceso;
    }

    public void setCodigoacceso(String codigoacceso) {
        this.codigoacceso = codigoacceso;
    }
}
