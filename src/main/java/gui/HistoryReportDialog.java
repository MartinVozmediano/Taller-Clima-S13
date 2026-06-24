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
import java.util.List;

public class HistoryReportDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final ServicioPrediccion servicioPrediccion;
    private final ContaminacionRepository contaminacionRepository;

    public HistoryReportDialog(ZonaRepository zonaRepository,
                              ServicioPrediccion servicioPrediccion,
                              ContaminacionRepository contaminacionRepository) {
        this.zonaRepository = zonaRepository;
        this.servicioPrediccion = servicioPrediccion;
        this.contaminacionRepository = contaminacionRepository;

        setHeaderTitle("📈 Reporte Histórico - 30 Días");
        setWidth("800px");
        setHeight("700px");
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

            VerticalLayout reportLayout = new VerticalLayout();
            reportLayout.setSpacing(true);
            reportLayout.setPadding(true);
            reportLayout.getStyle().set("background-color", "#f1f8e9");
            reportLayout.getStyle().set("border-radius", "4px");

            zonaCombo.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    displayHistoryReport(event.getValue(), reportLayout);
                }
            });

            layout.add(zonaCombo, reportLayout);
            layout.expand(reportLayout);

            Button closeButton = new Button("Cerrar");
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeButton.addClickListener(event -> close());

            layout.add(closeButton);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        add(layout);
    }

    private void displayHistoryReport(Zona zona, VerticalLayout container) {
        try {
            container.removeAll();

            List<LecturaContaminacion> historico = servicioPrediccion.obtenerHistorico30Dias(zona.getId());

            if (historico.isEmpty()) {
                container.add(createErrorComponent("No hay datos históricos para esta zona en los últimos 30 días"));
                return;
            }

            H3 title = new H3("Historial: " + zona.getNombre());
            title.getStyle().set("color", "#1b5e20");
            container.add(title);

            // Calcular estadísticas
            double promedioPM25 = servicioPrediccion.calcularPromedioPM25(historico);
            double maxPM25 = historico.stream().mapToDouble(LecturaContaminacion::getPm25).max().orElse(0);
            double minPM25 = historico.stream().mapToDouble(LecturaContaminacion::getPm25).min().orElse(0);

            // Mostrar estadísticas
            Div statsDiv = new Div();
            statsDiv.getStyle().set("padding", "10px");
            statsDiv.getStyle().set("background-color", "#c8e6c9");
            statsDiv.getStyle().set("border-radius", "4px");
            statsDiv.getStyle().set("margin-bottom", "10px");

            statsDiv.add(
                new Span("📊 Promedio PM2.5: " + String.format("%.2f", promedioPM25) + " µg/m³"),
                new com.vaadin.flow.component.html.Div(),
                new Span("📈 Máximo: " + String.format("%.2f", maxPM25) + " µg/m³"),
                new com.vaadin.flow.component.html.Div(),
                new Span("📉 Mínimo: " + String.format("%.2f", minPM25) + " µg/m³")
            );

            container.add(statsDiv);

            // Grid con datos históricos
            Grid<LecturaContaminacion> grid = new Grid<>(LecturaContaminacion.class);
            grid.setColumns("fechaHora", "co2", "so2", "no2", "pm25", "temperatura", "velocidadViento", "humedad");

            grid.getColumnByKey("fechaHora").setHeader("Fecha/Hora");
            grid.getColumnByKey("co2").setHeader("CO2");
            grid.getColumnByKey("so2").setHeader("SO2");
            grid.getColumnByKey("no2").setHeader("NO2");
            grid.getColumnByKey("pm25").setHeader("PM2.5");
            grid.getColumnByKey("temperatura").setHeader("Temp (°C)");
            grid.getColumnByKey("velocidadViento").setHeader("Viento (km/h)");
            grid.getColumnByKey("humedad").setHeader("Humedad (%)");

            grid.setItems(historico);
            grid.setWidthFull();
            grid.setHeightFull();

            container.add(grid);
            container.expand(grid);

        } catch (Exception e) {
            container.removeAll();
            container.add(createErrorComponent("Error: " + e.getMessage()));
        }
    }

    private Div createErrorComponent(String message) {
        Div div = new Div();
        div.setText(message);
        div.getStyle().set("color", "#c62828");
        div.getStyle().set("padding", "10px");
        div.getStyle().set("background-color", "#ffebee");
        div.getStyle().set("border-radius", "4px");
        return div;
    }
}


