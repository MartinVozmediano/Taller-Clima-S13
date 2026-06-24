package gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import models.Zona;
import models.LecturaContaminacion;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;
import java.util.List;

public class FinalReportDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final ServicioPrediccion servicioPrediccion;
    private final ContaminacionRepository contaminacionRepository;

    public FinalReportDialog(ZonaRepository zonaRepository,
                            ServicioPrediccion servicioPrediccion,
                            ContaminacionRepository contaminacionRepository) {
        this.zonaRepository = zonaRepository;
        this.servicioPrediccion = servicioPrediccion;
        this.contaminacionRepository = contaminacionRepository;

        setHeaderTitle("📋 Reporte Final - Resumen Completo");
        setWidth("900px");
        setHeight("800px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        setupContent();
    }

    private void setupContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        try {
            // Crear tabs
            Tabs tabs = new Tabs();

            Tab generalTab = new Tab("Resumen General");
            Tab zonesTab = new Tab("Zonas Registradas");
            Tab statsTab = new Tab("Estadísticas");

            tabs.add(generalTab, zonesTab, statsTab);

            VerticalLayout generalContent = createGeneralReport();
            VerticalLayout zonesContent = createZonesReport();
            VerticalLayout statsContent = createStatsReport();

            // Mostrar contenido según pestaña
            tabs.addSelectedChangeListener(event -> {
                generalContent.setVisible(event.getSelectedTab() == generalTab);
                zonesContent.setVisible(event.getSelectedTab() == zonesTab);
                statsContent.setVisible(event.getSelectedTab() == statsTab);
            });

            generalContent.setVisible(true);
            zonesContent.setVisible(false);
            statsContent.setVisible(false);

            layout.add(tabs, generalContent, zonesContent, statsContent);
            layout.expand(generalContent, zonesContent, statsContent);

            Button exportButton = new Button("📥 Exportar Datos");
            exportButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            exportButton.getStyle().set("background-color", "#43a047");

            Button closeButton = new Button("Cerrar");
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeButton.addClickListener(event -> close());

            layout.add(exportButton, closeButton);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        add(layout);
    }

    private VerticalLayout createGeneralReport() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle().set("background-color", "#f1f8e9");
        layout.getStyle().set("border-radius", "4px");

        try {
            H3 title = new H3("📊 Resumen General del Sistema");
            title.getStyle().set("color", "#1b5e20");
            layout.add(title);

            int totalZonas = (int) zonaRepository.count();
            int totalLecturas = 0;

            for (Zona zona : zonaRepository.findAll()) {
                List<LecturaContaminacion> lecturas = servicioPrediccion.obtenerHistorico30Dias(zona.getId());
                totalLecturas += lecturas.size();
            }

            Div infoDiv = new Div();
            infoDiv.getStyle().set("padding", "15px");
            infoDiv.getStyle().set("background-color", "white");
            infoDiv.getStyle().set("border-radius", "4px");
            infoDiv.getStyle().set("border-left", "4px solid #43a047");

            infoDiv.add(
                new Span("🌍 Zonas Registradas: " + totalZonas),
                new com.vaadin.flow.component.html.Div(),
                new Span("📡 Total de Lecturas (30 días): " + totalLecturas),
                new com.vaadin.flow.component.html.Div(),
                new Span("📅 Período: Últimos 30 días"),
                new com.vaadin.flow.component.html.Div(),
                new Span("✓ Estado: Sistema Operativo")
            );

            layout.add(infoDiv);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        return layout;
    }

    private VerticalLayout createZonesReport() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle().set("background-color", "#f1f8e9");
        layout.getStyle().set("border-radius", "4px");

        try {
            H3 title = new H3("🗺️ Zonas Monitoreadas");
            title.getStyle().set("color", "#1b5e20");
            layout.add(title);

            Grid<Object[]> grid = new Grid<>();
            grid.addColumn(row -> row[0]).setHeader("Zona");
            grid.addColumn(row -> row[1]).setHeader("ID");
            grid.addColumn(row -> row[2]).setHeader("Lecturas");
            grid.addColumn(row -> String.format("%.2f", (Double) row[3])).setHeader("Prom. PM2.5");
            grid.setWidthFull();
            grid.setHeightFull();

            java.util.List<Object[]> zonasData = new java.util.ArrayList<>();

            for (Zona zona : zonaRepository.findAll()) {
                List<LecturaContaminacion> lecturas = servicioPrediccion.obtenerHistorico30Dias(zona.getId());
                double promPM25 = servicioPrediccion.calcularPromedioPM25(lecturas);
                zonasData.add(new Object[]{zona.getNombre(), zona.getId(), lecturas.size(), promPM25});
            }

            grid.setItems(zonasData);
            layout.add(grid);
            layout.expand(grid);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        return layout;
    }

    private VerticalLayout createStatsReport() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle().set("background-color", "#f1f8e9");
        layout.getStyle().set("border-radius", "4px");

        try {
            H3 title = new H3("📈 Estadísticas Globales");
            title.getStyle().set("color", "#1b5e20");
            layout.add(title);

            double maxGlobalPM25 = 0;
            double minGlobalPM25 = Double.MAX_VALUE;
            double sumGlobalPM25 = 0;
            int countLecturas = 0;

            for (Zona zona : zonaRepository.findAll()) {
                List<LecturaContaminacion> lecturas = servicioPrediccion.obtenerHistorico30Dias(zona.getId());
                for (LecturaContaminacion lectura : lecturas) {
                    double pm25 = lectura.getPm25();
                    maxGlobalPM25 = Math.max(maxGlobalPM25, pm25);
                    minGlobalPM25 = Math.min(minGlobalPM25, pm25);
                    sumGlobalPM25 += pm25;
                    countLecturas++;
                }
            }

            double avgGlobalPM25 = countLecturas > 0 ? sumGlobalPM25 / countLecturas : 0;

            Div statsDiv = new Div();
            statsDiv.getStyle().set("padding", "15px");
            statsDiv.getStyle().set("background-color", "white");
            statsDiv.getStyle().set("border-radius", "4px");
            statsDiv.getStyle().set("display", "grid");
            statsDiv.getStyle().set("grid-template-columns", "1fr 1fr");
            statsDiv.getStyle().set("gap", "15px");

            Div stat1 = createStatBox("Promedio PM2.5 Global", String.format("%.2f µg/m³", avgGlobalPM25));
            Div stat2 = createStatBox("Máximo PM2.5", String.format("%.2f µg/m³", maxGlobalPM25));
            Div stat3 = createStatBox("Mínimo PM2.5", String.format("%.2f µg/m³", minGlobalPM25 == Double.MAX_VALUE ? 0 : minGlobalPM25));
            Div stat4 = createStatBox("Total de Lecturas", String.valueOf(countLecturas));

            statsDiv.add(stat1, stat2, stat3, stat4);
            layout.add(statsDiv);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error: " + e.getMessage()));
        }

        return layout;
    }

    private Div createStatBox(String label, String value) {
        Div box = new Div();
        box.getStyle().set("padding", "15px");
        box.getStyle().set("background-color", "#e8f5e9");
        box.getStyle().set("border-radius", "4px");
        box.getStyle().set("border-left", "4px solid #43a047");

        Div labelSpan = new Div();
        labelSpan.setText(label);
        labelSpan.getStyle().set("font-size", "12px");
        labelSpan.getStyle().set("color", "#666");

        Div valueSpan = new Div();
        valueSpan.setText(value);
        valueSpan.getStyle().set("font-size", "20px");
        valueSpan.getStyle().set("font-weight", "bold");
        valueSpan.getStyle().set("color", "#1b5e20");
        valueSpan.getStyle().set("margin-top", "5px");

        box.add(labelSpan, valueSpan);
        return box;
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



