package models;

public class turno {

    public String id;
    public String horacomienzo;
    public String disciplina;
    public String cupo;
    public String cupoalmacenado;
    public String dias;
    public int foto;
    public String coach;

    public turno() {
    }

    public turno(String id, String horacomienzo, String disciplina, String cupo, String cupoalmacenado, String dias, String coach) {
        this.id = id;
        this.horacomienzo = horacomienzo;
        this.disciplina = disciplina;
        this.cupo = cupo;
        this.cupoalmacenado = cupoalmacenado;
        this.dias = dias;
        this.coach = coach;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getCupoalmacenado() {
        return cupoalmacenado;
    }

    public void setCupoalmacenado(String cupoalmacenado) {
        this.cupoalmacenado = cupoalmacenado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoracomienzo() {
        return horacomienzo;
    }

    public void setHoracomienzo(String horacomienzo) {
        this.horacomienzo = horacomienzo;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getCupo() {
        return cupo;
    }

    public void setCupo(String cupo) {
        this.cupo = cupo;
    }

    @Override
    public String toString() {
        return  "Hora: "+horacomienzo + "\n"
                + "Cupo: "+cupo + "\n"
                + "Días: "+dias + "\n";
    }
}
