package models;

public class FactoresClimaticos {
    private double temperatura;
    private double humedad;
    private double velocidadViento;

    // Constructor
    public FactoresClimaticos(double temperatura, double humedad, double velocidadViento) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.velocidadViento = velocidadViento;
    }

    // Getters and Setters
    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    public double getVelocidadViento() {
        return velocidadViento;
    }

    public void setVelocidadViento(double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }
}
