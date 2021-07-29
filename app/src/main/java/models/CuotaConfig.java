package models;

public class CuotaConfig {
    public String id;
    public String disciplina;
    public String configuracioncuotas;
    public String monto;
    public String diasporsemana;
    public String idcuotas;

    public CuotaConfig(String id, String disciplina, String configuracioncuotas){
        this.id = id;
        this.disciplina = disciplina;
        this.configuracioncuotas = configuracioncuotas;
    }

   /* public CuotaConfig(String idcuotas, String diasporsemana,String monto){
        this.idcuotas = idcuotas;
        this.diasporsemana = diasporsemana;
        this.monto = monto;
    }*/

    public CuotaConfig(String id, String disciplina){
        this.id = id;
        this.disciplina = disciplina;
    }

    public CuotaConfig(){

    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getDiasporsemana() {
        return diasporsemana;
    }

    public void setDiasporsemana(String diasporsemana) {
        this.diasporsemana = diasporsemana;
    }

    public String getIdcuotas() {
        return idcuotas;
    }

    public void setIdcuotas(String idcuotas) {
        this.idcuotas = idcuotas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getConfiguracioncuotas() {
        return configuracioncuotas;
    }

    public void setConfiguracioncuotas(String configuracioncuotas) {
        this.configuracioncuotas = configuracioncuotas;
    }
}
