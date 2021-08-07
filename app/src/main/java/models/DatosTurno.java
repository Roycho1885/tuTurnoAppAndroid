package models;

public class DatosTurno {

    public String idTurno;
    public String nombre;
    public String apellido;
    public String direccionturno;
    public String dniturno;
    public String cliente;
    public String fecha;
    public String turno;
    public String disciplina;
    public String idturnoseleccionado;
    public String asistencia;
    public int icono;

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(String asistencia) {
        this.asistencia = asistencia;
    }

    public String getIdturnoseleccionado() {
        return idturnoseleccionado;
    }

    public void setIdturnoseleccionado(String idturnoseleccionado) {
        this.idturnoseleccionado = idturnoseleccionado;
    }

    public String getDireccionturno() {
        return direccionturno;
    }

    public void setDireccionturno(String direccionturno) {
        this.direccionturno = direccionturno;
    }

    public String getDniturno() {
        return dniturno;
    }

    public void setDniturno(String dniturno) {
        this.dniturno = dniturno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(String idTurno) {
        this.idTurno = idTurno;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return  apellido +" "+ nombre + "\n"
                + disciplina + "\n"
                + fecha + "\n"
                + "Turno: "+ turno+"hs";

    }

}
