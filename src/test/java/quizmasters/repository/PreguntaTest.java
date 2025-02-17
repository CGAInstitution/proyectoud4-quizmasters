package quizmasters.repository;

import jakarta.transaction.Transactional;
import quizmasters.model.Pregunta;
import quizmasters.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class PreguntaTest {

    @Autowired
    PreguntaRepository preguntaRepository;

    @Test
    public void crearPregunta() {
        //WHEN
        //una nueva pregunta es creada en memoria

        Pregunta pregunta = new Pregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //THEN
        //enunciado y respuesta son correctos

        assertThat(pregunta.getEnunciado()).isEqualTo("¿Qué año estalló la Revolución Francesa?");
        assertThat(pregunta.getRespuestaCorrecta()).isEqualTo("1789");
    }

    @Test
    @Transactional
    public void crearPreguntaBaseDatos(){
        //GIVEN
        //pregunta sin id
        Pregunta pregunta = new Pregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);

        //WHEN
        //se guarda en la base de datos

        preguntaRepository.save(pregunta);

        //THEN
        assertThat(pregunta.getId()).isNotNull();

        Pregunta preguntaBD = preguntaRepository.findById(pregunta.getId()).orElse(null);
        assertThat(preguntaBD.getEnunciado()).isEqualTo("¿Qué año estalló la Revolución Francesa?");
        assertThat(preguntaBD.getRespuestaCorrecta()).isEqualTo("1789");
        assertThat(preguntaBD.getCategoria()).isEqualTo(Categoria.HISTORIA);
    }

    @Test
    @Transactional
    public void buscarPreguntaEnBaseDatosPorId(){
        //GIVEN
        //pregunta en la base de datos
        Pregunta pregunta = new Pregunta("¿Cúal es la capital de Bután?","Timbu",Categoria.GEOGRAFIA,10f);
        preguntaRepository.save(pregunta);

        //WHEN
        //se recupera en la base de datos por su id

        Pregunta preguntaBD = preguntaRepository.findById(pregunta.getId()).orElse(null);

        //THEN
        //comprobamos sus datos

        assertThat(preguntaBD.getEnunciado()).isEqualTo("¿Cúal es la capital de Bután?");
        assertThat(preguntaBD.getRespuestaCorrecta()).isEqualTo("Timbu");
        assertThat(preguntaBD.getCategoria()).isEqualTo(Categoria.GEOGRAFIA);
        assertThat(preguntaBD.getPuntuacion()).isEqualTo(10f);
    }

    @Test
    @Transactional
    public void buscarPreguntasPorCategoria(){
        //GIVEN
        //varias preguntas en la Base de datos
        Pregunta preguntaGeografia_1 = new Pregunta("¿Cúal es la capital de Bután?","Timbu",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaGeografia_2 = new Pregunta("¿Cúal es la capital de Francia?","Paris",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaGeografia_3 = new Pregunta("¿Cúal es la capital de Rusia?","Moscú",Categoria.GEOGRAFIA,10f);

        Pregunta preguntaHistoria_1 = new Pregunta("¿Quién fue el primer emperador romano?","Augusto",Categoria.HISTORIA,10f);

        Pregunta preguntaHistoria_2 = new Pregunta("¿En que año invadieron los musulmanes la Península Ibérica?","711",Categoria.HISTORIA,10f);

        preguntaRepository.save(preguntaGeografia_1);
        preguntaRepository.save(preguntaGeografia_2);
        preguntaRepository.save(preguntaGeografia_3);
        preguntaRepository.save(preguntaHistoria_1);
        preguntaRepository.save(preguntaHistoria_2);

        //WHEN
        //buscamos las preguntas por categoría

        List<Pregunta> preguntasHistoria = preguntaRepository.findByCategoria(Categoria.HISTORIA);
        List<Pregunta> preguntasGeografia = preguntaRepository.findByCategoria(Categoria.GEOGRAFIA);

        //THEN
        //comprobamos los datos

        assertThat(preguntasHistoria.size()).isEqualTo(2);
        assertThat(preguntasHistoria.get(0).getCategoria()).isEqualTo(Categoria.HISTORIA);
        assertThat(preguntasHistoria.get(0).getRespuestaCorrecta()).isEqualTo("Augusto");
        assertThat(preguntasGeografia.size()).isEqualTo(3);
        assertThat(preguntasGeografia.get(0).getRespuestaCorrecta()).isEqualTo("Timbu");
    }

    @Test
    @Transactional
    public void buscarPreguntaPorEnunciado() {
        //GIVEN
        //una pregunta en la Base de datos
        Pregunta preguntaGeografia_1 = new Pregunta("¿Cúal es la capital de Bután?","Timbu",Categoria.GEOGRAFIA,10f);
        preguntaRepository.save(preguntaGeografia_1);

        //WHEN
        //buscamos la pregunta por enunciado
        Pregunta preguntaBD = preguntaRepository.findByEnunciado("¿Cúal es la capital de Bután?").orElse(null);

        //THEN
        //comprobamos que los datos son correctos
        assertThat(preguntaBD).isNotNull();
        assertThat(preguntaBD.getRespuestaCorrecta()).isEqualTo("Timbu");

    }
}
