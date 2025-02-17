## Índice
- [Introducción](#introducción)
  - [Supuesto](#supuesto-a-resolver)
    - [Usuarios](#usuarios)
    - [Preguntas](#preguntas)
    - [Modos de Juego](#modos-de-juego)
    - [Partidas](#partidas)
    - [Categorías](#categoría)
  - [Modelo de la Base de Datos](#Modelo-de-la-Base-de-Datos)
- [Manual técnico para desarrolladores](#manual-técnico-para-desarrolladores)
    - [Requisitos Previos](#requisitos-previos)
    - [Estructura del proyecto](#estructura)
    - [Ejecución del proyecto](#ejecución-del-proyecto)
    - [Testing](#testing)
    - [Manejo de errores](#manejo-de-errores)
- [Manual de usuario](#manual-de-usuario)
- [Metodología de Desarrollo en Equipo](#metodología-de-desarrollo-en-equipo)
  - [Git](#uso-de-git)
  - [Reparto de tareas](#reparto-de-tareas)
- [Extras Realizados](#extras)
- [Mejoras](#mejoras)
- [Conclusiones](#conclusiones)
- [Autores](#autores)

## Introducción

[Volver al índice](#índice)

Trabajo realizado por Yelko Veiga Quintas, Evan Silva González y David Búa Teijeiro como proyecto correspondiente a la unidad 4: Creación de Aplicaciones Web, de la asignatura de Acceso a Datos de 2ºDAM.

### Supuesto a resolver

___
Deseamos diseñar un juego tipo quiz, dónde varios usuarios se conecten a diferentes partidas, respondiendo a una serie
de peguntas, donde la velocidad va marcada por un gameMaster.

___

#### *Usuarios*

Llevamos un registro de nuestros usuarios con su **correo electrónico** como atributo obligatorio, junto con su **nombre
**, **contraseña** y **fecha de nacimiento**. Además, podemos almacenar si el usuario tiene privilegios de administrador
mediante el atributo `isAdmin`.

Además, se proporciona un constructor para crear instancias de usuarios con el correo electrónico obligatorio, mientras
que el resto de los atributos pueden ser opcionales. También se incluye un método que permite añadir tareas al usuario,
manteniendo la relación bidireccional entre usuario y tarea.

Los usuarios se identifican principalmente por su **correo electrónico** o, en su caso, por su **ID** si se encuentran
almacenados en la base de datos. Se implementa un sistema de comparación y hash basado en estos atributos para gestionar
la identidad de los usuarios de forma eficiente.

___

#### *Preguntas*

Llevamos un registro de las **preguntas** en el sistema, cada una con su **enunciado**, **respuesta correcta**, *
*categoría** y **puntuación** asociada. La **categoría** es un atributo que clasifica la pregunta dentro de un conjunto
predefinido. La **puntuación** es un valor de tipo flotante que asigna un valor numérico a cada pregunta.

Cada pregunta tiene un **ID** único, generado automáticamente, y un **enunciado** que debe ser único en la base de
datos. Además, se asegura que los atributos **respuesta correcta**, **categoría** y **puntuación** no sean nulos, ya que
son fundamentales para el funcionamiento de la pregunta.

Se implementan los métodos `equals()` y `hashCode()` para asegurar una correcta comparación entre preguntas, basándose
en el **ID** y el **enunciado**. Además, el método `toString()` permite una representación legible de la pregunta, que
incluye el **ID**, **enunciado**, **respuesta correcta**, **categoría** y **puntuación**.

___

#### *Modos de Juego*

Llevamos un registro de los **modos de juego**, cada uno con un **nombre**, **número de preguntas** que se deben
responder en el juego y una lista de **categorías** que estarán presentes en ese modo de juego.

Cada **modo de juego** tiene un **ID** único, generado automáticamente. El **nombre** del modo es un atributo de texto,
y el **número de preguntas** especifica cuántas preguntas estarán disponibles en ese modo de juego.

Las **categorías** son representadas por un conjunto de enumeraciones (`Categoria`), que se almacenan en una lista. Esta
lista se guarda utilizando una relación de colección a través de la anotación `@ElementCollection`, lo que permite
almacenar múltiples categorías para cada modo de juego.

Se proporciona un constructor para crear un **modo de juego** con los atributos **nombre**, **número de preguntas** y *
*categorías**. Además, se incluyen métodos de acceso (getters y setters) para todos los atributos.

___

#### *Partidas*

El sistema lleva un registro de las **partidas** en curso o terminadas. Cada partida tiene un **ID** único, un **hora y
fecha** de inicio (almacenado como `LocalDateTime`), y un **estado de unirse** que indica si la partida está abierta
para nuevos jugadores.

Una **partida** puede tener un **modo de juego** asociado, definido a través de una relación de tipo `ManyToOne`, que
indica qué tipo de juego se está jugando.

Las **preguntas** que forman parte de la partida están asociadas mediante una relación de **muchos a muchos** (
`ManyToMany`) con la entidad `Pregunta`, lo que permite tener múltiples preguntas para cada partida. Además, cada
partida puede tener múltiples **jugadores** (usuarios), también a través de una relación de **muchos a muchos** con la
entidad `Usuario`.

El sistema permite identificar al **ganador** de la partida, que se asocia con un usuario a través de una relación
`ManyToOne`.

Además, cada partida tiene dos estados booleanos:

- **start**: Indica si la partida ha comenzado.
- **next**: Indica si la partida está lista para la siguiente pregunta.

Los métodos permiten añadir o eliminar jugadores y preguntas a la partida, así como acceder a las listas de jugadores y
preguntas.

Se proporciona un constructor para crear una partida con todos los atributos necesarios y un constructor por defecto sin
parámetros.

___

#### *Categoría*

La **Categoría** es un enumerado (`enum`) que define los posibles tipos de categorías en las que pueden clasificarse las
preguntas dentro del sistema de juego. Las categorías disponibles son:

- **GEOGRAFÍA**
- **HISTORIA**
- **ARTE Y LITERATURA**
- **DEPORTES Y OCIO**
- **CIENCIA Y NATURALEZA**
- **ENTRETENIMIENTO**

Cada pregunta dentro de la base de datos puede estar asociada a una de estas categorías, lo que permite organizar y
filtrar las preguntas según su temática.
___

### Modelo de la Base de Datos

A continuación se muestra el esquema Crow's Foot de la base de datos diseñada para implementar el supuesto comentado en el apartado anterior

![](img/modelo_sin_tareas.png)

## Manual técnico para desarrolladores

[Volver al índice](#índice)

### Requisitos previos

- **Java SE 17 o superior**: El proyecto está desarrollado usando Java 17, por lo que necesitarás tener una versión
  igual o superior
  instalada. ([descargar](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html))
- **Maven**: La gestión de dependencias se hace con Maven, por lo que deberás tener Maven
  instalado.([descargar](https://maven.apache.org/download.cgi))
- **IDE recomendado**: Se recomienda el uso de IntelliJ IDEA para un desarrollo más sencillo, pero se puede usar
  cualquier otro IDE compatible con Java. ([descargar](https://www.jetbrains.com/idea/download/?section=windows))
- **SpringBoot** : Se ha usado SpringBoot para la creación de la aplicación
  web. ([documentacion](https://spring.io/projects/spring-boot))

### Estructura

[Volver al índice](#índice)

El proyecto está planteado siguiendo el
patrón [Modelo-Vista-Controlador.](https://es.wikipedia.org/wiki/Modelo%E2%80%93vista%E2%80%93controlador)

#### Modelo

___
El modelo contiene los datos del programa y define cómo estos deben ser manipulados, es decir, contiene la lógica que se
necesita para gestionar el estado y las reglas del negocio.
Interactúa respondiendo a las solicitudes del controlador para acceder o actualizar los datos. Notifica indirectamente a
la vista cuando los datos han cambiado para que se actualice la presentación.

Nuestra aplicación cuenta con los siguientes paquetes:

- **<u>java.quizmasters.model</u>**: Contiene las clases de acceso a base de datos. En este, se encuentran los
  siguientes paquetes: `Categoria`, `ModoDeJuego`, `Partida`, `Pregunta`, `Usuario`y `Tarea`, este ultimo como una clase
  vestigial del proyecto base ofrecido por el tutor.



#### Controlador

___
El controlador recibe las entradas del usuario desde la vista y las traduce en acciones que el modelo debe ejecutar. Se
encarga de interpretar las acciones del usuario, manejar los eventos, y de actualizar tanto el modelo como la vista.

- **<u>java.quizmasters.controller</u>**: Coordina la interacción entre los diferentes componentes. Para cada clase del
  modelo de la aplicación existe un controlador que la maneja y estructura los datos del modelo: `ModoDeJuego` es
  controlado por `ModoDeJuegoController`, `citas.fxml` por `CitasController.java` etc. En total tenemos 8 controladores
  en nuestra aplicación, pero solo 7 que usemos como tal, dado que `TareaController` es una clase vestigial como su
  correspondiente en el modelo.

#### Vista

___
Se encarga de la visualización de los datos del modelo de manera que el usuario los entienda. No contiene lógica de
negocio, solo muestra lo que el modelo le proporciona.. La vista recibe entradas del usuario (clics, teclas, etc.) y las
envía al controlador, en Springboot, a veces se tramitan en la propia página.

- **<u>resources.static.css</u>**: en el directorio css se almacenan los recursos necesarios para construir la interfaz
  de usuario enfocado a estilos.

- **<u>resources.templates</u>**: Es el principal paqiete del paquete de vista, con los archivos `*.html`, que es
  utilizada para almacenar la estructura de las paginas que se visualizan a lo largo de la aplicación.

### Código Destacado

A continuación destacamos los puntos que consideramos más relevantes de nuestro código

#### Sse Controller y SseEmitters
Para que la aplicación cumpla con nuestros propósitos, es necesario que las vistas de unos usuarios cambien de acuerdo a eventos que generan otros (por ejemplo, el adminstrador podrá empezar una partida, y automáticamente todos los jugadores verán en sus pantallas la primera pregunta). Para este tipo de situaciones, Spring Boot cuenta con WebSockets, una herramienta muy potente para la comunicación. No obstante, por tiempo limitado y por ser las comunicaciones que necesitamos más rudimentarias, optamos por implementar un método más sencillo, el uso de SseEmitters gestionado por un SseController. De forma simplificada, se podría considerar que esta clase ayuda a implementar listeners para dar respuesto a los eventos ya mencionados.

![](img/ssecontroller.png)

La Clase SseController, que es un Bean de tipo RestController, dispondrá de una serie de endpoints, cada uno asociado a un evento en concreto que se quiere escuchar. En la imagen se muestran los que hemos incluído en nuestro caso. Algunos ejemplos son el cambio de las partidas a las que se puede unir el jugador, o la señal de que una partida en concreto ha empezado. Los métodos correspondientes se llaman desde los html que contienen un fragmento de javascript con la definición conexión a uno o varios de estos endpoints. En la siguiente imagen se muestra como ejemplo el código de la sala de espera antes de iniciar una partida, que escucha si la partida que se está esperando empieza o se cancela, redireccionando la vista al endpoint correspondiente en cada caso.

![](img/listenEmitter.png)

De esta forma, cada vez que se cargue un html que contenga un código similar a este, se ejecutará el método que crea un emitter con destino al cliente que hizo la solicitud. Dado que tenemos emitters para distintos eventos, los clasificamos en listas, agrupadas en un map, cuya clave refleja sus respectivas funciones. Cuando ocurra el evento que queremos que desencadene la acción, simplemente se tendrá que llamar al método completeSignal, que enviará los emitters del tipo indicado por parámetro. Puesto que solo queremos indicar la ocurrencia de un evento, no es relevante el mensaje que estos incluyan.

![](img/addemitter.png)

El mayor problema que surge de esta implementación es que los emitters son propensos a fallar, ya que el usuario que estaba escuchando puede realizar cualquier acción que cambia la vista, perdiéndose la comunicación. Cuando esto ocurre lanzan una excecpión que impide el correcto funcionamiento de la aplicación. Es por ello que hay que gestionar adecuadamente estas situaciones. En la imagen anterior se muestra como definimos que se borre un emitter de la lista correspondiente si se ha completado (ya ha cumplido su misión y el cliente que lo creó ya no está escuchando ese evento) o ha fallado. Además, en la siguiente imagen se muestra también como capturamos errores tanto al enviarlos como al setearlos como completados, eliminado de la lista cualquier emitter que haya perdido la conexión.

![](img/sendUpdate.png)

### Testing

[Volver al índice](#índice)

Este proyecto implementa un enfoque de Desarrollo Guiado por Pruebas (TDD) para la capa de acceso a datos (el modelo del
patrón MVC). TDD nos permitió diseñar la funcionalidad necesaria enfocándonos en cumplir primero los requisitos a través
de casos de prueba. Este enfoque garantiza que cada funcionalidad desarrollada no solo cumpla con las especificaciones
iniciales, sino que también sea robusta y verificable.

Al principio se utilizó una base de datos específica para realizar los test, **`quiz_test`**. Cada prueba se ejecuta en
una base de datos aislada. Los tests se centran sobre todo en comprobar que las operaciones se realizan con éxito.
Debido a problemas con el método de inicialización drop and create de hibernate, los test dependen unos de otros. Solo
es posible ejecutarlos en un orden específico, y si se realizan cambios en la base de datos de prueba no se mantienen
aislados, con lo cual se pueden producir errores en la automatización. Luego las pruebas se llevaron a cabo en la base
de datos de AWS.

En esta versión (1.0) tan solo está testeada la parte de acceso a datos del modelo (preguntas y modo de juego). Los
casos test referidos al modelo se encuentran en las clases dentro del paquete `src.test.quizmasters.`, dividiendo los
test del mismo modo que se hizo con el modelo, usando directorios con los nombres de la parte que se desea testear,
siendo `controller`, `repository` y `service`.


### Configuración de Maven

[Volver al índice](#índice)

El archivo `pom.xml` incluye las siguientes dependencias importantes:

```xml

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.1</version>
</parent>


<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```


### Ejecución del proyecto

[Volver al índice](#índice)

#### Desde el IDE (IntelliJ IDEA):

1. Importar el proyecto como un proyecto Maven.
2. Asegurarse de que las dependencias estén instaladas en el `pom.xml`.
3. Configurar los módulos de JavaFX como se describió anteriormente.
4. Ejecutar la clase `Main` para iniciar la aplicación.

#### Desde la terminal:

Una vez que el JAR esté generado, se ejecuta el siguiente comando **desde el directorio donde se encuentre el .jar
generado**:

``` bash
java --module-path="/ruta/al/javafx/lib" --add-modules="javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.media" -jar nombre-del-archivo.jar
```

En la siguiente captura mostramos la ejecución del JAR desde el terminal.

### Despliegue (Yelko time)

### Log in

[Volver al índice](#índice)

El log in de la aplicación se realiza mediante un correo electrónico y una contraseña. La contraseña se almacena en la base de datos, aunque exta clase ha sido proporcionada por la tutora, nosotros usamos datos para determinar si el usuario es normal o administrador.

#### **Características**

- **Registro de usuarios**: Permite a los usuarios registrarse en la aplicación proporcionando un correo electrónico y
  una contraseña. La contraseña se almacena en la base de datos como un hash seguro.

- **Inicio de sesión de usuarios**: Permite a los usuarios iniciar sesión en la aplicación proporcionando su correo.

- **Inicio de sesion de administrador**: Permite a los usuarios con privilegios de administrador iniciar sesión en la
  aplicación y acceder a funciones adicionales.
## Manual de usuario

[Volver al índice](#índice)

Cambiar el enlace al nuevo vídeo.

[Vídeo tutorial en Google drive](https://drive.google.com/drive/folders/1WzihyfGQkL56N75eXA3xjHCwqtfAH2ga)

## Metodología de Desarrollo en Equipo

[Volver al índice](#índice)

### Uso de Git

Este proyecto sigue una metodología de desarrollo incremental basado en ramas y pull request, lo que facilita la gestión
de versiones y la colaboración entre desarrolladores. Cada grupo de tareas que se requieren para completar una seccion
de la aplicacion fue englobada en una **issue**. Tratando de seguir la metodología de git-flow, las ramas principales del proyecto son `main` y `develop`, que contienen, respectivamente, versiones listas para producción e implementaciones correctas de las funcionalidades, mientras que el
desarrollo se llevó a cabo en paralelo en las ramas que tienen un nombre acorde a la tarea.

El flujo de trabajo del desarrollo es el siguiente:

1. **Diseñar el issue**: Cuando se desea implementar una nueva funcionalidad, se trabaja en la rama relacionada con la
   propia funcionalidad que hemos creado a traves delissue.

2. **Testeo**: Una vez que se ha completado la funcionalidad, se realizan pruebas para asegurar que todo funciona
   correctamente y cumple con los requisitos establecidos.


3. **Pull request**: Después de las pruebas exitosas, se realiza un "pull request" de la rama de funcionalidad a
   `develop`. Este paso es crucial para comprobar la integración de la nueva funcionalidad con el resto del código del
   proyecto, los otros colaboradores revisan y aceptan el codigo proporcionado.


4. **Merge a Main**: Finalmente, cuando la versión en `develop` ha sido probada y se confirma que es estable, se realiza
   un "merge" a la rama `develop`, cuando una version del código llega a un estado útil, el "merge" se lleva a `main`.

Este enfoque permite una colaboración fluida entre los dos desarrolladores, asegurando que el código sea de alta calidad
y esté bien integrado antes de ser lanzado.

### Reparto de tareas

TODO

## Extras

[Volver al índice](#índice)

En la realización de este trabajo se incluyeron varios extras a mayores de los requisitos mínimos del proyecto. Los más
destacables son los siquientes:

- Despliegue con Docker: la aplicación estará publicada en una instancia dockerizada EC2 de AWS o Azure, conectándose a una base de datos MySQL publicada en Azure o AWS-RDS. (10%)
- Dashboard para el administrador de usuarios que acceden a nuestra aplicación (podrá crear, modificar, bloquear y borrar usuarios) (3%)
- Utilización de SseController y SseEmitter para enviar señales entre clientes: aunque no forma parte de los extras propuestos, la dedicación temporal necesaria para desarrollar esta parte es, cuanto menos, digna de mención.


## Mejoras

[Volver al índice](#índice)

Como en todo proyecto, existen numerosas características que por tiempo o recursos no se han podido implementar o que no
se han podido perfeccionar. En nuestro caso hemos considerado las siguientes posibilidades de mejora, aunque siempre
abiertos a la posibilidad de otras que no hemos detectado:

- Implementar distintos tipos de preguntas (de respuesta múltiple, verdadero/falso)
- Mejorar el sistema de corrección de pregunta, de modo que la respuesta del jugador sea válida si hay un % de acierto o si se acerca lo suficiente a la respuesta correcta (para evitar que las faltas de ortografía o errores de teclado cuenten como respuestas fallidas)
- Filtrar las partidas que todavía no están finaliadas de las que sí.
- Permitir la creación de categorías
- Permitir partida de un solo jugador
- Añadir más configuración customizable a los modos de juego (que el paso de preguntas sea por tiempo o por administrador, que se preconfiguren las pausas, que se muestren las respuestas correctas...)
- Refactorizar la comunicación entre usuarios en el desarollo del quiz para implementar una metodología más eficiente que los emitters con vista a tener mayor control y escalabilidad de la aplicación.

## Conclusiones

[Volver al índice](#índice)

Durante este proyecto hemos sido capaces de desarrollar un aplicación funcional que cumpliese los requisitos que nos habíamos propuesto: poder realizar partidas multijugador de un juego tipo trivial de forma sincronizada, con alta customización de las mismas. Esto supone una gran satisfacción para nosotros porque es una base sobre la que podemos construir un producto más ambicioso (con más funcionalidades, más eficiente, etc.) con el que llevamos tiempo soñando. Evidentemente, debido al tiempo limitado para realizar este proyecto, la aplicación aquí presentada constituye solamente un prototipo, pero resulta muy gratificante ver que con las herramientas que estamos adquiriendo podemos convertir estas ideas en realidades.

Dicho esto, cabe mencionar que Spring Boot era una novedad para nosotros, por lo que el desarrollo fue, ante todo, una puesta en práctica de los conceptos básicos que hemos aprendido. Por ello, somos conscientes de que todavía tenemos mucho que aprender para dominar su uso y que hay muchas mejoras que podemos implementar, no solo a nivel de funcionalidades, sino también a nivel de estructura y eficiencia. Además, las particularidades de nuestra aplicación supusieron tener que investigar de forma autónoma cómo llevar a cabo una comunicación entre los clientes, funcionalidad cuya implementación, al ser novedosa para nosotros, puede ser también optimizada.

No obstante, consideramos que hemos cumplido todos los requisitos exigidos para esta actividad de forma satisfactoria, reflejándose en el código y en este documento que hemos adquirido no solo los conocimiento correspondientes al nivel en el que nos hallamos, sino que las investigaciones a mayores nos han hecho crecer todavía más como programadores. Este trabajo supuso una dedicación temporal en horas superior a 20 horas por persona y, por lo todo lo ya expuesto, estimamos que es digno de un sobresaliente.


## Autores

[Volver al índice](#índice)

Yelko Veiga Quintas [@yelkov](https://github.com/yelkov)

Evan Silva González [@EvanSilva](https://github.com/EvanSilva)

David Búa Teijeiro [@BuaTeijeiro](https://github.com/BuaTeijeiro)
