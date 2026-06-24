package com.clima;

import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;

@SpringBootApplication
@ComponentScan(basePackages = {"com.clima", "gui", "services", "repositories", "models"})
@EnableMongoRepositories(basePackages = "repositories")
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@StyleSheet("styles.css")
@Push
@PWA(name = "Sistema de Monitoreo de Clima", shortName = "ClimaSys")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
