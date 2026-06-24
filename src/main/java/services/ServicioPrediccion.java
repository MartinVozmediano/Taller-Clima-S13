package services;

import models.Prediccion;
import models.LecturaContaminacion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

public class ServicioPrediccion {

    public Prediccion predecirNivelesFuturos(int zonaId, List<LecturaContaminacion> historialZona) {

        if (historialZona == null || historialZona.isEmpty()) {
            throw new IllegalArgumentException("No hay registros en el historial para realizar la predicción.");
        }

        // 1.Asegurar que el historial esté ordenado cronológicamente
        historialZona.sort(Comparator.comparing(LecturaContaminacion::getFechaHora));

        double sumCo2 = 0, sumSo2 = 0, sumNo2 = 0, sumPm25 = 0;
        double pesoTotal = 0;
        int registrosAnalizados = 0;

        // 2. MATEMÁTICA PONDERADA
        for (int j = historialZona.size() - 1; j >= 0 && registrosAnalizados < 10; j--) {
            LecturaContaminacion lectura = historialZona.get(j);

            // El peso va de 1.0 (el más reciente) bajando hasta 0.1 (el décimo más reciente)
            double peso = (10.0 - registrosAnalizados) / 10.0;
            pesoTotal += peso;

            sumCo2 += lectura.getCo2() * peso;
            sumSo2 += lectura.getSo2() * peso;
            sumNo2 += lectura.getNo2() * peso;
            sumPm25 += lectura.getPm25() * peso;

            registrosAnalizados++;
        }

        // 3. CALCULAR LOS PROMEDIOS PROYECTADOS
        double co2Predicho = 0;
        double so2Predicho = 0;
        double no2Predicho = 0;
        double pm25Predicho = 0;

        if (pesoTotal > 0) {
            co2Predicho = sumCo2 / pesoTotal;
            so2Predicho = sumSo2 / pesoTotal;
            no2Predicho = sumNo2 / pesoTotal;
            pm25Predicho = sumPm25 / pesoTotal;
        }

        // 4. EL TIEMPO: Calcular la fecha objetivo (24 horas en el futuro)
        LocalDateTime fechaProyeccion = LocalDateTime.now().plusHours(24);

        // 5. Instanaciamiento del objeto Prediccion con los valores calculados
        // Se ejecutará generarResultadosProyectados()

        return new Prediccion(
                zonaId,
                fechaProyeccion,
                co2Predicho,
                so2Predicho,
                no2Predicho,
                pm25Predicho
        );
    }
}

