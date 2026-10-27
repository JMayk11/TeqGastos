# TeqGastos - Sistema de Rendición y Control de Gastos para Terreno

TeqGastos es una solución móvil nativa desarrollada en Kotlin para Android, diseñada para optimizar y auditar la rendición de fondos operativos en terreno. La plataforma permite a los usuarios registrar comprobantes con validación de datos y facilita a la jefatura la revisión, aprobación o rechazar de solicitudes en tiempo real mediante persistencia local síncrona y reactiva.

---

## Funcionalidades Principales

- Autenticación y Control de Acceso por Roles: Flujo de acceso diferenciado para perfil Técnico (solicitante) y Jefatura (aprobador).
- Registro Integral de Rendiciones: Captura de comprobantes con selección de categoría, centro de costos, monto, descripción y geolocalización GPS.
- Trazabilidad y Autoría de Gastos: Vinculación automática e inmutable del nombre del usuario autenticado a la entidad del gasto generado para rendición de cuentas.
- Módulo de Aprobaciones: Interfaz exclusiva para jefatura que permite revisar las rendiciones pendientes y actualizar su estado a Aprobado o Rechazado en tiempo real.
- Sistema de Accesibilidad y Tema Dinámico: Soporte nativo para modo claro y modo oscuro (Dark Mode) mediante Material3 ColorScheme, junto con ajuste de escala del tamaño de fuente.
- Persistencia Local y Control de Esquema: Almacenamiento local mediante Room Database (Versión 2) estructurado con fallbackToDestructiveMigration para mantener la integridad de los datos durante la evolución del esquema.

---

## Tecnologías y Librerías Utilizadas

- Lenguaje: Kotlin 1.9+
- Interfaz de Usuario: Jetpack Compose con Material Design 3
- Navegación: Jetpack Navigation Compose
- Arquitectura: MVVM (Model-View-ViewModel) + Clean Architecture
- Concurrencia: Kotlin Coroutines y StateFlow / SharedFlow
- Persistencia de Datos: Room Database (DAO, Entities)
- Inyección / Factores: ViewModelProvider.Factory customizado

---

## Estructura del Proyecto

```text
com.example.teqgastos/
├── data/
│   ├── AppDatabase.kt          # Instancia singleton de la base de datos Room (v2)
│   ├── GastoDao.kt             # Consultas y operaciones CRUD de la entidad Gasto
│   └── UsuarioDao.kt           # Consultas de autenticación y datos de usuario
├── domain/
│   ├── Gasto.kt                # Entidad de gasto (incluye el campo usuario)
│   └── Usuario.kt              # Entidad de usuario y roles
├── ui/
│   ├── components/             # Componentes reutilizables (BottomNavigationBar, etc.)
│   ├── screens/                # Pantallas (LoginScreen, HomeScreen, AprobacionesScreen, NuevoGastoScreen, etc.)
│   ├── theme/                  # Tema, colores dinámicos (Light/Dark) y tipografía
│   └── viewmodel/              # GastoViewModel y lógica de estado
└── MainActivity.kt             # Punto de entrada y gestión del NavHost de la aplicación

Requisitos e Instalación
Clonar el repositorio:

Bash
git clone [https://github.com/JMayk11/TeqGastos.git](https://github.com/JMayk11/TeqGastos.git)
Abrir el proyecto en Android Studio (Ladybug, Koala o superior).

Sincronizar las dependencias mediante Gradle.

Ejecutar la aplicación en un emulador o dispositivo físico con Android 8.0 (API Nivel 26) o superior.
