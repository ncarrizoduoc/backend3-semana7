# Proyecto Spring Batch - Banco XYZ
El objetivo de este proyecto es definir dos microservicios, comunicados de manera asíncrona a través de eventos.
El microservicio **msbanco** procesa transacciones bancarias y genera estados de cuenta.
El microservicio **ms_correo** recibe notificaciones de transacciones completadas e imprime la información de cada transacción en consola.
Se utiliza **Config Server** para configurar los microservicios de manera centralizada a través de archivos de configuración.
Se utiliza **Eureka Server** para registrar el microservicio de banco, para facilitar su comunicación con otras aplicaciones más adelante. 

## Requisitos previos
- **Java 21**: Asegúrate de tener instalado JDK 21.
- **Maven 3.9.x** o superior: Para compilar y ejecutar el proyecto.

## Tecnologías utilizadas
- **Java 21**
- **Spring Boot**
- **Maven**

## Estructura del proyecto
El proyecto incluye cuatro sub-proyectos, cada uno corresponde a una aplicación diferente.

# Como utilizar
Para poder utilizar este proyecto, debes ejecutar las aplicaciones contenidas en `config-server` y `service-discovery` en primer lugar. Con las aplicaciones anteriores en ejecución, debes ejecutar las aplicaciones contenidas en las carpetas `msBanco` y `ms_correo`. Las aplicaciones deben ejecutarse en ese orden, puestos que los microservicios requieren la configuración definida en `config-server` para funcionar correctamente.