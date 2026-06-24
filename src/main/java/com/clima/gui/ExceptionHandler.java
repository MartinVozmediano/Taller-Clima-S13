package com.clima.gui;

import com.vaadin.copilot.shaded.io.netty.util.concurrent.ImmediateEventExecutor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class ExceptionHandler {

    public static void handleDatabaseException(Exception e) {
        String message;

        if (e.getMessage().contains("Connection refused")) {
            message = "Error de conexión a MongoDB: Verifique la conexión";
        } else if (e.getMessage().contains("Authentication failed")) {
            message = "Error de autenticación en MongoDB: Credenciales inválidas";
        } else if (e.getMessage().contains("Network error")) {
            message = "Error de red: No se puede conectar a MongoDB";
        } else if (e.getMessage().contains("Duplicate")) {
            message = "Error: El registro ya existe";
        } else if (e.getMessage().contains("Invalid")) {
            message = "Error: Datos inválidos";
        } else {
            message = "Error en base de datos: " + e.getMessage();
        }

        showErrorNotification(message);
    }

    public static void handleValidationException(String message) {
        showErrorNotification("Validación: " + message);
    }

    public static void handleGeneralException(Exception e) {
        showErrorNotification("Error: " + (e.getMessage() != null ? e.getMessage() : "Operación fallida"));
    }

    public static void showSuccessNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
        notification.setDuration(3000);
    }

    public static void showErrorNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(4000);
    }

    public static void showWarningNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        notification.setDuration(4000);
    }
}

