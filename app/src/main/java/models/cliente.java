package models;

public class cliente {

    public String id;
    public String nombre;
    public String apellido;
    public String dni;
    public String direccion;
    public String email;
    public String gym;
    public String admin;
    public String token;
    public String ultimopago;
    public String fechavencimiento;
    public int estadopago;
    public String estadodeuda;
    public String disciplinaelegida;
    public String diasporsemana;
    public String diasporsemanaresg;
    public String telefono;

    public cliente(String id, String nombre, String apellido, String dni, String direccion, String email, String gym,
                   String admin,String token, String ultimopago,String fechavencimiento, int estadopago, String estadodeuda, String disciplinaelegida, String diasporsemana, String diasporsemanaresg, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.direccion = direccion;
        this.email = email;
        this.gym = gym;
        this.admin = admin;
        this.token = token;
        this.ultimopago = ultimopago;
        this.fechavencimiento = fechavencimiento;
        this.estadopago = estadopago;
        this.estadodeuda = estadodeuda;
        this.disciplinaelegida = disciplinaelegida;
        this.diasporsemana = diasporsemana;
        this.diasporsemanaresg = diasporsemanaresg;
        this.telefono = telefono;
    }

    public cliente() {
    }

    public cliente(String apellido,String nombre,String disciplinaelegida){
        this.apellido = apellido;
        this.nombre = nombre;
        this.disciplinaelegida = disciplinaelegida;
    }

    public cliente(String apellido,String nombre, String ultimopago,String fechavencimiento, int estadopago, String disciplinaelegida, String diasporsemana){
        this.apellido = apellido;
        this.nombre = nombre;
        this.ultimopago = ultimopago;
        this.fechavencimiento = fechavencimiento;
        this.estadopago = estadopago;
        this.disciplinaelegida = disciplinaelegida;
        this.diasporsemana = diasporsemana;
    }

    public cliente(String id,String estadodeuda){
        this.id = id;
        this.estadodeuda = estadodeuda;
    }

    public cliente(String email){
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDiasporsemanaresg() {
        return diasporsemanaresg;
    }

    public void setDiasporsemanaresg(String diasporsemanaresg) {
        this.diasporsemanaresg = diasporsemanaresg;
    }

    public String getDiasporsemana() {
        return diasporsemana;
    }

    public void setDiasporsemana(String diasporsemana) {
        this.diasporsemana = diasporsemana;
    }

    public String getDisciplinaelegida() {
        return disciplinaelegida;
    }

    public void setDisciplinaelegida(String disciplinaelegida) {
        this.disciplinaelegida = disciplinaelegida;
    }

    public String getEstadodeuda() {
        return estadodeuda;
    }

    public void setEstadodeuda(String estadodeuda) {
        this.estadodeuda = estadodeuda;
    }

    public String getFechavencimiento() {
        return fechavencimiento;
    }

    public void setFechavencimiento(String fechavencimiento) {
        this.fechavencimiento = fechavencimiento;
    }

    public String getUltimopago() {
        return ultimopago;
    }

    public void setUltimopago(String ultimopago) {
        this.ultimopago = ultimopago;
    }

    public int getEstadopago() {
        return estadopago;
    }

    public void setEstadopago(int estadopago) {
        this.estadopago = estadopago;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGym() { return gym; }

    public void setGym(String gym) { this.gym = gym; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return apellido + " " + nombre + "\n"
                +"Admin "+ admin;
    }
}
