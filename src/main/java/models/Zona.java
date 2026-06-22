package models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "zonas")
public class Zona {

    @Id
    private String id;

    private String nombre;

    public Zona(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters para atributos
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
