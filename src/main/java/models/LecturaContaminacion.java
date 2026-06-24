package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "lecturas_contaminacion")
public class LecturaContaminacion {

    @Id
    private String id;
    
    private String zonaId;
    private LocalDateTime fechaHora;

    //Contaminantes:
    private double co2;
    private double so2;
    private double no2;
    private double pm25;

    //Factores climaticos:
    private double temperatura;                 //En °C
    private double velocidadViento;             //En km/h;
    private double humedad;                     //Porcentaje

    public LecturaContaminacion(String id, String zonaId, LocalDateTime fechaHora,
                                double co2, double so2, double no2, double pm25,
                                double temperatura, double velocidadViento, double humedad) {
        this.id = id;
        this.zonaId = zonaId;
        this.fechaHora = fechaHora;
        this.co2 = co2;
        this.so2 = so2;
        this.no2 = no2;
        this.pm25 = pm25;
        this.temperatura = temperatura;
        this.velocidadViento = velocidadViento;
        this.humedad = humedad;
    }


    // Getters y Setters para atributos

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZonaId() {
        return zonaId;
    }

    public void setZonaId(String zonaId) {
        this.zonaId = zonaId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public double getSo2() {
        return so2;
    }

    public void setSo2(double so2) {
        this.so2 = so2;
    }

    public double getNo2() {
        return no2;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public double getPm25() {
        return pm25;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getVelocidadViento() {
        return velocidadViento;
    }

    public void setVelocidadViento(double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }
}
