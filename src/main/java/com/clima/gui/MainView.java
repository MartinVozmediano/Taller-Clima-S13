package com.clima.gui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class MainView extends AppLayout {

    private final ZonaRepository zonaRepository;
    private final ServicioPrediccion servicioPrediccion;
    private final ContaminacionRepository contaminacionRepository;

    private DashboardPanel dashboardPanel;
    private MenuPanel menuPanel;

    public MainView(@Autowired ZonaRepository zonaRepository,
                    @Autowired ServicioPrediccion servicioPrediccion,
                    @Autowired ContaminacionRepository contaminacionRepository) {
        this.zonaRepository = zonaRepository;
        this.servicioPrediccion = servicioPrediccion;
        this.contaminacionRepository = contaminacionRepository;

        setupLayout();
    }

    private void setupLayout() {
        createHeader();

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        content.setSizeFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.setSpacing(false);
        mainLayout.setPadding(false);

        // Inicializar el contenedor principal izquierdo
        dashboardPanel = new DashboardPanel(zonaRepository, servicioPrediccion);
        dashboardPanel.setContaminacionRepository(contaminacionRepository);
        dashboardPanel.setWidth("75%");
        dashboardPanel.setHeight("100%");

        // Inicializar el menú derecho pasando la referencia del contenedor izquierdo
        menuPanel = new MenuPanel(zonaRepository, servicioPrediccion, dashboardPanel);
        menuPanel.setContaminacionRepository(contaminacionRepository);
        menuPanel.setWidth("25%");
        menuPanel.setHeight("100%");

        mainLayout.add(dashboardPanel, menuPanel);
        mainLayout.expand(dashboardPanel);

        content.add(mainLayout);
        setContent(content);
    }

    private void createHeader() {
        H1 appTitle = new H1("🌍 Sistema de Monitoreo de Calidad del Aire");
        appTitle.getStyle().set("color", "#2e7d32");
        appTitle.getStyle().set("margin", "10px 20px");

        VerticalLayout header = new VerticalLayout(appTitle);
        header.getStyle().set("background-color", "#e8f5e9");
        header.setWidthFull();
        addToNavbar(header);
    }
}