package gui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import models.Zona;
import repositories.ZonaRepository;
import repositories.ContaminacionRepository;
import services.ServicioPrediccion;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@PWA(name = "Sistema de Monitoreo de Clima", shortName = "ClimaSys")
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
        // Crear barra de navegación
        createHeader();

        // Crear contenido principal
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.setHeightFull();

        // Crear layout horizontal: panel izquierdo (dashboard) y derecho (menú)
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.setSpacing(false);
        mainLayout.setPadding(false);

        // Panel izquierdo (Dashboard - 75%)
        dashboardPanel = new DashboardPanel(zonaRepository, servicioPrediccion);
        dashboardPanel.setContaminacionRepository(contaminacionRepository);
        dashboardPanel.setWidth("75%");
        dashboardPanel.setHeight("100%");

        // Panel derecho (Menú - 25%)
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
        header.getStyle().set("border-bottom", "3px solid #2e7d32");
        header.setPadding(true);
        header.setSpacing(false);

        addToNavbar(header);
    }
}



