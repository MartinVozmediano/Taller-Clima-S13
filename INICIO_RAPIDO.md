🚀 GUÍA DE INICIO RÁPIDO - SISTEMA DE MONITOREO DE CLIMA

═══════════════════════════════════════════════════════════════

REQUISITOS PREVIOS:
✓ Java 21 o superior
✓ Maven 3.6+
✓ MongoDB cuenta en mongodb.com (ya configurado)
✓ Conexión a internet

═══════════════════════════════════════════════════════════════

PASO 1: VERIFICAR CONEXIÓN MONGODB
───────────────────────────────────────

La conexión ya está configurada en:
📄 src/main/resources/application.properties

Línea:
spring.data.mongodb.uri=mongodb+srv://martinvozmeudla:Martokun32@cluster0.ihk5zwd.mongodb.net/sistema_clima?appName=Cluster0

✅ La conexión está LISTA para usar

═══════════════════════════════════════════════════════════════

PASO 2: COMPILAR EL PROYECTO
───────────────────────────────────────

Abrir terminal en: Taller-Clima-S13

Ejecutar:
> mvn clean install

Esto descargará todas las dependencias y compilará el proyecto.

═══════════════════════════════════════════════════════════════

PASO 3: INICIAR LA APLICACIÓN
───────────────────────────────────────

Ejecutar:
> mvn spring-boot:run

Esperaré hasta ver en consola:
"Started Application in X.XXX seconds"

═══════════════════════════════════════════════════════════════

PASO 4: ABRIR EN NAVEGADOR
───────────────────────────────────────

Dirección:
http://localhost:8080

¡La aplicación estará lista para usar!

═══════════════════════════════════════════════════════════════

PRIMERA VEZ - CREAR UNA ZONA EJEMPLO
───────────────────────────────────────

1. En el panel derecho, hace clic en "➕ Crear Zona"
2. Ingrese:
   - ID: Centro
   - Nombre: Centro Histórico
3. Haga clic en "Guardar"
4. La zona aparecerá en el panel izquierdo

═══════════════════════════════════════════════════════════════

ESTRUCTURA DE LA INTERFAZ
───────────────────────────────────────

┌─────────────────────────────────────────┐
│  🌍 Sistema de Monitoreo de Clima      │ ← Header
├──────────────────────────┬──────────────┤
│                          │ 🔧 MENÚ      │
│   📊 Monitoreo Zonas     │              │
│   (Dashboard)            │ ➕ Crear     │
│                          │ 👁️ Ver      │
│  [Zona Cards]            │ ✏️ Editar   │
│  [Colores]               │              │
│  [PM2.5 Actual]          │ 📡 Monitor. │
│                          │ 🔮 Predicción
│   75% del ancho          │              │
│                          │ 📈 Histórico
│                          │ 📋 Final    │
│                          │              │
│                          │   25% ancho  │
└──────────────────────────┴──────────────┘

═══════════════════════════════════════════════════════════════

MENÚ DE FUNCIONES - GUÍA RÁPIDA
───────────────────────────────────────

1️⃣ ➕ CREAR ZONA
   └─ Agregar una nueva zona para monitorear

2️⃣ 👁️ VER ZONAS
   └─ Listar todas las zonas registradas

3️⃣ ✏️ EDITAR ZONA
   └─ Modificar o eliminar zonas existentes

4️⃣ 📡 MONITOREAR LÍMITES
   └─ Ver niveles actuales de contaminantes
   └─ Comparar con límites OMS

5️⃣ 🔮 PREDICCIÓN 24h
   └─ Ingresar factores climáticos
   └─ Ver predicción para próximas 24 horas

6️⃣ 📈 REPORTE HISTÓRICO
   └─ Análisis de últimos 30 días
   └─ Gráficos y estadísticas

7️⃣ 📋 REPORTE FINAL
   └─ Resumen completo del sistema
   └─ Estadísticas globales

═══════════════════════════════════════════════════════════════

PRUEBA COMPLETA - FLUJO RECOMENDADO
───────────────────────────────────────

1. CREAR 2-3 ZONAS
   Menu > ➕ Crear Zona
   Ej: Centro, Norte, Sur

2. MONITOREAR LÍMITES
   Menu > 📡 Monitorear Límites
   Seleccionar una zona
   Ver datos actuales

3. HACER PREDICCIÓN
   Menu > 🔮 Predicción 24h
   Ingresar: Temperatura, Viento, Humedad
   Clickear "Predecir"

4. VER REPORTES
   Menu > 📈 Reporte Histórico
   Menu > 📋 Reporte Final

═══════════════════════════════════════════════════════════════

COLORES DE ZONAS - SIGNIFICADO
───────────────────────────────────────

🟢 VERDE CLARO (PM2.5 ≤ 12)
   └─ ✓ Calidad Buena

🟡 AMARILLO (12 < PM2.5 ≤ 35.4)
   └─ ○ Calidad Moderada

🟠 NARANJA (35.4 < PM2.5 ≤ 55.4)
   └─ ⚠ Grupos Sensibles

🔴 ROJO (PM2.5 > 55.4)
   └─ ✗ No Saludable

═══════════════════════════════════════════════════════════════

LIMITES OMS UTILIZADOS
───────────────────────────────────────

CO2:   400.0 ppm
SO2:    20.0 µg/m³
NO2:    40.0 µg/m³
PM2.5:  15.0 µg/m³

═══════════════════════════════════════════════════════════════

SOLUCIÓN DE PROBLEMAS
───────────────────────────────────────

❌ "Connection refused" a MongoDB
   └─ Verificar conexión a internet
   └─ Verificar credenciales en application.properties
   └─ Esperar 30 segundos y reintentar

❌ Puerto 8080 ya en uso
   └─ Cambiar puerto en application.properties:
      server.port=8081

❌ Errores de validación en formularios
   └─ Verificar que los datos sean válidos
   └─ No usar caracteres especiales en ID
   └─ IDs deben ser únicos

❌ No aparecen datos en dashboard
   └─ Las zonas aparecerán después de crear y guardar
   └─ Refrescar la página (F5)

═══════════════════════════════════════════════════════════════

DATOS DE EJEMPLO DEL ARCHIVO
───────────────────────────────────────

En historicos.txt encontrará datos de 5 zonas:
- Centro Histórico
- Calderón
- Valle de los Chillos
- La Bota
- San Bartolo

Puede usar estos nombres como referencia.

═══════════════════════════════════════════════════════════════

ARCHIVO DE CONFIGURACIÓN IMPORTANTE
───────────────────────────────────────

📄 src/main/resources/application.properties

Contiene:
- Puerto del servidor (8080)
- Conexión MongoDB
- Database name
- Vaadin settings

NO EDITAR A MENOS QUE SEPA QUÉ ESTÁ HACIENDO

═══════════════════════════════════════════════════════════════

DOCUMENTACIÓN ADICIONAL
───────────────────────────────────────

Para más información, ver:
📖 GUI_DOCUMENTATION.md      - Documentación completa
📋 IMPLEMENTACION_RESUMEN.txt - Resumen técnico
📄 README.md                  - Información general

═══════════════════════════════════════════════════════════════

¿LISTA PARA EMPEZAR?
───────────────────────────────────────

Ejecute en terminal:
> mvn spring-boot:run

Abra navegador en:
http://localhost:8080

¡Disfrute del Sistema de Monitoreo de Clima! 🌍✨

═══════════════════════════════════════════════════════════════

