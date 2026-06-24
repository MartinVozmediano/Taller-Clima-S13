package services;

import models.Prediccion;
import models.LecturaContaminacion;
import models.FactoresClimaticos;
import repositories.ContaminacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

@Service
public class ServicioPrediccion {

    @Autowired
    private ContaminacionRepository contaminacionRepository;

    // Límites OMS
    private static final double LIMITE_CO2 = 400.0;
    private static final double LIMITE_SO2 = 20.0;
    private static final double LIMITE_NO2 = 40.0;
    private static final double LIMITE_PM25 = 15.0;

    /**
     * Obtiene el historial de lecturas de los últimos 30 días para una zona específica
     */
    public List<LecturaContaminacion> obtenerHistorico30Dias(String zonaId) {
        LocalDateTime fechaFin = LocalDateTime.now();
        LocalDateTime fechaInicio = fechaFin.minusDays(30);

        return contaminacionRepository.findByZonaIdAndFechaHoraBetween(
                zonaId, fechaInicio, fechaFin
        );
    }

    /**
     * Calcula el promedio de PM2.5 a partir de una lista de lecturas
     */
    public double calcularPromedioPM25(List<LecturaContaminacion> lecturas) {
        if (lecturas == null || lecturas.isEmpty()) {
            return 0.0;
        }

        double sumaPM25 = lecturas.stream()
                .mapToDouble(LecturaContaminacion::getPm25)
                .sum();

        return sumaPM25 / lecturas.size();
    }

    /**
     * Evalúa si la lectura actual excede los límites de calidad del aire según OMS
     */
    public String evaluarAlertasOMS(LecturaContaminacion actual) {
        StringBuilder alerta = new StringBuilder();

        if (actual.getCo2() > LIMITE_CO2) {
            alerta.append("(!!) CO2 excedido (")
                    .append(String.format("%.1f", actual.getCo2()))
                    .append(" ppm). ");
        }
        if (actual.getSo2() > LIMITE_SO2) {
            alerta.append("(!!) SO2 excedido (")
                    .append(String.format("%.1f", actual.getSo2()))
                    .append(" µg/m³). ");
        }
        if (actual.getNo2() > LIMITE_NO2) {
            alerta.append("(!!) NO2 excedido (")
                    .append(String.format("%.1f", actual.getNo2()))
                    .append(" µg/m³). ");
        }
        if (actual.getPm25() > LIMITE_PM25) {
            alerta.append("(!!) PM2.5 excedido (")
                    .append(String.format("%.1f", actual.getPm25()))
                    .append(" µg/m³). ");
        }

        return alerta.length() > 0 ? alerta.toString().trim() : "✓ Valores dentro de límites OMS";
    }

    public Prediccion predecirNivelesFuturos(String zonaId, FactoresClimaticos clima) {

        // Obtener histórico de los últimos 30 días
        List<LecturaContaminacion> historialZona = obtenerHistorico30Dias(zonaId);

        if (historialZona == null || historialZona.isEmpty()) {
            throw new IllegalArgumentException("No hay registros en el historial para realizar la predicción.");
        }

        // Ordenar cronológicamente
        historialZona.sort(Comparator.comparing(LecturaContaminacion::getFechaHora));

        // PASO 1: Aplicar matemática ponderada sobre histórico reciente
        double sumCo2 = 0, sumSo2 = 0, sumNo2 = 0, sumPm25 = 0;
        double pesoTotal = 0;
        int registrosAnalizados = 0;

        for (int j = historialZona.size() - 1; j >= 0 && registrosAnalizados < 10; j--) {
            LecturaContaminacion lectura = historialZona.get(j);
            double peso = (10.0 - registrosAnalizados) / 10.0;
            pesoTotal += peso;

            sumCo2 += lectura.getCo2() * peso;
            sumSo2 += lectura.getSo2() * peso;
            sumNo2 += lectura.getNo2() * peso;
            sumPm25 += lectura.getPm25() * peso;

            registrosAnalizados++;
        }

        // PASO 2: Calcular promedios ponderados
        double co2Base = (pesoTotal > 0) ? sumCo2 / pesoTotal : 0;
        double so2Base = (pesoTotal > 0) ? sumSo2 / pesoTotal : 0;
        double no2Base = (pesoTotal > 0) ? sumNo2 / pesoTotal : 0;
        double pm25Base = (pesoTotal > 0) ? sumPm25 / pesoTotal : 0;

        // PASO 3: Ajustar con factores climáticos actuales
        double factorViento = calcularFactorViento(clima.getVelocidadViento());
        double factorHumedad = calcularFactorHumedad(clima.getHumedad());

        double co2Predicho = co2Base * factorViento;
        double so2Predicho = so2Base * factorViento;
        double no2Predicho = no2Base * factorViento;
        double pm25Predicho = pm25Base * factorHumedad;

        LocalDateTime fechaProyeccion = LocalDateTime.now().plusHours(24);

        return new Prediccion(
                zonaId,
                fechaProyeccion,
                co2Predicho,
                so2Predicho,
                no2Predicho,
                pm25Predicho
        );
    }

    /**
     * Calcula factor de reducción de contaminantes según velocidad del viento
     * A mayor viento, más dispersión (menor contaminación)
     */
    private double calcularFactorViento(double velocidadViento) {
        // Rango: 0.8 (vientos fuertes) a 1.0 (sin viento)
        // Por cada 5 km/h de viento, se reduce 4% la contaminación
        double reduccion = Math.min(0.2, velocidadViento * 0.04);
        return Math.max(0.8, 1.0 - reduccion);
    }

    /**
     * Calcula factor de aumento de PM2.5 según humedad
     * A mayor humedad, mayor concentración de partículas
     */
    private double calcularFactorHumedad(double humedad) {
        // Rango: 0.9 (humedad baja) a 1.2 (humedad alta)
        // Relación lineal: 30% a 90% humedad = 0.9 a 1.2
        if (humedad < 30) return 0.9;
        if (humedad > 90) return 1.2;
        return 0.9 + (humedad - 30) * 0.005;
    }
}

