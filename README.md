# Banco BanQuito - Core Bancario

Backend del **Core Bancario Banco BanQuito**, desarrollado con **Java + Spring Boot + Maven + JPA/Hibernate + MariaDB**.

Este proyecto implementa la base programable del Core, tomando como fuente principal:

- Modelo físico definitivo **Core v4 - MariaDB**.
- Documento de contratos REST del Core por dominios.
- Requisitos funcionales del Core Bancario.
- Requisitos funcionales del Switch de Pagos Masivos que dependen del Core.
- Guía de trazabilidad de requisitos y base de datos.
- Directrices del docente sobre programación con Spring Boot, JPA, enums, Lombok, relaciones y estructura por capas.

---

## 1. Objetivo del proyecto

El objetivo del Core Bancario es actuar como **fuente única de verdad** para:

- Clientes naturales y jurídicos.
- Sucursales.
- Cuentas bancarias.
- Estados de cuenta.
- Saldos contables y disponibles.
- Bloqueos de fondos.
- Transacciones financieras.
- Cuentas institucionales del banco.
- Auditoría y trazabilidad.
- Integración interna con el Switch de Pagos Masivos.

El Core no debe ser una simple aplicación CRUD. Debe funcionar como un componente transaccional confiable, con reglas de integridad, idempotencia, auditoría y control de concurrencia.

---

## 2. Stack técnico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build tool | Maven |
| Persistencia | Spring Data JPA / Hibernate |
| Base de datos | MariaDB |
| Driver BD | MariaDB JDBC/R2DBC Driver según configuración del proyecto |
| Validación | Spring Validation |
| Observabilidad | Spring Boot Actuator |
| Desarrollo local | Spring Boot DevTools |
| Reducción de boilerplate | Lombok, usado con criterio |

---

## 3. Dependencias principales

El proyecto utiliza las siguientes dependencias base:

```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation
mariadb-java-client
spring-boot-starter-actuator
spring-boot-devtools
lombok
spring-boot-starter-test
```

### Criterio de dependencias

- `Spring Web`: creación de API REST.
- `Spring Data JPA`: persistencia y repositorios.
- `MariaDB Driver`: conexión con MariaDB.
- `Validation`: validación de DTOs con `@NotNull`, `@NotBlank`, `@Size`, `@Email`, etc.
- `Actuator`: endpoints técnicos de salud y monitoreo.
- `DevTools`: mejora de experiencia en desarrollo local.
- `Lombok`: solo para getters/setters en entidades y constructor injection en servicios/controladores.

---

## 4. Arquitectura general

El proyecto sigue un enfoque de **monolito modular por dominios internos**.

No se diseñó como microservicios independientes ni como arquitectura con API Gateway. El Core se comunica con el Switch mediante **APIs REST internas**, sin acceso directo del Switch a la base de datos MariaDB.

```text
Banca Web Empresas / Switch
        ↓ REST interno
Core Bancario
        ↓ JPA/Hibernate
MariaDB Core
```

### Principios arquitectónicos

- El Core es dueño de clientes, cuentas, saldos y transacciones.
- El Switch no accede directamente a MariaDB.
- El Switch llama endpoints internos del Core.
- No existen foreign keys físicas entre Core y Switch porque usan bases distintas.
- La comunicación se rastrea con UUIDs, referencias externas y correlation IDs.
- La lógica bancaria vive en servicios, no en controladores.

---

## 5. Estructura de paquetes

El paquete raíz del Core es:

```text
com.banquito.core
```

La organización se realiza por dominios internos:

```text
com.banquito.core
├── accounts
├── audit
├── branches
├── customers
├── institutional
├── integration
│   └── switchapi
├── parameters
├── security
├── shared
└── transactions
```

Dentro de cada dominio se aplica la estructura por capas:

```text
controller
    Capa de presentación REST.

dto
    request
        Objetos de entrada de API.
    response
        Objetos de salida de API.

enums
    Enumeraciones alineadas a CHECKs, estados y tipos de BD.

mapper
    Conversión Entity ↔ DTO.

model
    Entidades JPA mapeadas a tablas físicas.

repository
    Interfaces Spring Data JPA.

service
    Lógica de negocio y reglas del dominio.
```

### Ejemplo por dominio

```text
com.banquito.core.customers
├── controller
├── dto
│   ├── request
│   └── response
├── enums
├── mapper
├── model
├── repository
└── service
```

---

## 6. Dominios del Core

### 6.1. branches

Responsable de sucursales.

Entidades principales:

- `Sucursal`

Uso:

- Apertura de cuentas.
- Asociación de usuarios internos.
- Reportes por agencia.

---

### 6.2. customers

Responsable de clientes naturales y jurídicos.

Entidades principales:

- `Cliente`
- `SubtipoCliente`

Reglas relevantes:

- Un cliente puede ser `NATURAL` o `JURIDICO`.
- Una empresa debe poder tener representante legal.
- Solo clientes jurídicos pueden tener activo el servicio de pagos masivos.
- El subtipo del cliente debe coincidir con el tipo de cliente.

---

### 6.3. accounts

Responsable de cuentas, estados, bloqueos y disponibilidad.

Entidades principales:

- `Cuenta`
- `SubtipoCuenta`
- `BloqueoCuenta`
- `HistorialEstadoCuenta`

Reglas relevantes:

- Toda cuenta pertenece a un cliente, una sucursal y un subtipo de cuenta.
- El saldo disponible puede diferir del saldo contable por bloqueos.
- Los cambios de estado deben registrarse en historial.
- La cuenta puede marcarse como favorita para pagos masivos por SFTP.
- Puede existir sobregiro controlado para cobro de comisiones autorizado.

---

### 6.4. transactions

Responsable del motor transaccional y movimientos financieros.

Entidades principales:

- `SubtipoTransaccion`
- `TransaccionCuenta`
- `TransaccionInstitucional`

Reglas relevantes:

- Toda transacción debe tener UUID.
- Se debe controlar idempotencia por cuenta + UUID + fecha de negocio.
- Se registra saldo resultante posterior a la operación.
- Las transacciones financieras se consideran históricas e inmutables.
- El motor transaccional debe operar bajo transacciones ACID.

---

### 6.5. institutional

Responsable de cuentas internas del banco.

Entidades principales:

- `CuentaInstitucional`

Uso:

- Ingresos por servicios masivos.
- Pasivos de IVA retenido.
- Liquidaciones generadas por el Switch.

---

### 6.6. security

Responsable de credenciales web y usuarios internos.

Entidades principales:

- `CredencialWeb`
- `UsuarioCore`

Reglas relevantes:

- `CredencialWeb` representa usuarios de Banca Web Empresas.
- `UsuarioCore` representa operadores internos del banco.
- No se deben mezclar usuarios empresariales con usuarios internos.
- Los hashes de contraseña no deben exponerse en DTOs ni logs.

---

### 6.7. parameters

Responsable de parámetros operativos y feriados.

Entidades principales:

- `ParametroCore`
- `Feriado`

Uso:

- Configuración operativa.
- Calendario para cálculo de días hábiles.
- Soporte a reglas de horario y encolamiento del Switch.

---

### 6.8. audit

Responsable de auditoría transversal del Core.

Entidades principales:

- `AuditoriaEvento`

Uso:

- Registro de acciones críticas.
- Trazabilidad de cambios.
- Control de usuario, canal, entidad y resultado.

---

### 6.9. integration.switchapi

Responsable de servicios REST internos consumidos por el Switch.

Casos principales:

- Validar empresa por RUC.
- Validar si la empresa tiene pagos masivos activo.
- Consultar disponibilidad de cuenta matriz.
- Validar cuenta destino.
- Ejecutar transferencia por línea de pago.
- Liquidar comisión e IVA.

---

### 6.10. shared

Contiene componentes transversales reutilizables:

- Configuración.
- Excepciones.
- Respuestas estándar.
- Utilidades.

---

## 7. Base de datos

Base de datos final del Core:

```text
MariaDB
modeloFisicoBD_Core_v4_mariadb.sql
```

Tablas principales:

```text
SUCURSAL
FERIADO
PARAMETRO_CORE
SUBTIPO_CLIENTE
CLIENTE
CREDENCIAL_WEB
USUARIO_CORE
SUBTIPO_CUENTA
CUENTA
BLOQUEO_CUENTA
HISTORIAL_ESTADO_CUENTA
SUBTIPO_TRANSACCION
TRANSACCION_CUENTA
CUENTA_INSTITUCIONAL
TRANSACCION_INSTITUCIONAL
AUDITORIA_EVENTO
```

---

## 8. Configuración local

Archivo principal:

```text
src/main/resources/application.properties
```

Ejemplo de configuración:

```properties
spring.application.name=banquito-core

spring.datasource.url=jdbc:mariadb://localhost:3306/banquito_core
spring.datasource.username=root
spring.datasource.password=admin
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect

management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when_authorized
```

> Recomendación: mantener `spring.jpa.hibernate.ddl-auto=none` porque la estructura debe venir del script SQL físico validado, no ser generada automáticamente por Hibernate.

---

## 9. Instalación y ejecución

### 9.1. Requisitos previos

- Java 21.
- Maven.
- MariaDB instalado y en ejecución.
- IntelliJ IDEA.
- Postman o herramienta equivalente para probar APIs.

### 9.2. Crear base de datos

Ejecutar el script:

```text
modeloFisicoBD_Core_v4_mariadb.sql
```

Desde cliente MariaDB:

```bash
mysql -u root -p < src/main/resources/modeloFisicoBD_Core_v4_mariadb.sql
```

O desde una herramienta gráfica como DBeaver, HeidiSQL, DataGrip o MySQL Workbench.

### 9.3. Ejecutar la aplicación

Desde terminal:

```bash
mvn clean spring-boot:run
```

Desde IntelliJ IDEA:

1. Abrir el proyecto como Maven.
2. Esperar descarga de dependencias.
3. Revisar `application.properties`.
4. Ejecutar `CoreApplication`.

### 9.4. Validar salud de la aplicación

```http
GET http://localhost:8080/actuator/health
```

---

## 10. Endpoints principales

### Clientes

```http
GET  /api/core/clientes
POST /api/core/clientes
GET  /api/core/clientes/{id}
GET  /api/core/clientes/identificacion/{identificacion}
```

### Cuentas

```http
GET   /api/core/cuentas
POST  /api/core/cuentas
GET   /api/core/cuentas/{id}
GET   /api/core/cuentas/numero/{numeroCuenta}
GET   /api/core/cuentas/numero/{numeroCuenta}/saldo
PATCH /api/core/cuentas/{id}/estado
POST  /api/core/cuentas/{id}/bloqueos
```

### Transacciones

```http
POST /api/core/transacciones/transferencias
GET  /api/core/transacciones/cuenta/{numeroCuenta}
```

### Integración Switch

```http
GET  /api/core/integracion-switch/empresas/{ruc}/validacion
GET  /api/core/integracion-switch/cuentas/{numeroCuenta}/disponibilidad
POST /api/core/integracion-switch/transacciones/transferencia
POST /api/core/integracion-switch/transacciones/liquidacion-servicio
```

### Auditoría

```http
GET /api/core/auditoria
GET /api/core/auditoria/entidad/{entidad}/{idEntidad}
```

---

## 11. Reglas oficiales de programación JPA

Estas reglas son obligatorias para todo el desarrollo del Core.

### 11.1. Declaración de entidades

Toda entidad debe tener:

```java
@Getter
@Setter
@Entity
@Table(name = "NOMBRE_TABLA")
public class NombreEntidad {
}
```

Imports correctos:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
```

No usar `javax.persistence`.

---

### 11.2. Clave primaria

La clave primaria debe llamarse `id` en Java.

Para `INT`:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "ID", nullable = false)
private Integer id;
```

Para `BIGINT`:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "ID", nullable = false)
private Long id;
```

---

### 11.3. Columnas

Toda columna debe mapearse explícitamente.

```java
@Column(name = "NOMBRE", length = 100, nullable = false)
private String nombre;
```

Para `VARCHAR`:

- Siempre colocar `length`.
- Siempre colocar `nullable` según la BD.

Para `DECIMAL`:

```java
@Column(name = "MONTO", precision = 19, scale = 4, nullable = false)
private BigDecimal monto;
```

---

### 11.4. Fechas

Usar tipos modernos:

| Tipo BD | Tipo Java |
|---|---|
| DATE | LocalDate |
| DATETIME | LocalDateTime |
| TIMESTAMP | LocalDateTime |
| TIMESTAMPTZ | OffsetDateTime o LocalDateTime según el alcance |

No usar:

```java
@Temporal
java.util.Date
java.sql.Date
```

---

### 11.5. Enums

Cuando una columna tenga valores controlados por `CHECK`, debe existir un enum Java.

Ejemplo:

```java
@Getter
public enum TipoClienteEnum {
    NATURAL("NATURAL"),
    JURIDICO("JURIDICO");

    private final String value;

    TipoClienteEnum(String value) {
        this.value = value;
    }
}
```

En la entidad:

```java
@Enumerated(EnumType.STRING)
@Column(name = "TIPO_CLIENTE", length = 15, nullable = false)
private TipoClienteEnum tipoCliente;
```

Reglas:

- Usar `EnumType.STRING`.
- No usar `EnumType.ORDINAL`.
- No reutilizar enums incorrectamente.
- Usar sufijo `Enum` al final: `EstadoCuentaEnum`, no `EnumEstadoCuenta`.

---

### 11.6. Lombok

Permitido en entidades:

```java
@Getter
@Setter
```

Permitido en enums con valor interno:

```java
@Getter
public enum EstadoCuentaEnum { ... }
```

No usar en entidades:

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
```

Motivo: estas anotaciones pueden generar identidad incorrecta, imprimir datos sensibles o crear objetos inconsistentes.

---

### 11.7. Constructores

Toda entidad debe tener:

```java
public Cliente() {
}

public Cliente(Integer id) {
    this.id = id;
}
```

En entidades con `Long`:

```java
public TransaccionCuenta() {
}

public TransaccionCuenta(Long id) {
    this.id = id;
}
```

---

### 11.8. equals() y hashCode()

Deben implementarse manualmente por clave primaria.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cliente that = (Cliente) o;
    if (id == null || that.id == null) return false;
    return Objects.equals(id, that.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

No usar Lombok para `equals()` ni `hashCode()`.

---

### 11.9. toString()

Debe implementarse manualmente y con campos seguros.

Ejemplo:

```java
@Override
public String toString() {
    return "Cliente{" +
            "id=" + id +
            ", tipoCliente=" + tipoCliente +
            ", identificacion='" + identificacion + '\'' +
            ", estado=" + estado +
            '}';
}
```

No incluir:

- Passwords.
- Hashes.
- Saldos sensibles.
- Relaciones completas.
- Listas hijas.
- JSON extensos.
- Datos personales innecesarios.

---

### 11.10. @Version

Usar en tablas mutables:

```java
@Version
@Column(name = "VERSION", nullable = false)
private Integer version;
```

Aplica principalmente en:

- `Cliente`
- `Cuenta`
- `Sucursal`
- `CredencialWeb`
- `UsuarioCore`
- `ParametroCore`
- `CuentaInstitucional`
- Catálogos mutables

No aplicar por defecto en:

- Transacciones financieras inmutables.
- Auditoría.
- Historiales que no se actualizan.

---

## 12. Reglas de relaciones JPA

### Regla principal

Mapear relaciones preferentemente de **hijo hacia padre**.

La entidad hija es la que contiene la FK.

Ejemplo:

```text
CUENTA tiene CLIENTE_ID
CUENTA tiene SUCURSAL_ID
CUENTA tiene SUBTIPO_CUENTA_ID
```

Entonces `Cuenta` debe tener:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "CLIENTE_ID", nullable = false)
private Cliente cliente;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "SUCURSAL_ID", nullable = false)
private Sucursal sucursal;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "SUBTIPO_CUENTA_ID", nullable = false)
private SubtipoCuenta subtipoCuenta;
```

No agregar por defecto:

```java
@OneToMany(mappedBy = "cliente")
private List<Cuenta> cuentas;
```

### Motivos

- Reduce acoplamiento.
- Evita ciclos infinitos en JSON.
- Simplifica consultas.
- Mejora mantenimiento.
- Evita cargar colecciones grandes por accidente.

---

## 13. Repositorios

Todo repositorio debe ser una interfaz:

```java
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
```

Para entidades con PK `Long`:

```java
public interface TransaccionCuentaRepository extends JpaRepository<TransaccionCuenta, Long> {
}
```

Reglas:

- No crear métodos finder innecesarios.
- Usar primero los métodos base de `JpaRepository`.
- Agregar finder solo si un caso de uso lo requiere.
- Los resultados para combos o catálogos deben venir filtrados y ordenados.

Ejemplo aceptable:

```java
List<SubtipoCliente> findByTipoClienteAndEstadoOrderByNombreAsc(
        TipoClienteEnum tipoCliente,
        EstadoSubtipoClienteEnum estado
);
```

---

## 14. Servicios y controladores

### Controller

Responsabilidad:

- Recibir HTTP.
- Validar DTOs básicos.
- Delegar al servicio.
- Retornar DTOs.

No debe contener lógica bancaria.

### Service

Responsabilidad:

- Reglas de negocio.
- Validaciones del dominio.
- Transacciones ACID.
- Coordinación entre repositorios.
- Idempotencia.
- Registro de auditoría.

### Repository

Responsabilidad:

- Acceso a datos.
- Consultas específicas.
- Persistencia.

---

## 15. DTOs

No se deben exponer entidades JPA directamente por API.

Usar DTOs:

```text
dto.request
    CrearClienteRequest
    CrearCuentaRequest
    CambiarEstadoCuentaRequest
    TransferenciaSwitchRequest

dto.response
    ClienteResponse
    CuentaResponse
    SaldoCuentaResponse
    TransferenciaSwitchResponse
```

Validar requests con anotaciones:

```java
@NotBlank
@NotNull
@Size
@Email
@DecimalMin
```

---

## 16. Mappers

Los mappers convierten:

```text
Entity → Response DTO
Request DTO → Entity
```

No deben contener reglas bancarias complejas.

Ejemplo:

```java
public ClienteResponse toResponse(Cliente cliente) {
    return new ClienteResponse(
            cliente.getId(),
            cliente.getTipoCliente(),
            cliente.getIdentificacion(),
            cliente.getEstado()
    );
}
```

---

## 17. Integración con Switch

El Switch se comunica con el Core mediante APIs REST internas.

No se permite:

- Acceso directo del Switch a MariaDB.
- Foreign keys físicas entre PostgreSQL y MariaDB.
- Lógica de saldos en el Switch.

Endpoints internos principales:

```http
GET  /api/core/integracion-switch/empresas/{ruc}/validacion
GET  /api/core/integracion-switch/cuentas/{numeroCuenta}/disponibilidad
POST /api/core/integracion-switch/transacciones/transferencia
POST /api/core/integracion-switch/transacciones/liquidacion-servicio
```

Reglas:

- Usar UUIDs para trazabilidad.
- Usar idempotencia para evitar duplicados.
- Usar fecha de negocio.
- El Core siempre valida estado, saldo e idempotencia antes de afectar saldos.

---

## 18. Flujo recomendado de desarrollo

Orden sugerido:

```text
1. branches
2. customers
3. security
4. accounts
5. parameters
6. institutional
7. transactions
8. audit
9. integration.switchapi
10. pruebas de integración
```

Motivo:

- Primero catálogos y entidades base.
- Luego clientes y seguridad.
- Luego cuentas.
- Después transacciones.
- Finalmente integración con Switch.

---

## 19. Checklist antes de aceptar una entidad

Antes de aprobar una entidad JPA, verificar:

```text
[ ] Tiene @Entity.
[ ] Tiene @Table(name = "...").
[ ] Usa jakarta.persistence.
[ ] Tiene @Id.
[ ] Tiene @GeneratedValue si aplica.
[ ] La clave primaria se llama id en Java.
[ ] Todas las columnas tienen @Column.
[ ] Los VARCHAR tienen length.
[ ] Los NOT NULL tienen nullable = false.
[ ] Los DECIMAL usan BigDecimal con precision y scale.
[ ] Las fechas usan LocalDate o LocalDateTime.
[ ] No usa @Temporal.
[ ] Los CHECK de BD están representados como enums.
[ ] Los enums usan @Enumerated(EnumType.STRING).
[ ] No usa EnumType.ORDINAL.
[ ] Tiene constructor vacío.
[ ] Tiene constructor por ID.
[ ] Usa @Getter y @Setter.
[ ] No usa @Data.
[ ] No usa @Builder en entidades.
[ ] No usa @EqualsAndHashCode de Lombok.
[ ] equals() y hashCode() son manuales por PK.
[ ] toString() es manual y seguro.
[ ] No imprime datos sensibles.
[ ] Tiene @Version si la tabla es mutable.
[ ] Relaciones JPA van de hijo a padre.
[ ] No tiene @OneToMany innecesario.
[ ] No expone entidades directamente en controller.
```

---

## 20. Checklist antes de aceptar un endpoint

```text
[ ] Tiene DTO request si recibe body.
[ ] Tiene DTO response.
[ ] Tiene validaciones con Bean Validation.
[ ] Controller delega al Service.
[ ] Service contiene la regla de negocio.
[ ] Repository no contiene lógica de negocio.
[ ] Maneja errores funcionales.
[ ] No expone passwordHash ni datos sensibles.
[ ] Registra auditoría si es operación crítica.
[ ] Usa transacción si afecta saldos o estados.
[ ] Está alineado al contrato REST.
```

---

## 21. Convenciones de nombres

### Clases

```text
Cliente
Cuenta
TransaccionCuenta
CuentaInstitucional
```

### Enums

```text
TipoClienteEnum
EstadoCuentaEnum
CanalOrigenEnum
```

### Repositories

```text
ClienteRepository
CuentaRepository
TransaccionCuentaRepository
```

### Services

```text
ClienteService
CuentaService
MotorTransaccionalService
```

### Controllers

```text
ClienteController
CuentaController
IntegracionSwitchController
```

### DTOs

```text
CrearClienteRequest
ClienteResponse
CambiarEstadoCuentaRequest
SaldoCuentaResponse
```

---

## 22. Buenas prácticas específicas del proyecto

- No duplicar lógica entre Core y Switch.
- El Core siempre decide sobre saldos.
- El Switch solo orquesta lotes.
- Las transacciones financieras deben ser atómicas.
- La idempotencia debe revisarse antes de insertar movimientos.
- Los movimientos financieros no deben actualizarse libremente.
- La auditoría no debe eliminarse.
- Los errores funcionales deben tener códigos claros.
- No se debe usar `double` para dinero.
- No se deben imprimir datos sensibles en logs.
- No se deben exponer entidades JPA en API.
- No se deben crear relaciones bidireccionales si no son necesarias.

---

## 23. Estado actual del proyecto

Estado: **Base backend inicial validada para desarrollo del Core**.

Incluye:

- Estructura por dominios.
- Entidades JPA principales.
- Enums base.
- Repositories.
- Services iniciales.
- Controllers iniciales.
- DTOs y mappers básicos.
- Configuración MariaDB.
- Dependencias alineadas al docente.
- Reglas JPA aplicadas.
- Relaciones hijo → padre.
- Uso controlado de Lombok.

---

## 24. Próximos pasos

1. Abrir proyecto en IntelliJ IDEA.
2. Ejecutar script SQL v4 en MariaDB.
3. Configurar credenciales de `application.properties`.
4. Compilar proyecto.
5. Levantar aplicación.
6. Validar endpoints básicos.
7. Revisar dominio `branches`.
8. Revisar dominio `customers`.
9. Revisar dominio `accounts`.
10. Implementar motor transaccional con mayor profundidad.
11. Implementar integración real con Switch.
12. Agregar pruebas de integración.

---

## 25. Comando rápido de ejecución

```bash
mvn clean spring-boot:run
```

---

## 26. Comando rápido de pruebas

```bash
mvn test
```

---

## 27. Contacto del módulo

Proyecto académico:

```text
Banco BanQuito - Core Bancario
Universidad de las Fuerzas Armadas ESPE
Arquitectura de Software / Aplicaciones Distribuidas
```

---

## 28. Nota final

Este README no reemplaza los contratos REST ni el modelo físico de base de datos. Su función es servir como guía operativa para que cualquier integrante del equipo pueda abrir, revisar, ejecutar y continuar el desarrollo del Core respetando la arquitectura, las reglas JPA y las directrices de programación establecidas.
