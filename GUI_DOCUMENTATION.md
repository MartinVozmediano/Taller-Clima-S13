# 🌍 Sistema de Monitoreo de Calidad del Aire

## Descripción General

Una aplicación web interactiva construida con **Vaadin** y **Spring Boot** que permite monitorear, predecir y analizar la calidad del aire en diferentes zonas geográficas. La aplicación integra datos en tiempo real con **MongoDB** para almacenamiento persistente.

## Características Principales

### 1. **Dashboard Principal** (Panel Izquierdo - 75%)
- Visualización de todas las zonas registradas
- Cards interactivas que muestran el nivel actual de PM2.5
- Codificación de colores según calidad del aire:
  - 🟢 **Verde claro**: Buena calidad (PM2.5 ≤ 12)
  - 🟡 **Amarillo**: Calidad moderada (12 < PM2.5 ≤ 35.4)
  - 🟠 **Naranja**: Grupos sensibles (35.4 < PM2.5 ≤ 55.4)
  - 🔴 **Rojo**: No saludable (PM2.5 > 55.4)

### 2. **Panel de Menú** (Panel Derecho - 25%)

#### Gestión de Zonas:
- **➕ Crear Zona**: Ingresar ID y nombre de nueva zona
- **👁️ Ver Zonas**: Listar todas las zonas registradas
- **✏️ Editar Zona**: Modificar nombre o eliminar zonas

#### Monitoreo y Predicción:
- **📡 Monitorear Límites**: Ver niveles actuales de contaminantes (CO2, SO2, NO2, PM2.5)
- **🔮 Predicción 24h**: Predecir niveles para las próximas 24 horas basado en:
  - Histórico de los últimos 30 días
  - Factores climáticos actuales (temperatura, viento, humedad)

#### Reportes:
- **📈 Reporte Histórico**: Análisis de 30 días con estadísticas (promedio, máximo, mínimo)
- **📋 Reporte Final**: Resumen completo del sistema incluyendo:
  - Zonas monitoreadas
  - Estadísticas globales
  - Total de lecturas registradas

## Configuración de MongoDB

La conexión a MongoDB está configurada en `application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://martinvozmeudla:Martokun32@cluster0.ihk5zwd.mongodb.net/sistema_clima?appName=Cluster0
spring.data.mongodb.database=sistema_clima
```

### Colecciones Principales:
- **zonas**: Almacena información de las zonas geográficas
- **lecturas_contaminacion**: Registra mediciones de contaminantes

## Algoritmo de Predicción

La predicción combina dos estrategias:

1. **Análisis Histórico Ponderado**
   - Utiliza los últimos 10 registros del histórico de 30 días
   - Asigna mayor peso a datos más recientes

2. **Ajuste por Factores Climáticos**
   - **Velocidad de viento**: Reduce contaminación (factor 0.8-1.0)
   - **Humedad**: Aumenta PM2.5 (factor 0.9-1.2)

## Paleta de Colores

El sistema utiliza una paleta de colores verdes y claros para facilitar la visualización:

```css
--primary-green: #2e7d32
--light-green: #43a047
--lighter-green: #66bb6a
--very-light-green: #a5d6a7
--pale-green: #c8e6c9
--off-white: #f1f8e9
```

## Manejo de Excepciones

El sistema incluye un gestor centralizado de excepciones (`ExceptionHandler`) que categoriza errores:

- **Errores de Base de Datos**: Conexión, autenticación, duplicados
- **Errores de Validación**: Datos inválidos o incompletos
- **Errores Generales**: Operaciones fallidas

## Modelo de Datos

### Zona
```java
{
  id: String,          // Identificador único
  nombre: String       // Nombre descriptivo
}
```

### LecturaContaminacion
```java
{
  id: String,
  zonaId: String,
  fechaHora: LocalDateTime,
  co2: Double,         // ppm
  so2: Double,         // µg/m³
  no2: Double,         // µg/m³
  pm25: Double,        // µg/m³
  temperatura: Double, // °C
  velocidadViento: Double,  // km/h
  humedad: Double      // %
}
```

## Límites OMS

Los límites de calidad del aire según la OMS utilizados:

- **CO2**: 400.0 ppm
- **SO2**: 20.0 µg/m³
- **NO2**: 40.0 µg/m³
- **PM2.5**: 15.0 µg/m³

## Requisitos del Sistema

- Java 21+
- Maven 3.6+
- MongoDB 4.4+
- Navegador web moderno (Chrome, Firefox, Safari, Edge)

## Instalación y Ejecución

### 1. Clonar o descargar el proyecto
```bash
cd Taller-Clima-S13
```

### 2. Compilar con Maven
```bash
mvn clean install
```

### 3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 4. Acceder a la aplicación
```
http://localhost:8080
```

## Arquitectura

### Paquetes
- **com.clima.gui**: Componentes visuales (MainView, Dialogs, Panels)
- **services**: Lógica de negocio (ServicioPrediccion)
- **repositories**: Acceso a datos (MongoDB)
- **models**: Entidades de datos

### Estructura de Directorios
```
src/main/
├── java/
│   ├── com/clima/
│   │   └── Application.java
│   ├── com.clima.gui/
│   │   ├── MainView.java
│   │   ├── DashboardPanel.java
│   │   ├── MenuPanel.java
│   │   ├── ZoneDialog.java
│   │   ├── MonitoringDialog.java
│   │   ├── PredictionDialog.java
│   │   ├── HistoryReportDialog.java
│   │   └── FinalReportDialog.java
│   ├── services/
│   │   └── ServicioPrediccion.java
│   ├── repositories/
│   │   ├── ZonaRepository.java
│   │   └── ContaminacionRepository.java
│   └── models/
│       ├── Zona.java
│       ├── LecturaContaminacion.java
│       ├── Prediccion.java
│       └── FactoresClimaticos.java
└── resources/
    ├── application.properties
    └── META-INF/resources/
        └── styles.css
```

## Uso de Datos Históricos

El archivo `historicos.txt` contiene datos iniciales de PM2.5 para 5 zonas:
- Centro Histórico
- Calderón
- Valle de los Chillos
- La Bota
- San Bartolo

Estos datos pueden ser cargados en la base de datos para pruebas iniciales.

## Características de Seguridad

- ✅ Validación de entrada en todos los formularios
- ✅ Manejo robusto de excepciones
- ✅ Conexión segura a MongoDB con credenciales
- ✅ Notificaciones de error clara al usuario

## Mejoras Futuras

- Gráficos de tendencias históricas
- Exportación de reportes en PDF/Excel
- Alertas automáticas por email
- Integración con sistemas de pronóstico del clima
- Análisis machine learning para predicciones mejoradas

## Autor

Sistema de Monitoreo de Calidad del Aire - Programación II

## Licencia

Ver archivo LICENSE.md

