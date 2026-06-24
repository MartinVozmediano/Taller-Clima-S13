package gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import models.Zona;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;
import org.springframework.beans.factory.annotation.Autowired;

public class MenuPanel extends VerticalLayout {

    private final ZonaRepository zonaRepository;
    private final ServicioPrediccion servicioPrediccion;
    private final DashboardPanel dashboardPanel;
    private ContaminacionRepository contaminacionRepository;

    public MenuPanel(@Autowired ZonaRepository zonaRepository,
                     @Autowired ServicioPrediccion servicioPrediccion,
                     DashboardPanel dashboardPanel) {
        this.zonaRepository = zonaRepository;
        this.servicioPrediccion = servicioPrediccion;
        this.dashboardPanel = dashboardPanel;

        setSpacing(true);
        setPadding(true);
        getStyle().set("background-color", "#e8f5e9");
        getStyle().set("border-left", "2px solid #a5d6a7");
        getStyle().set("overflow-y", "auto");

        setupMenu();
    }

    private void setupMenu() {
        H3 menuTitle = new H3("🔧 MENÚ");
        menuTitle.getStyle().set("color", "#1b5e20");
        menuTitle.getStyle().set("margin", "0 0 20px 0");
        menuTitle.getStyle().set("text-align", "center");
        add(menuTitle);

        // Botón: Crear Zona
        add(createMenuButton("➕ Crear Zona", () -> {
            try {
                ZoneDialog dialog = new ZoneDialog(zonaRepository, dashboardPanel);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));

        // Botón: Ver Zonas
        add(createMenuButton("👁️ Ver Zonas", () -> {
            try {
                ZonesListDialog dialog = new ZonesListDialog(zonaRepository, dashboardPanel);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));

        // Botón: Editar Zona
        add(createMenuButton("✏️ Editar Zona", () -> {
            try {
                ZonesEditDialog dialog = new ZonesEditDialog(zonaRepository, dashboardPanel);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));

        // Divisor visual
        addDivisor();

        // Botón: Monitorear Límites
        add(createMenuButton("📡 Monitorear Límites", () -> {
            try {
                if (contaminacionRepository == null) {
                    ExceptionHandler.showWarningNotification("Repositorio no disponible aún");
                    return;
                }
                MonitoringDialog dialog = new MonitoringDialog(zonaRepository, contaminacionRepository, servicioPrediccion);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));

        // Botón: Predicción 24h
        add(createMenuButton("🔮 Predicción 24h", () -> {
            try {
                if (contaminacionRepository == null) {
                    ExceptionHandler.showWarningNotification("Repositorio no disponible aún");
                    return;
                }
                PredictionDialog dialog = new PredictionDialog(zonaRepository, servicioPrediccion, contaminacionRepository);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));

        // Divisor visual
        addDivisor();

        // Botón: Reporte Histórico
        add(createMenuButton("📈 Reporte Histórico", () -> {
            try {
                if (contaminacionRepository == null) {
                    ExceptionHandler.showWarningNotification("Repositorio no disponible aún");
                    return;
                }
                HistoryReportDialog dialog = new HistoryReportDialog(zonaRepository, servicioPrediccion, contaminacionRepository);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));

        // Botón: Reporte Final
        add(createMenuButton("📋 Reporte Final", () -> {
            try {
                if (contaminacionRepository == null) {
                    ExceptionHandler.showWarningNotification("Repositorio no disponible aún");
                    return;
                }
                FinalReportDialog dialog = new FinalReportDialog(zonaRepository, servicioPrediccion, contaminacionRepository);
                dialog.open();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        }));
    }

    private Button createMenuButton(String label, Runnable onClick) {
        Button button = new Button(label);
        button.setWidthFull();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("background-color", "#43a047");
        button.getStyle().set("color", "white");
        button.getStyle().set("font-size", "14px");
        button.getStyle().set("padding", "12px");
        button.getStyle().set("border-radius", "4px");
        button.getStyle().set("cursor", "pointer");

        button.addClickListener(event -> {
            try {
                onClick.run();
            } catch (Exception e) {
                ExceptionHandler.handleGeneralException(e);
            }
        });

        button.getElement().addEventListener("mouseover", event -> {
            button.getStyle().set("background-color", "#2e7d32");
        });

        button.getElement().addEventListener("mouseout", event -> {
            button.getStyle().set("background-color", ""); // Vuelve al color original o al de tu CSS
        });

        return button;
    }

    private void addDivisor() {
        com.vaadin.flow.component.html.Hr divisor = new com.vaadin.flow.component.html.Hr();
        divisor.getStyle().set("border-top", "2px solid #a5d6a7");
        divisor.getStyle().set("margin", "15px 0");
        add(divisor);
    }

    public void setContaminacionRepository(ContaminacionRepository repo) {
        this.contaminacionRepository = repo;
    }
}



