package com.clima.gui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import models.Zona;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DashboardPanel extends VerticalLayout {

    private final ZonaRepository zonaRepository;
    private final ServicioPrediccion servicioPrediccion;
    private ContaminacionRepository contaminacionRepository;

    private HorizontalLayout zonesContainer;

    public DashboardPanel(@Autowired ZonaRepository zonaRepository,
                         @Autowired ServicioPrediccion servicioPrediccion) {
        this.zonaRepository = zonaRepository;
        this.servicioPrediccion = servicioPrediccion;

        setSpacing(true);
        setPadding(true);
        getStyle().set("background-color", "#f1f8e9");
        getStyle().set("border-right", "2px solid #c5e1a5");

        setupHeader();
        setupZonesContainer();
        refreshZones();
    }

    private void setupHeader() {
        H2 title = new H2("📊 Monitoreo de Zonas");
        title.getStyle().set("color", "#2e7d32");
        title.getStyle().set("margin", "0 0 20px 0");
        add(title);
    }

    private void setupZonesContainer() {
        zonesContainer = new HorizontalLayout();
        zonesContainer.setWidthFull();
        zonesContainer.setSpacing(true);
        zonesContainer.setPadding(true);
        zonesContainer.getStyle().set("flex-wrap", "wrap");
        zonesContainer.getStyle().set("align-content", "flex-start");

        add(zonesContainer);
    }

    public void refreshZones() {
        try {
            zonesContainer.removeAll();

            List<Zona> zonas = null;
            try {
                zonas = zonaRepository.findAll();
            } catch (Exception e) {
                zonas = List.of();
            }

            if (zonas == null || zonas.isEmpty()) {
                Div emptyMessage = new Div();
                emptyMessage.setText("No hay zonas registradas. Cree una nueva zona para comenzar.");
                emptyMessage.getStyle().set("color", "#888");
                emptyMessage.getStyle().set("text-align", "center");
                emptyMessage.setWidthFull();
                zonesContainer.add(emptyMessage);
                return;
            }

            for (Zona zona : zonas) {
                try {
                    zonesContainer.add(createZoneCard(zona));
                } catch (Exception e) {
                    // Continuar con la siguiente zona si hay error en una
                }
            }

        } catch (Exception e) {
            showError("Error al cargar las zonas: " + e.getMessage());
        }
    }

    private Div createZoneCard(Zona zona) {
        Div card = new Div();
        card.setWidth("280px");
        card.setHeight("200px");
        card.getStyle().set("border-radius", "8px");
        card.getStyle().set("padding", "15px");
        card.getStyle().set("box-shadow", "0 2px 6px rgba(0,0,0,0.1)");
        card.getStyle().set("display", "flex");
        card.getStyle().set("flex-direction", "column");
        card.getStyle().set("justify-content", "space-between");

        try {
            // Obtener última lectura con manejo robusto de errores
            double pm25Actual = 0;

            try {
                if (contaminacionRepository != null) {
                    var ultimaLectura = contaminacionRepository.findFirstByZonaIdOrderByFechaHoraDesc(zona.getId());
                    if (ultimaLectura.isPresent()) {
                        pm25Actual = ultimaLectura.get().getPm25();
                    }
                }
            } catch (Exception ex) {
                // Si falla la consulta, usar valor por defecto
                pm25Actual = 0;
            }

            // Determinar color según contaminación
            String colorFondo = getColorByPM25(pm25Actual);

            card.getStyle().set("background-color", colorFondo);

            // Header con nombre
            H2 zoneName = new H2(zona.getNombre());
            zoneName.getStyle().set("margin", "0 0 10px 0");
            zoneName.getStyle().set("color", "#1b5e20");
            zoneName.getStyle().set("font-size", "18px");

            // Info de contaminación
            Span pm25Info = new Span("PM2.5: " + String.format("%.1f", pm25Actual) + " µg/m³");
            pm25Info.getStyle().set("font-size", "16px");
            pm25Info.getStyle().set("font-weight", "bold");
            pm25Info.getStyle().set("color", getTextColorByPM25(pm25Actual));

            // Estado
            Span status = new Span(getStatusByPM25(pm25Actual));
            status.getStyle().set("font-size", "14px");
            status.getStyle().set("color", "#333");
            status.getStyle().set("margin-top", "10px");

            card.add(zoneName, pm25Info, status);

        } catch (Exception e) {
            Span error = new Span("Error al cargar datos");
            error.getStyle().set("color", "#c62828");
            card.add(error);
        }

        return card;
    }

    private String getColorByPM25(double pm25) {
        if (pm25 <= 12) return "#c8e6c9"; // Verde claro - Bueno
        if (pm25 <= 35.4) return "#fff9c4"; // Amarillo - Moderado
        if (pm25 <= 55.4) return "#ffe0b2"; // Naranja - No saludable sensibles
        return "#ffcdd2"; // Rojo - No saludable
    }

    private String getTextColorByPM25(double pm25) {
        if (pm25 <= 12) return "#1b5e20";
        if (pm25 <= 35.4) return "#f57f17";
        if (pm25 <= 55.4) return "#e65100";
        return "#c62828";
    }

    private String getStatusByPM25(double pm25) {
        if (pm25 <= 12) return "✓ Calidad Buena";
        if (pm25 <= 35.4) return "○ Calidad Moderada";
        if (pm25 <= 55.4) return "⚠ Grupos Sensibles";
        return "✗ No Saludable";
    }

    private void showError(String message) {
        Div errorDiv = new Div();
        errorDiv.setText(message);
        errorDiv.getStyle().set("color", "#c62828");
        errorDiv.getStyle().set("padding", "10px");
        errorDiv.getStyle().set("background-color", "#ffebee");
        errorDiv.getStyle().set("border-radius", "4px");
        add(errorDiv);
    }

    public void setContaminacionRepository(ContaminacionRepository repo) {
        this.contaminacionRepository = repo;
    }
}


