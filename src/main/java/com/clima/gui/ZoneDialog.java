package com.clima.gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import models.Zona;
import repositories.ZonaRepository;

public class ZoneDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final DashboardPanel dashboardPanel;

    public ZoneDialog(ZonaRepository zonaRepository, DashboardPanel dashboardPanel) {
        this.zonaRepository = zonaRepository;
        this.dashboardPanel = dashboardPanel;

        setHeaderTitle("➕ Crear Nueva Zona");
        setWidth("400px");
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

        layout.add(idField, nombreField);

        // Botones
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);

        Button saveButton = new Button("Guardar");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.getStyle().set("background-color", "#43a047");
        saveButton.addClickListener(event -> saveZone(idField, nombreField));

        Button cancelButton = new Button("Cancelar");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(event -> close());

        buttonLayout.add(saveButton, cancelButton);
        buttonLayout.setFlexGrow(1, saveButton, cancelButton);

        layout.add(buttonLayout);
        add(layout);
    }

    private void saveZone(TextField idField, TextField nombreField) {
        try {
            String id = idField.getValue().trim();
            String nombre = nombreField.getValue().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                showNotification("Por favor complete todos los campos", "error");
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

            Zona nuevaZona = new Zona(id, nombre);
            zonaRepository.save(nuevaZona);

            showNotification("Zona creada exitosamente", "success");
            dashboardPanel.refreshZones();
            close();

        } catch (IllegalArgumentException e) {
            showNotification("Datos inválidos: " + e.getMessage(), "error ");
        } catch (Exception e) {
            showNotification("Error al guardar en MongoDB: " + e.getMessage(), "error");
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



