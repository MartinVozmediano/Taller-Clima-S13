package com.clima.gui;

import com.vaadin.flow.component.notification.Notification;

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
        Notification notification = new Notification(message, 3000);
        notification.getStyle().set("background-color", "#c8e6c9");
        notification.getStyle().set("color", "#1b5e20");
        notification.open();
    }

    public static void showErrorNotification(String message) {
        Notification notification = new Notification(message, 4000);
        notification.getStyle().set("background-color", "#ffcdd2");
        notification.getStyle().set("color", "#c62828");
        notification.open();
    }

    public static void showWarningNotification(String message) {
        Notification notification = new Notification(message, 3500);
        notification.getStyle().set("background-color", "#fff3e0");
        notification.getStyle().set("color", "#f57f17");
        notification.open();
    }
}

