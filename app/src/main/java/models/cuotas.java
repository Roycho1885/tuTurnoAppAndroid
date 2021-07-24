package models;

public class cuotas {
    public String clientenombre;
    public String emailcliente;
    public String fechapago;
    public String fechavenc;
    public String mespago;
    public String disciplinaele;
    public String monto;

    public cuotas(String clientenombre, String emailcliente, String fechapago,String fechavenc,String mespago, String disciplinaele, String monto) {
        this.clientenombre = clientenombre;
        this.emailcliente = emailcliente;
        this.fechapago = fechapago;
        this.fechavenc = fechavenc;
        this.mespago = mespago;
        this.disciplinaele = disciplinaele;
        this.monto = monto;
    }

    public cuotas() {
    }


    public cuotas(String clientenombre, String fechapago,String fechavenc,String mespago, String disciplinaele){
        this.clientenombre = clientenombre;
        this.fechapago = fechapago;
        this.fechavenc = fechavenc;
        this.mespago = mespago;
        this.disciplinaele = disciplinaele;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public void setMespago(String mespago) {
        this.mespago = mespago;
    }

    public String getDisciplinaele() {
        return disciplinaele;
    }

    public void setDisciplinaele(String disciplinaele) {
        this.disciplinaele = disciplinaele;
    }

    public String getEmailcliente() {
        return emailcliente;
    }

    public void setEmailcliente(String emailcliente) {
        this.emailcliente = emailcliente;
    }

    public String getFechavenc() {
        return fechavenc;
    }

    public void setFechavenc(String fechavenc) {
        this.fechavenc = fechavenc;
    }

    public String getClientenombre() {
        return clientenombre;
    }

    public void setClientenombre(String clientenombre) {
        this.clientenombre = clientenombre;
    }

    public String getFechapago() {
        return fechapago;
    }

    public void setFechapago(String fechapago) {
        this.fechapago = fechapago;
    }

    public String getMespago() {
        return mespago;
    }


}
