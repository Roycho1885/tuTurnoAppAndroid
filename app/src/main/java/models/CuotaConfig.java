package models;

public class CuotaConfig {
    public String id;
    public String disciplina;
    public String monto;

    public CuotaConfig(String id, String disciplina, String monto){
        this.id = id;
        this.disciplina = disciplina;
        this.monto = monto;
    }

    public CuotaConfig(){

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

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }
}
