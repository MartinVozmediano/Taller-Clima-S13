package com.clima.gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import models.Zona;
import repositories.ZonaRepository;

public class ZonesEditDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final DashboardPanel dashboardPanel;

    public ZonesEditDialog(ZonaRepository zonaRepository, DashboardPanel dashboardPanel) {
        this.zonaRepository = zonaRepository;
        this.dashboardPanel = dashboardPanel;

        setHeaderTitle("✏️ Editar Zona");
        setWidth("400px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);

        setupContent();
    }

    private void setupContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        try {
            // ComboBox para seleccionar zona
            ComboBox<Zona> zonaCombo = new ComboBox<>("Seleccionar Zona");
            zonaCombo.setItemLabelGenerator(Zona::getNombre);
            zonaCombo.setItems(zonaRepository.findAll());
            zonaCombo.setWidthFull();

            // Campo para nuevo nombre
            TextField nombreField = new TextField("Nuevo Nombre");
            nombreField.setPlaceholder("Ingrese el nuevo nombre");
            nombreField.setWidthFull();

            // Mostrar nombre actual cuando se selecciona
            zonaCombo.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    nombreField.setValue(event.getValue().getNombre());
                } else {
                    nombreField.clear();
                }
            });

            layout.add(zonaCombo, nombreField);

            // Botones
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setWidthFull();
            buttonLayout.setSpacing(true);

            Button updateButton = new Button("Actualizar");
            updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            updateButton.getStyle().set("background-color", "#43a047");
            updateButton.addClickListener(event -> updateZone(zonaCombo, nombreField));

            Button deleteButton = new Button("Eliminar");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            deleteButton.getStyle().set("background-color", "#e53935");
            deleteButton.addClickListener(event -> deleteZone(zonaCombo));

            Button cancelButton = new Button("Cancelar");
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            cancelButton.addClickListener(event -> close());

            buttonLayout.add(updateButton, deleteButton, cancelButton);

            layout.add(buttonLayout);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        add(layout);
    }

    private void updateZone(ComboBox<Zona> zonaCombo, TextField nombreField) {
        try {
            Zona zona = zonaCombo.getValue();
            if (zona == null) {
                showNotification("Seleccione una zona", "error");
                return;
            }

            String nuevoNombre = nombreField.getValue().trim();
            if (nuevoNombre.isEmpty()) {
                showNotification("El nombre no puede estar vacío", "error");
                return;
            }

            zona.setNombre(nuevoNombre);
            zonaRepository.save(zona);

            showNotification("Zona actualizada exitosamente", "success");
            dashboardPanel.refreshZones();
            close();

        } catch (Exception e) {
            showNotification("Error: " + e.getMessage(), "error");
        }
    }

    private void deleteZone(ComboBox<Zona> zonaCombo) {
        try {
            Zona zona = zonaCombo.getValue();
            if (zona == null) {
                showNotification("Seleccione una zona", "error");
                return;
            }

            zonaRepository.delete(zona);
            showNotification("Zona eliminada exitosamente", "success");
            dashboardPanel.refreshZones();
            close();

        } catch (Exception e) {
            showNotification("Error: " + e.getMessage(), "error");
        }
    }

    private void showNotification(String message, String type) {
        if (type.equals("error")) {
            ExceptionHandler.showErrorNotification(message);
        } else {
            ExceptionHandler.showSuccessNotification(message);
        }
    }

    private com.vaadin.flow.component.html.Div createErrorComponent(String message) {
        com.vaadin.flow.component.html.Div div = new com.vaadin.flow.component.html.Div();
        div.setText(message);
        div.getStyle().set("color", "#c62828");
        return div;
    }
}


