package com.clima.gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import models.Zona;
import models.Prediccion;
import models.FactoresClimaticos;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;

public class PredictionDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final ServicioPrediccion servicioPrediccion;

    public PredictionDialog(ZonaRepository zonaRepository,
                           ServicioPrediccion servicioPrediccion,
                           ContaminacionRepository contaminacionRepository) {
        this.zonaRepository = zonaRepository;
        this.servicioPrediccion = servicioPrediccion;

        setHeaderTitle("🔮 Predicción 24 Horas");
        setWidth("600px");
        setHeight("700px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setupContent();
    }

    private void setupContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        try {
            ComboBox<Zona> zonaCombo = new ComboBox<>("Seleccionar Zona");
            zonaCombo.setItemLabelGenerator(Zona::getNombre);
            zonaCombo.setItems(zonaRepository.findAll());
            zonaCombo.setWidthFull();

            // Factores climáticos
            H3 climaTitle = new H3("Factores Climáticos Actuales");
            climaTitle.getStyle().set("color", "#1b5e20");
            climaTitle.getStyle().set("margin-top", "20px");

            TextField temperaturaField = new TextField("Temperatura (°C)");
            temperaturaField.setPlaceholder("0");
            temperaturaField.setWidth("100%");
            temperaturaField.setValue("0");

            TextField vientoField = new TextField("Velocidad Viento (km/h)");
            vientoField.setPlaceholder("0");
            vientoField.setWidth("100%");
            vientoField.setValue("0");

            TextField humedadField = new TextField("Humedad (%)");
            humedadField.setPlaceholder("50");
            humedadField.setWidth("100%");
            humedadField.setValue("50");

            VerticalLayout resultLayout = new VerticalLayout();
            resultLayout.setSpacing(true);
            resultLayout.setPadding(true);
            resultLayout.getStyle().set("background-color", "#f1f8e9");
            resultLayout.getStyle().set("border-radius", "4px");
            resultLayout.setVisible(false);

            layout.add(
                zonaCombo,
                climaTitle,
                temperaturaField,
                vientoField,
                humedadField,
                resultLayout
            );

            // Botones
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setWidthFull();
            buttonLayout.setSpacing(true);

            Button predictButton = new Button("Predecir");
            predictButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            predictButton.getStyle().set("background-color", "#43a047");
            predictButton.addClickListener(event -> {
                if (zonaCombo.getValue() == null) {
                    ExceptionHandler.showWarningNotification("Seleccione una zona");
                    return;
                }

                try {
                    double temp = parseDouble(temperaturaField.getValue(), 0);
                    double viento = parseDouble(vientoField.getValue(), 0);
                    double humedad = parseDouble(humedadField.getValue(), 50);

                    if (viento < 0 || humedad < 0 || humedad > 100) {
                        ExceptionHandler.showWarningNotification("Valores inválidos");
                        return;
                    }

                    makePrediction(zonaCombo.getValue(), temp, viento, humedad, resultLayout);
                    resultLayout.setVisible(true);
                } catch (NumberFormatException e) {
                    ExceptionHandler.showErrorNotification("Ingrese valores numéricos válidos");
                }
            });

            Button closeButton = new Button("Cerrar");
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeButton.addClickListener(event -> close());

            buttonLayout.add(predictButton, closeButton);

            layout.add(buttonLayout);
            layout.expand(resultLayout);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        add(layout);
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return value == null || value.isEmpty() ? defaultValue : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void makePrediction(Zona zona, double temperatura, double viento, double humedad,
                               VerticalLayout resultLayout) {
        try {
            resultLayout.removeAll();

            FactoresClimaticos clima = new FactoresClimaticos(temperatura, humedad, viento);
            Prediccion prediccion = servicioPrediccion.predecirNivelesFuturos(zona.getId(), clima);

            H3 title = new H3("Predicción para: " + zona.getNombre());
            title.getStyle().set("color", "#1b5e20");
            resultLayout.add(title);

            // Resumen
            Div resumenDiv = new Div();
            resumenDiv.setText(prediccion.getResumen());
            resumenDiv.getStyle().set("padding", "10px");
            resumenDiv.getStyle().set("border-radius", "4px");
            resumenDiv.getStyle().set("background-color", "#e8f5e9");
            resumenDiv.getStyle().set("border-left", "4px solid #43a047");
            resultLayout.add(resumenDiv);

            // Grid con predicciones
            Grid<Object[]> grid = new Grid<>();
            grid.addColumn(row -> row[0]).setHeader("Contaminante");
            grid.addColumn(row -> String.format("%.2f", (Double) row[1])).setHeader("Predicción 24h");
            grid.addColumn(row -> String.format("%.1f", (Double) row[2])).setHeader("Límite OMS");
            grid.addColumn(row -> row[3]).setHeader("Nivel Riesgo");
            grid.setWidthFull();

            grid.setItems(
                new Object[]{"CO2", prediccion.getCo2Proyectado(), 400.0, getStatusColor(prediccion.getCo2Proyectado(), 400.0)},
                new Object[]{"SO2", prediccion.getSo2Proyectado(), 20.0, getStatusColor(prediccion.getSo2Proyectado(), 20.0)},
                new Object[]{"NO2", prediccion.getNo2Proyectado(), 40.0, getStatusColor(prediccion.getNo2Proyectado(), 40.0)},
                new Object[]{"PM2.5", prediccion.getPm25Proyectado(), 15.0, getStatusColor(prediccion.getPm25Proyectado(), 15.0)}
            );

            resultLayout.add(grid);

        } catch (Exception e) {
            ExceptionHandler.handleGeneralException(e);
        }
    }

    private String getStatusColor(double valor, double limite) {
        if (valor <= limite) return "✓ Bueno";
        if (valor <= limite * 1.5) return "○ Moderado";
        return "⚠ Crítico";
    }

    private Div createErrorComponent(String message) {
        Div div = new Div();
        div.setText(message);
        div.getStyle().set("color", "#c62828");
        return div;
    }
}




