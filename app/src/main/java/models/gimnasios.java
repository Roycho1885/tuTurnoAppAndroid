package models;

public class gimnasios {
    private String nombre;
    private String logo;
    public String codigoacceso;
    private String direccion;
    private String urldire;
    private String fondonav;

    public gimnasios() {
    }

    public gimnasios(String nombre, String logo, String codigoacceso, String direccion, String urldire, String fondonav) {
        this.nombre = nombre;
        this.logo = logo;
        this.codigoacceso = codigoacceso;
        this.direccion = direccion;
        this.urldire = urldire;
        this.fondonav = fondonav;
    }


    public String getUrldire() {
        return urldire;
    }

    public void setUrldire(String urldire) {
        this.urldire = urldire;
    }

    public String getFondonav() {
        return fondonav;
    }

    public void setFondonav(String fondonav) {
        this.fondonav = fondonav;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
