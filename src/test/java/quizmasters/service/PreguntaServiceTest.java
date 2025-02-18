package quizmasters.service;

import quizmasters.model.Categoria;
import quizmasters.model.Pregunta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class PreguntaServiceTest {

    @Autowired
    private PreguntaService preguntaService;

    @Test
    public void test_crearPregunta(){
        //WHEN
        //añadimos una pregunta en la BD
        Pregunta pregunta = preguntaService.crearPregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //THEN
        //comprobamos que se genera un id automaticamente
        assertThat(pregunta.getId()).isNotNull();
    }

    @Test
    public void test_encontrarPregunta(){
        //GIVEN
        //una pregunta almacenada en base de datos
        Pregunta pregunta = preguntaService.crearPregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //WHEN
        //buscamos la pregunta por id
        Pregunta preguntaBD = preguntaService.findById(pregunta.getId());

        //THEN
        //comprobamos que los campos son correctos
        assertThat(preguntaBD.getRespuestaCorrecta()).isEqualTo("1789");
        assertThat(preguntaBD.getPuntuacion()).isEqualTo(10f);

    }

    @Test
    public void test_getPreguntasByCategoria(){
        //GIVEN
        //varias preguntas en la Base de datos
        Pregunta preguntaGeografia_1 = preguntaService.crearPregunta("¿Cúal es la capital de Bután?","Timbu",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaGeografia_2 = preguntaService.crearPregunta("¿Cúal es la capital de Francia?","Paris",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaGeografia_3 = preguntaService.crearPregunta("¿Cúal es la capital de Rusia?","Moscú",Categoria.GEOGRAFIA,10f);

        //WHEN
        //buscamos las preguntas por categoría

        List<Pregunta> preguntasGeografia = preguntaService.findByCategoria(Categoria.GEOGRAFIA);
        List<Pregunta> preguntasHistoria = preguntaService.findByCategoria(Categoria.HISTORIA);

        //THEN
        //comprobamos los datos
        assertThat(preguntasGeografia.size()).isEqualTo(3);
        assertThat(preguntasGeografia.get(0).getRespuestaCorrecta()).isEqualTo("Timbu");
        assertThat(preguntasHistoria).isEmpty();

    }

    @Test
    public void test_findAll(){
        //GIVEN
        //varias preguntas en la Base de datos
        Pregunta preguntaGeografia_1 = preguntaService.crearPregunta("¿Cúal es la capital de Bután?","Timbu",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaGeografia_2 = preguntaService.crearPregunta("¿Cúal es la capital de Francia?","Paris",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaGeografia_3 = preguntaService.crearPregunta("¿Cúal es la capital de Rusia?","Moscú",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaHistoria_1 = preguntaService.crearPregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //WHEN
        //buscamos las preguntas
        List<Pregunta> preguntas = preguntaService.findAll();
        //THEN
        //comprobamos los datos
        assertThat(preguntas.size()).isEqualTo(4);
        assertThat(preguntas.get(0).getRespuestaCorrecta()).isEqualTo("Timbu");
        assertThat(preguntas.stream().filter(pregunta ->
            pregunta.getCategoria() == Categoria.GEOGRAFIA).toList().size()).isEqualTo(3);
    }

    @Test
    public void test_updatePregunta(){
        //GIVEN
        //una pregunta almacenada en base de datos
        Pregunta pregunta = preguntaService.crearPregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //WHEN
        //actualizamos los datos
        Pregunta preguntaActualizada = preguntaService.updatePregunta(pregunta,"¿Qué día se considera que estalló la Revolución Francesa?","14 de julio de 1789",Categoria.HISTORIA,10f);

        //THEN
        //comprobamos que los campos son correctos
        Pregunta preguntaBD = preguntaService.findById(pregunta.getId());
        assertThat(preguntaBD.getRespuestaCorrecta()).isEqualTo("14 de julio de 1789");
        assertThat(preguntaBD.getPuntuacion()).isEqualTo(10f);
    }

    @Test
    public void test_deletePregunta(){
        //GIVEN
        //una pregunta almacenada en base de datos
        Pregunta pregunta = preguntaService.crearPregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //WHEN
        //eliminamos la pregunta
        preguntaService.deletePregunta(pregunta);

        //THEN
        //comprobamos que no existe en la BD
        assertThat(preguntaService.findById(pregunta.getId())).isNull();
    }
}
