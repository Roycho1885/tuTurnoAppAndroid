package models;

public class ingresosextras {
    private String id;
    private String descripcion;
    private String montoingreso;
    private String montoingresoextra;
    private String montogasto;
    private String mes;
    private String ano;
    private String tipo;
    private String fecha;

    public String getMontoingresoextra() {
        return montoingresoextra;
    }

    public void setMontoingresoextra(String montoingresoextra) {
        this.montoingresoextra = montoingresoextra;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMontogasto() {
        return montogasto;
    }

    public void setMontogasto(String montogasto) {
        this.montogasto = montogasto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMontoingreso() {
        return montoingreso;
    }

    public void setMontoingreso(String montoingreso) {
        this.montoingreso = montoingreso;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
