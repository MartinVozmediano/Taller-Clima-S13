# 📖 Sistema de Monitoreo de Calidad del Aire - Documentación Completa

## Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Componentes Principales](#componentes-principales)
5. [Modelos de Datos](#modelos-de-datos)
6. [Servicios](#servicios)
7. [Interfaz Gráfica (GUI)](#interfaz-gráfica-gui)
8. [Flujo de Datos](#flujo-de-datos)
9. [Cómo Ejecutar](#cómo-ejecutar)
10. [Funcionalidades Detalladas](#funcionalidades-detalladas)

---

##  Descripción General

**Sistema de Monitoreo de Calidad del Aire** es una aplicación web moderna que permite:

- **Monitorear zonas geográficas** con mediciones de contaminación en tiempo real
- **Registrar lecturas** de contaminantes (CO2, SO2, NO2, PM2.5)
- **Capturar factores climáticos** (temperatura, humedad, velocidad del viento)
- **Evaluar alertas** según límites de la OMS
- **Predecir niveles futuros** de contaminación usando algoritmos matemáticos ponderados
- **Generar reportes** históricos y finales de monitoreo

### Objetivo Principal
Proporcionar una herramienta integral para organizaciones ambientales, municipios o instituciones de salud que necesiten monitorear y predecir la calidad del aire.

---

## Tecnologías Utilizadas

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| **Backend** | Spring Boot | 4.0.7 |
| **Frontend** | Vaadin | 25.1.8 |
| **Base de Datos** | MongoDB | - |
| **Lenguaje** | Java | 21 |
| **Build Tool** | Maven | - |
| **Build Frontend** | Vaadin Maven Plugin | 25.1.8 |

### Características Clave de las Tecnologías

- **Spring Boot**: Facilita la creación de aplicaciones autónomas basadas en Spring
- **Vaadin**: Framework que permite crear UIs modernas en Java sin necesidad de HTML/JavaScript
- **MongoDB**: Base de datos NoSQL flexible para almacenar documentos JSON
- **Java 21**: Soporte a características modernas del lenguaje

---

## Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                   APLICACIÓN VAADIN (Frontend)               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  MainView (Contenedor Principal)                       │ │
│  │  ┌──────────────────────┬──────────────────────────┐   │ │
│  │  │  DashboardPanel      │    MenuPanel             │   │ │
│  │  │  (75% ancho)         │    (25% ancho)           │   │ │
│  │  │                      │                          │   │ │
│  │  │  - Tarjetas de Zonas │ - Crear Zona             │   │ │
│  │  │  - Información       │ - Ver Zonas              │   │ │
│  │  │  - Últimas mediciones│ - Editar Zona            │   │ │
│  │  │                      │ - Monitorear Límites     │   │ │
│  │  │                      │ - Realizar Predicción    │   │ │
│  │  │                      │ - Reportes Históricos    │   │ │
│  │  │                      │ - Reportes Finales       │   │ │
│  │  └──────────────────────┴──────────────────────────┘   │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                            ↓
            ┌───────────────────────────────┐
            │  Spring Boot Controllers/      │
            │  Services Layer               │
            │  ┌─────────────────────────┐  │
            │  │ ServicioPrediccion      │  │
            │  │ - Cálculos ponderados   │  │
            │  │ - Alertas OMS           │  │
            │  │ - Predicciones          │  │
            │  └─────────────────────────┘  │
            └───────────────────────────────┘
                            ↓
            ┌───────────────────────────────┐
            │  Repositories (Data Layer)    │
            │  ┌─────────────────────────┐  │
            │  │ ZonaRepository          │  │
            │  │ ContaminacionRepository │  │
            │  └─────────────────────────┘  │
            └───────────────────────────────┘
                            ↓
            ┌───────────────────────────────┐
            │  MongoDB Database             │
            │  - Colección: zonas           │
            │  - Colección: lecturas_       │
            │    contaminacion              │
            └───────────────────────────────┘
```

---

## Componentes Principales

### 1. **Application.java** (Punto de Entrada)
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.clima", "gui", "services", "repositories", "models"})
@EnableMongoRepositories(basePackages = "repositories")
public class Application implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
- **Rol**: Inicia la aplicación Spring Boot
- **Responsabilidades**:
  - Escanea componentes en múltiples paquetes
  - Habilita MongoDB
  - Configura Vaadin como frontend
  - Activa Push (comunicación en tiempo real servidor-cliente)

---

## Modelos de Datos

### 1. **Zona.java**
```java
@Document(collection = "zonas")
public class Zona {
    @Id private String id;
    private String nombre;
}
```

**Descripción**: Representa una zona geográfica a monitorear.

**Campos**:
- `id`: Identificador único (generado por MongoDB)
- `nombre`: Nombre descriptivo de la zona (ej: "Centro Histórico", "Puerto")

**Colección MongoDB**: `zonas`

---

### 2. **LecturaContaminacion.java**
```java
@Document(collection = "lecturas_contaminacion")
public class LecturaContaminacion {
    @Id private String id;
    private String zonaId;
    private LocalDateTime fechaHora;
    
    // Contaminantes
    private double co2;      // ppm
    private double so2;      // µg/m³
    private double no2;      // µg/m³
    private double pm25;     // µg/m³
    
    // Factores climáticos
    private double temperatura;    // °C
    private double velocidadViento; // km/h
    private double humedad;         // %
}
```

**Descripción**: Representa una medición de contaminación en una zona específica.

**Campos de Contaminantes**:
- `co2`: Dióxido de carbono (ppm - partes por millón)
- `so2`: Dióxido de azufre (µg/m³)
- `no2`: Dióxido de nitrógeno (µg/m³)
- `pm25`: Partículas menores a 2.5 micrómetros (µg/m³)

**Campos Climáticos**:
- `temperatura`: En grados Celsius
- `velocidadViento`: En kilómetros por hora
- `humedad`: En porcentaje (0-100%)

**Colección MongoDB**: `lecturas_contaminacion`

---

### 3. **FactoresClimaticos.java**
```java
public class FactoresClimaticos {
    private double velocidadViento;  // km/h
    private double humedad;           // %
    private double temperatura;       // °C
}
```

**Descripción**: Agrupa los factores climáticos para el cálculo de predicciones.

**Uso**: Se utiliza en el servicio de predicción para ajustar los niveles predichos.

---

### 4. **Prediccion.java**
```java
public class Prediccion {
    private String zonaId;
    private LocalDateTime fechaProyeccion;
    private double co2Predicho;
    private double so2Predicho;
    private double no2Predicho;
    private double pm25Predicho;
}
```

**Descripción**: Contiene la predicción de contaminantes para 24 horas en el futuro.

**Cálculo**: Se genera mediante el `ServicioPrediccion` usando matemática ponderada + factores climáticos.

---

## Servicios

### **ServicioPrediccion.java**

Este es el corazón lógico del sistema. Realiza tres operaciones principales:

#### 1. **Evaluación de Alertas OMS**

```java
public String evaluarAlertasOMS(LecturaContaminacion actual)
```

**Límites OMS Configurados**:
- CO2: ≤ 400.0 ppm
- SO2: ≤ 20.0 µg/m³
- NO2: ≤ 40.0 µg/m³
- PM2.5: ≤ 15.0 µg/m³

**Retorna**: 
- String con alertas específicas si hay excedencias
- "✓ Valores dentro de límites OMS" si está todo bien

**Ejemplo**:
```
(!!) CO2 excedido (450.5 ppm). (!!) PM2.5 excedido (22.3 µg/m³).
```

---

#### 2. **Cálculo de Promedio PM2.5**

```java
public double calcularPromedioPM25(List<LecturaContaminacion> lecturas)
```

**Propósito**: Calcula el promedio aritmético de PM2.5 de un conjunto de lecturas.

**Fórmula**:
```
Promedio PM2.5 = Σ(PM2.5) / cantidad de lecturas
```

---

#### 3. **Predicción de Niveles Futuros** (Algoritmo Clave)

```java
public Prediccion predecirNivelesFuturos(String zonaId, FactoresClimaticos clima)
```

**Algoritmo en 3 pasos**:

##### PASO 1: Obtener Histórico Ponderado
- Recupera lecturas de los últimos **30 días**
- Selecciona las últimas **10 lecturas** más recientes
- Asigna pesos decrecientes: registro más reciente = peso 1.0, más antiguo = peso 0.1

**Fórmula de peso**:
```
peso = (10 - posición) / 10
```

##### PASO 2: Calcular Promedios Ponderados
```java
co2Base = Σ(lecturas_recientes[i].co2 * peso[i]) / pesoTotal
so2Base = Σ(lecturas_recientes[i].so2 * peso[i]) / pesoTotal
no2Base = Σ(lecturas_recientes[i].no2 * peso[i]) / pesoTotal
pm25Base = Σ(lecturas_recientes[i].pm25 * peso[i]) / pesoTotal
```

##### PASO 3: Ajustar con Factores Climáticos

**Factor Viento** (dispersión de contaminantes):
```java
reduccion = min(0.2, velocidadViento * 0.04)
factorViento = max(0.8, 1.0 - reduccion)
```
- A mayor viento → menor contaminación (factor más bajo)
- Rango: 0.8 a 1.0
- Por cada 5 km/h de viento: -4% contaminación

**Factor Humedad** (concentración de partículas):
```java
factorHumedad = 0.9 + (humedad - 30) * 0.005  [si 30 ≤ humedad ≤ 90]
factorHumedad = 0.9  [si humedad < 30]
factorHumedad = 1.2  [si humedad > 90]
```
- Mayor humedad → mayor concentración de PM2.5
- Rango: 0.9 a 1.2

**Predicción Final**:
```
co2Predicho = co2Base * factorViento
so2Predicho = so2Base * factorViento
no2Predicho = no2Base * factorViento
pm25Predicho = pm25Base * factorHumedad
fechaProyeccion = ahora + 24 horas
```

---

## Interfaz Gráfica (GUI)

### **MainView.java** (Vista Principal)

- **Layout**: AppLayout de Vaadin
- **Estructura**:
  - **Header**: Título "🌍 Sistema de Monitoreo de Calidad del Aire"
  - **Contenido Principal**: Layout horizontal con:
    - **DashboardPanel** (75% ancho): Muestra tarjetas de zonas
    - **MenuPanel** (25% ancho): Menú lateral con opciones

---

### **DashboardPanel.java** (Panel de Zonas)

**Responsabilidades**:
- Mostrar todas las zonas registradas como tarjetas
- Cada tarjeta contiene:
  - Nombre de la zona
  - Última medición disponible
  - Indicadores visuales de calidad del aire
  - Botones para ver detalles, realizar predicciones, etc.

**Estilos**:
- Fondo verde claro (#f1f8e9)
- Bordes verdes (#c5e1a5)
- Tarjetas individuales con sombra y bordes redondeados

**Métodos Clave**:
- `refreshZones()`: Recarga la lista de zonas desde MongoDB
- `createZoneCard(Zona)`: Genera la tarjeta visual de una zona

---

### **MenuPanel.java** (Menú Lateral)

**Botones Disponibles**:

1. **➕ Crear Zona**
   - Abre `ZoneDialog`
   - Permite agregar una nueva zona a monitorear

2. **👁️ Ver Zonas**
   - Abre `ZonesListDialog`
   - Muestra lista completa de zonas con opciones

3. **✏️ Editar Zona**
   - Abre `ZonesEditDialog`
   - Permite modificar nombre/información de zonas existentes

4. **📡 Monitorear Límites**
   - Abre `MonitoringDialog`
   - Registra nuevas lecturas de contaminación
   - Ingresa factores climáticos

5. **🔮 Realizar Predicción**
   - Abre `PredictionDialog`
   - Ingresa factores climáticos actuales
   - Genera predicción para 24 horas

6. **📊 Reportes Históricos**
   - Abre `HistoryReportDialog`
   - Muestra gráficas y estadísticas de los últimos 30 días

7. **📋 Reporte Final**
   - Abre `FinalReportDialog`
   - Resumen completo del monitoreo

---

### **Diálogos Específicos**

#### **ZoneDialog.java** (Crear Zona)
- Campo de entrada: Nombre de la zona
- Validación de campos vacíos
- Al aceptar: guarda en MongoDB y actualiza dashboard

#### **MonitoringDialog.java** (Registrar Lecturas)
- Campos para cada contaminante (CO2, SO2, NO2, PM2.5)
- Campos para factores climáticos
- Selector de zona
- Validación de rangos
- Al aceptar: guarda `LecturaContaminacion` en MongoDB

#### **PredictionDialog.java** (Hacer Predicción)
- Ingresa factores climáticos actuales
- Selector de zona
- Al generar: llama a `ServicioPrediccion.predecirNivelesFuturos()`
- Muestra resultados predichos

#### **HistoryReportDialog.java** (Reportes)
- Recupera lecturas de los últimos 30 días
- Calcula estadísticas (promedio, máximo, mínimo)
- Muestra gráficas y tablas

#### **FinalReportDialog.java** (Reporte Final)
- Resumen global de todas las zonas
- Situación actual general
- Recomendaciones basadas en datos

---

## Flujo de Datos

### Flujo 1: Crear una Zona
```
Usuario → MenuPanel → ZoneDialog 
  → Valida → ZonaRepository.save() 
  → MongoDB (colección: zonas) 
  → DashboardPanel.refreshZones() 
  → Muestra tarjeta nueva
```

### Flujo 2: Registrar una Lectura
```
Usuario → MenuPanel → MonitoringDialog 
  → Ingresa datos de contaminación + clima
  → Valida → ContaminacionRepository.save() 
  → MongoDB (colección: lecturas_contaminacion)
  → ServicioPrediccion.evaluarAlertasOMS() 
  → Muestra alertas si las hay
```

### Flujo 3: Realizar una Predicción
```
Usuario → MenuPanel → PredictionDialog 
  → Ingresa factores climáticos actuales
  → ServicioPrediccion.predecirNivelesFuturos(zonaId, clima)
    ├─ obtenerHistorico30Dias(zonaId)
    ├─ Aplica pesos decrecientes (últimas 10 lecturas)
    ├─ Calcula promedios ponderados
    ├─ Aplica factor viento
    ├─ Aplica factor humedad
    └─ Retorna Prediccion
  → Muestra resultados predichos (CO2, SO2, NO2, PM2.5)
```

### Flujo 4: Ver Reportes
```
Usuario → MenuPanel → HistoryReportDialog/FinalReportDialog
  → ContaminacionRepository.findByZonaIdAndFechaHoraBetween()
  → MongoDB (últimas 30 días)
  → Calcula estadísticas
  → Muestra gráficas y tablas
```

---

## 🚀 Cómo Ejecutar

### Opción 1: Ejecución en Desarrollo
Desde la carpeta raíz del proyecto:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**Resultado**:
- Maven descargará dependencias (~30 segundos en primera ejecución)
- La aplicación se ejecutará en `http://localhost:8080`
- Abre el navegador en esa URL

### Opción 2: Compilar y Ejecutar

```bash
# Windows
.\mvnw.cmd package
java -jar target\prediccionclima-1.0-SNAPSHOT.jar

# Linux/Mac
./mvnw package
java -jar target/prediccionclima-1.0-SNAPSHOT.jar
```

### Opción 3: Con Hot Swap (Recarga en Vivo)

Instala el plugin de Vaadin en tu IDE:
- **IntelliJ IDEA**: Marketplace → Vaadin
- **VS Code**: Extensions → Vaadin
- **Eclipse**: Help → Install New Software → Vaadin plugin

Luego usa la opción "Debug using Hotswap Agent" para editar código sin reiniciar.

### Detener la Aplicación
```
Ctrl + C en la terminal
```

### Cambiar Puerto (si 8080 está en uso)
Edita `src/main/resources/application.properties`:
```properties
server.port=8081
```

---

## Funcionalidades Detalladas

### 1. **Gestión de Zonas**

#### Crear Zona
- Acceso: MenuPanel → ➕ Crear Zona
- Requiere: Nombre de la zona
- Validación: No permite nombres vacíos
- Almacenamiento: MongoDB colección `zonas`
- Resultado: Nueva zona aparece en el dashboard

#### Ver Zonas
- Acceso: MenuPanel → 👁️ Ver Zonas
- Muestra: Lista de todas las zonas registradas
- Opciones: Seleccionar, ver detalles

#### Editar Zona
- Acceso: MenuPanel → ✏️ Editar Zona
- Permite: Cambiar nombre de zona existente
- Validación: No permite nombres vacíos
- Actualización: Inmediata en MongoDB y dashboard

---

### 2. **Registro de Mediciones**

#### Monitorear Límites
- Acceso: MenuPanel → 📡 Monitorear Límites
- Datos requeridos:
  - Zona (selector)
  - CO2 (ppm)
  - SO2 (µg/m³)
  - NO2 (µg/m³)
  - PM2.5 (µg/m³)
  - Temperatura (°C)
  - Velocidad del viento (km/h)
  - Humedad (%)

- Validaciones:
  - Todos los campos son obligatorios
  - Valores numéricos válidos
  - Rango de humedad: 0-100%

- Proceso:
  1. Guarda `LecturaContaminacion` en MongoDB
  2. Evalúa límites OMS automáticamente
  3. Muestra alertas si hay excedencias
  4. Actualiza información en el dashboard

- Ejemplo de Alerta:
```
(!!) CO2 excedido (450.5 ppm). 
(!!) PM2.5 excedido (22.3 µg/m³).
```

---

### 3. **Predicciones**

#### Realizar Predicción
- Acceso: MenuPanel → 🔮 Realizar Predicción
- Datos requeridos:
  - Zona (selector)
  - Temperatura actual (°C)
  - Velocidad del viento (km/h)
  - Humedad (%)

- Proceso:
  1. Obtiene histórico de 30 días
  2. Aplica ponderación a últimas 10 lecturas
  3. Ajusta con factores climáticos
  4. Genera predicción para próximas 24 horas

- Resultados mostrados:
  - CO2 predicho (ppm)
  - SO2 predicho (µg/m³)
  - NO2 predicho (µg/m³)
  - PM2.5 predicho (µg/m³)
  - Comparación con límites OMS

- Ejemplo de Resultado:
```
PREDICCIÓN PARA 24 HORAS (25-Jun-2026 00:45)
CO2:    380.2 ppm      ✓ Dentro de límites
SO2:    18.5 µg/m³     ✓ Dentro de límites
NO2:    35.1 µg/m³     ✓ Dentro de límites
PM2.5:  12.8 µg/m³     ✓ Dentro de límites
```

---

### 4. **Reportes**

#### Reportes Históricos (últimos 30 días)
- Acceso: MenuPanel → 📊 Reportes Históricos
- Datos mostrados:
  - Gráficas de tendencia para cada contaminante
  - Promedios
  - Máximos y mínimos
  - Tabla con últimas mediciones

#### Reporte Final
- Acceso: MenuPanel → 📋 Reporte Final
- Contiene:
  - Resumen de todas las zonas
  - Situación actual general
  - Zonas críticas (con más excedencias)
  - Recomendaciones
  - Exportación de datos

---

## 🔌 Integración con MongoDB

### Configuración
- URL de conexión: Configurada en `application.properties`
- Base de datos: `clima` (por defecto)
- Colecciones automáticas:
  - `zonas`: Almacena definiciones de zonas
  - `lecturas_contaminacion`: Almacena mediciones

### Ejemplos de Documentos

**Documento Zona**:
```json
{
  "_id": "zona-001",
  "nombre": "Centro Histórico"
}
```

**Documento LecturaContaminacion**:
```json
{
  "_id": ObjectId("..."),
  "zonaId": "zona-001",
  "fechaHora": ISODate("2026-06-24T00:43:56Z"),
  "co2": 380.5,
  "so2": 15.3,
  "no2": 32.1,
  "pm25": 12.8,
  "temperatura": 22.5,
  "velocidadViento": 8.5,
  "humedad": 65.0
}
```

---

## Manejo de Excepciones

### ExceptionHandler.java
- Método: `handleGeneralException(Exception e)`
- Acción: Muestra diálogos de error amigables
- Evita crashes no controlados
- Mantiene log de errores en `app.log`

### Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| MongoException | MongoDB no está disponible | Iniciar servicio MongoDB |
| NullPointerException en histórico | No hay datos de 30 días | Registrar lecturas primero |
| Puerto 8080 en uso | Otra aplicación usa el puerto | Cambiar puerto en properties |
| ClassNotFoundException | Clases mal mapeadas | Verificar @ComponentScan |

---

## 🎯 Casos de Uso Principales

### Caso 1: Monitoreo Diario de una Ciudad
1. Crear zona "Centro Urbano"
2. Cada 4 horas: registrar mediciones
3. Cada 12 horas: generar predicciones
4. Diariamente: revisar reportes

### Caso 2: Alerta por Contaminación
1. Sistema registra lectura con CO2 = 520 ppm
2. Sistema evalúa límites OMS (400 ppm)
3. Sistema muestra alerta: "(!!) CO2 excedido (520 ppm)"
4. Usuario puede generar predicción para anticipar tendencia

### Caso 3: Análisis Tendencias Mensuales
1. Acceder a Reportes Históricos
2. Analizar gráficas de 30 días
3. Identificar horarios/días críticos
4. Generar recomendaciones basadas en datos

---

## Características Destacadas

**Sistema de Ponderación**: Los últimos datos tienen más peso  
**Factores Climáticos**: Considera viento y humedad en predicciones  
**Límites OMS**: Alertas basadas en estándares internacionales  
**Interfaz Moderna**: Vaadin con diseño responsive  
**Persistencia MongoDB**: Datos duraderos y escalables  
**Manejo de Errores**: Excepciones controladas y logs  
**Push en Tiempo Real**: Actualizaciones servidor-cliente  
**Reportes Completos**: Históricos y finales con gráficas  

---

## Soporte y Contacto

Para más información sobre las tecnologías:
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Vaadin**: https://vaadin.com/docs
- **MongoDB**: https://docs.mongodb.com
- **Java 21**: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html

---

**Última Actualización**: Junio 24, 2026  
**Versión**: 1.0-SNAPSHOT  
**Estado**: Producción
