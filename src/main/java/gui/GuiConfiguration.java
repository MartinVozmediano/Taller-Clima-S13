package gui;

import repositories.ContaminacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class GuiConfiguration {

    @Autowired
    private ContaminacionRepository contaminacionRepository;

    private static GuiConfiguration instance;

    @PostConstruct
    public void init() {
        GuiConfiguration.instance = this;
    }

    public static ContaminacionRepository getContaminacionRepository() {
        return instance != null ? instance.contaminacionRepository : null;
    }
}

