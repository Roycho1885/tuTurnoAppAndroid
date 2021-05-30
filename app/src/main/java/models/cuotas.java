package models;

public class cuotas {
    public String clientenombre;
    public String emailcliente;
    public String fechapago;
    public String fechavenc;
    public String mespago;
    public int estadopago;

    public cuotas(String clientenombre, String emailcliente, String fechapago,String fechavenc,String mespago, int estadopago) {
        this.clientenombre = clientenombre;
        this.emailcliente = emailcliente;
        this.fechapago = fechapago;
        this.fechavenc = fechavenc;
        this.mespago = mespago;
        this.estadopago = estadopago;
    }

    public cuotas() {
    }


    public cuotas(String clientenombre, String fechapago,String fechavenc,String mespago){
        this.clientenombre = clientenombre;
        this.fechapago = fechapago;
        this.fechavenc = fechavenc;
        this.mespago = mespago;
    }

    public int getEstadopago() {
        return estadopago;
    }

    public void setEstadopago(int estadopago) {
        this.estadopago = estadopago;
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
