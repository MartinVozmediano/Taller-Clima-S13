package gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import models.Zona;
import models.LecturaContaminacion;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;
import java.util.Optional;

public class MonitoringDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final ContaminacionRepository contaminacionRepository;
    private final ServicioPrediccion servicioPrediccion;

    public MonitoringDialog(ZonaRepository zonaRepository,
                           ContaminacionRepository contaminacionRepository,
                           ServicioPrediccion servicioPrediccion) {
        this.zonaRepository = zonaRepository;
        this.contaminacionRepository = contaminacionRepository;
        this.servicioPrediccion = servicioPrediccion;

        setHeaderTitle("📡 Monitorear Límites OMS");
        setWidth("600px");
        setHeight("600px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

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

            VerticalLayout dataLayout = new VerticalLayout();
            dataLayout.setSpacing(true);
            dataLayout.setPadding(true);
            dataLayout.getStyle().set("background-color", "#f1f8e9");
            dataLayout.getStyle().set("border-radius", "4px");

            zonaCombo.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    displayZoneMonitoring(event.getValue(), dataLayout);
                }
            });

            layout.add(zonaCombo, dataLayout);

            Button refreshButton = new Button("🔄 Actualizar");
            refreshButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            refreshButton.getStyle().set("background-color", "#43a047");
            refreshButton.addClickListener(e -> {
                if (zonaCombo.getValue() != null) {
                    displayZoneMonitoring(zonaCombo.getValue(), dataLayout);
                }
            });

            Button closeButton = new Button("Cerrar");
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeButton.addClickListener(event -> close());

            layout.add(refreshButton, closeButton);
            layout.expand(dataLayout);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        add(layout);
    }

    private void displayZoneMonitoring(Zona zona, VerticalLayout container) {
        try {
            container.removeAll();

            var optLectura = contaminacionRepository
                    .findFirstByZonaIdOrderByFechaHoraDesc(zona.getId());

            if (optLectura.isEmpty()) {
                container.add(createErrorComponent("No hay datos de lectura para esta zona"));
                return;
            }

            LecturaContaminacion lectura = optLectura.get();
            String alerta = servicioPrediccion.evaluarAlertasOMS(lectura);

            H3 title = new H3("Monitoreo: " + zona.getNombre());
            title.getStyle().set("color", "#1b5e20");
            container.add(title);

            // Mostrar alerta general
            Div alertaDiv = new Div();
            alertaDiv.setText(alerta);
            alertaDiv.getStyle().set("padding", "10px");
            alertaDiv.getStyle().set("border-radius", "4px");
            if (alerta.contains("✓")) {
                alertaDiv.getStyle().set("background-color", "#c8e6c9");
                alertaDiv.getStyle().set("color", "#1b5e20");
            } else {
                alertaDiv.getStyle().set("background-color", "#ffcdd2");
                alertaDiv.getStyle().set("color", "#c62828");
            }
            container.add(alertaDiv);

            // Grid con datos
            Grid<Object[]> grid = new Grid<>();
            grid.addColumn(row -> row[0]).setHeader("Contaminante");
            grid.addColumn(row -> String.format("%.2f", (Double) row[1])).setHeader("Nivel Actual");
            grid.addColumn(row -> String.format("%.1f", (Double) row[2])).setHeader("Límite OMS");
            grid.addColumn(row -> row[3]).setHeader("Estado");
            grid.setWidthFull();
            grid.setHeight("300px");

            grid.setItems(
                new Object[]{"CO2", lectura.getCo2(), 400.0, getStatus(lectura.getCo2(), 400.0)},
                new Object[]{"SO2", lectura.getSo2(), 20.0, getStatus(lectura.getSo2(), 20.0)},
                new Object[]{"NO2", lectura.getNo2(), 40.0, getStatus(lectura.getNo2(), 40.0)},
                new Object[]{"PM2.5", lectura.getPm25(), 15.0, getStatus(lectura.getPm25(), 15.0)}
            );

            container.add(grid);

        } catch (Exception e) {
            container.removeAll();
            container.add(createErrorComponent("Error: " + e.getMessage()));
        }
    }

    private String getStatus(double valor, double limite) {
        return valor > limite ? "⚠️ Excedido" : "✓ Dentro";
    }

    private Div createErrorComponent(String message) {
        Div div = new Div();
        div.setText(message);
        div.getStyle().set("color", "#c62828");
        div.getStyle().set("padding", "10px");
        div.getStyle().set("background-color", "#ffebee");
        return div;
    }
}


