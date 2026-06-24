package gui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import models.Zona;
import repositories.ZonaRepository;

public class ZonesListDialog extends Dialog {

    private final ZonaRepository zonaRepository;
    private final DashboardPanel dashboardPanel;

    public ZonesListDialog(ZonaRepository zonaRepository, DashboardPanel dashboardPanel) {
        this.zonaRepository = zonaRepository;
        this.dashboardPanel = dashboardPanel;

        setHeaderTitle("👁️ Zonas Registradas");
        setWidth("600px");
        setHeight("500px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        setupContent();
    }

    private void setupContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        try {
            Grid<Zona> grid = new Grid<>(Zona.class);
            grid.setColumns("id", "nombre");
            grid.getColumnByKey("id").setHeader("ID");
            grid.getColumnByKey("nombre").setHeader("Nombre");
            grid.setWidthFull();
            grid.setHeightFull();

            grid.setItems(zonaRepository.findAll());
            grid.getStyle().set("border", "1px solid #c5e1a5");

            layout.add(grid);

            Button closeButton = new Button("Cerrar");
            closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeButton.addClickListener(event -> close());

            layout.add(closeButton);
            layout.expand(grid);

        } catch (Exception e) {
            layout.add(createErrorComponent("Error al cargar las zonas: " + e.getMessage()));
        }

        add(layout);
    }

    private com.vaadin.flow.component.html.Div createErrorComponent(String message) {
        com.vaadin.flow.component.html.Div div = new com.vaadin.flow.component.html.Div();
        div.setText(message);
        div.getStyle().set("color", "#c62828");
        div.getStyle().set("padding", "10px");
        div.getStyle().set("background-color", "#ffebee");
        return div;
    }
}

