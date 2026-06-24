package models;

import  java.time.LocalDateTime;

public class Prediccion {
    //Limites planteados en el codigo anterior por investigacion de niveles criticos de la OMS

    private static final double LIMITE_CO2 = 400.0;
    private static final double LIMITE_SO2 = 20.0;
    private static final double LIMITE_NO2 = 40.0;
    private static final double LIMITE_PM25 = 15.0;

    private String zonaId;
    private LocalDateTime fechaGeneracion;
    private String resumen;
    private boolean alertaActiva;
    private enum NivelesRiesgo {BAJO, MEDIO, ALTO};
    private NivelesRiesgo nivelRiesgo;
    private double co2Proyectado;
    private double so2Proyectado;
    private double no2Proyectado;
    private double pm25Proyectado;

    //Constructor sin resumen ni alerta
    public Prediccion(String zonaId, LocalDateTime fechaGeneracion,
                      double co2Proyectado, double so2Proyectado,
                      double no2Proyectado, double pm25Proyectado) {
        this.zonaId = zonaId;
        this.fechaGeneracion = fechaGeneracion;
        this.co2Proyectado = co2Proyectado;
        this.so2Proyectado = so2Proyectado;
        this.no2Proyectado = no2Proyectado;
        this.pm25Proyectado = pm25Proyectado;

        generarResultadosProyectados();
    }

    private void generarResultadosProyectados() {
        int limitesExcedidos = 0;
        StringBuilder constructorResumen = new StringBuilder("Proyección 24h: ");

        // Comparacion de contaminantes
        if (this.co2Proyectado > LIMITE_CO2) {
            limitesExcedidos++;
            constructorResumen.append("CO2 excedido. ");
        }
        if (this.so2Proyectado > LIMITE_SO2) {
            limitesExcedidos++;
            constructorResumen.append("SO2 excedido. ");
        }
        if (this.no2Proyectado > LIMITE_NO2) {
            limitesExcedidos++;
            constructorResumen.append("NO2 excedido. ");
        }
        if (this.pm25Proyectado > LIMITE_PM25) {
            limitesExcedidos++;
            constructorResumen.append("PM2.5 excedido. ");
        }

        // Asigna estado de alerta
        this.alertaActiva = (limitesExcedidos > 0);

        // Asignar nivel de riesgo y completar el resumen
        if (limitesExcedidos == 0) {
            this.nivelRiesgo = NivelesRiesgo.BAJO;
            constructorResumen.append("Calidad del aire en condiciones normales.");
        } else if (limitesExcedidos <= 2) {
            this.nivelRiesgo = NivelesRiesgo.MEDIO;
            constructorResumen.append("Precaución: Calidad del aire moderada a mala.");
        } else {
            this.nivelRiesgo = NivelesRiesgo.ALTO;
            constructorResumen.append("¡Alerta crítica! Riesgo alto para la salud.");
        }

        // Guardar el resumen final
        this.resumen = constructorResumen.toString().trim();
    }

    //GETTERS Y SETTERS
    public String getZonaid() { return zonaId; }
    public void setZonaid(String zonaId) { this.zonaId = zonaId; }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getResumen() { return resumen; }

    public boolean isAlertaActiva() { return alertaActiva; }

    public NivelesRiesgo getNivelRiesgo() { return nivelRiesgo; }

    public double getCo2Proyectado() { return co2Proyectado; }
    public void setCo2Proyectado(double co2Proyectado) {
        this.co2Proyectado = co2Proyectado;
        generarResultadosProyectados(); // Recalcular si el valor cambia
    }

    public double getSo2Proyectado() { return so2Proyectado; }
    public void setSo2Proyectado(double so2Proyectado) {
        this.so2Proyectado = so2Proyectado;
        generarResultadosProyectados();
    }

    public double getNo2Proyectado() { return no2Proyectado; }
    public void setNo2Proyectado(double no2Proyectado) {
        this.no2Proyectado = no2Proyectado;
        generarResultadosProyectados();
    }

    public double getPm25Proyectado() { return pm25Proyectado; }
    public void setPm25Proyectado(double pm25Proyectado) {
        this.pm25Proyectado = pm25Proyectado;
        generarResultadosProyectados();
    }

}