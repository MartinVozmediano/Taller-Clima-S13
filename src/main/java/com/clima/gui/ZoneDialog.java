package com.clima.gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import models.LecturaContaminacion;
import models.Zona;
import repositories.ContaminacionRepository;
import repositories.ZonaRepository;

import java.time.LocalDateTime;

public class ZoneDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final ContaminacionRepository contaminacionRepository;
    private final DashboardPanel dashboardPanel;

    public ZoneDialog(
            ZonaRepository zonaRepository,
            ContaminacionRepository contaminacionRepository,
            DashboardPanel dashboardPanel
    ) {
        this.zonaRepository = zonaRepository;
        this.contaminacionRepository = contaminacionRepository;
        this.dashboardPanel = dashboardPanel;

        setHeaderTitle("➕ Crear Nueva Zona");
        setWidth("480px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setupContent();
    }

    private void setupContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        // Campos del formulario
        TextField idField = new TextField("ID de Zona");
        idField.setPlaceholder("Ej: Centro");
        idField.setWidthFull();

        TextField nombreField = new TextField("Nombre de Zona");
        nombreField.setPlaceholder("Ej: Centro Histórico");
        nombreField.setWidthFull();
        nombreField.setRequiredIndicatorVisible(true);

        NumberField co2Field = createNumberField("CO2 actual (ppm)", 0.0, 5000.0, "400.0");
        NumberField so2Field = createNumberField("SO2 actual (µg/m³)", 0.0, 2000.0, "20.0");
        NumberField no2Field = createNumberField("NO2 actual (µg/m³)", 0.0, 2000.0, "40.0");
        NumberField pm25Field = createNumberField("PM2.5 actual (µg/m³)", 0.0, 1000.0, "15.0");
        NumberField temperaturaField = createNumberField("Temperatura actual (°C)", -50.0, 60.0, "20.0");
        NumberField vientoField = createNumberField("Viento actual (km/h)", 0.0, 250.0, "5.0");
        NumberField humedadField = createNumberField("Humedad actual (%)", 0.0, 100.0, "50.0");

        layout.add(
                idField,
                nombreField,
                co2Field,
                so2Field,
                no2Field,
                pm25Field,
                temperaturaField,
                vientoField,
                humedadField
        );

        // Botones
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);

        Button saveButton = new Button("Guardar");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.getStyle().set("background-color", "#43a047");
        saveButton.addClickListener(event -> saveZone(
                idField,
                nombreField,
                co2Field,
                so2Field,
                no2Field,
                pm25Field,
                temperaturaField,
                vientoField,
                humedadField
        ));

        Button cancelButton = new Button("Cancelar");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(event -> close());

        buttonLayout.add(saveButton, cancelButton);
        buttonLayout.setFlexGrow(1, saveButton, cancelButton);

        layout.add(buttonLayout);
        add(layout);
    }

    private NumberField createNumberField(String label, double min, double max, String placeholder) {
        NumberField field = new NumberField(label);
        field.setWidthFull();
        field.setStep(0.1);
        field.setMin(min);
        field.setMax(max);
        field.setPlaceholder(placeholder);
        field.setRequiredIndicatorVisible(true);
        return field;
    }

    private void saveZone(
            TextField idField,
            TextField nombreField,
            NumberField co2Field,
            NumberField so2Field,
            NumberField no2Field,
            NumberField pm25Field,
            NumberField temperaturaField,
            NumberField vientoField,
            NumberField humedadField
    ) {
        try {
            String id = idField.getValue().trim();
            String nombre = nombreField.getValue().trim();
            Double co2 = co2Field.getValue();
            Double so2 = so2Field.getValue();
            Double no2 = no2Field.getValue();
            Double pm25 = pm25Field.getValue();
            Double temperatura = temperaturaField.getValue();
            Double viento = vientoField.getValue();
            Double humedad = humedadField.getValue();

            if (id.isEmpty() || nombre.isEmpty()) {
                showNotification("Por favor complete todos los campos", "error");
                return;
            }

            if (co2 == null || so2 == null || no2 == null || pm25 == null
                    || temperatura == null || viento == null || humedad == null) {
                showNotification("Debe ingresar todos los valores ambientales actuales", "error");
                return;
            }

            // Validar caracteres especiales
            if (!id.matches("^[a-zA-Z0-9_-]+$")) {
                showNotification("ID solo puede contener letras, números, guiones y guiones bajos", "error");
                return;
            }

            // Validar que no exista la zona
            if (zonaRepository.existsById(id)) {
                showNotification("La zona con ID '" + id + "' ya existe", "error");
                return;
            }

            if (co2 < 0 || so2 < 0 || no2 < 0 || pm25 < 0 || viento < 0 || humedad < 0 || humedad > 100
                    || temperatura < -50 || temperatura > 60) {
                showNotification("Valores fuera de rango permitido", "error");
                return;
            }

            Zona nuevaZona = new Zona(id, nombre);
            zonaRepository.save(nuevaZona);

            try {
                LecturaContaminacion lecturaInicial = new LecturaContaminacion(
                        null,
                        id,
                        LocalDateTime.now(),
                        co2,
                        so2,
                        no2,
                        pm25,
                        temperatura,
                        viento,
                        humedad
                );
                contaminacionRepository.save(lecturaInicial);
            } catch (Exception e) {
                zonaRepository.deleteById(id);
                throw e;
            }

            showNotification("Zona y datos actuales guardados correctamente", "success");
            dashboardPanel.refreshZones();
            close();

        } catch (IllegalArgumentException e) {
            showNotification("Datos inválidos: " + e.getMessage(), "error");
        } catch (Exception e) {
            ExceptionHandler.handleDatabaseException(e);
        }
    }

    private void showNotification(String message, String type) {
        if (type.equals("error")) {
            ExceptionHandler.showErrorNotification(message);
        } else {
            ExceptionHandler.showSuccessNotification(message);
        }
    }
}


