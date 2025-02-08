package madstodolist.service;

import madstodolist.dto.PartidaForm;
import madstodolist.model.Categoria;
import madstodolist.model.ModoDeJuego;
import madstodolist.model.Partida;
import madstodolist.model.Pregunta;
import madstodolist.service.exception.NotEnoughQuestionsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class PartidaServiceGenerarPreguntasTest {
    @Autowired
    PartidaService partidaService;
    @Autowired
    ModoDeJuegoService modoDeJuegoService;
    @Autowired
    PreguntaService preguntaService;

    @Test
    @Transactional
    public void RecuperarPreguntasMatching_1Category_AllMatching(){
        Pregunta pregunta1 = preguntaService.crearPregunta("Capital de España","Madrid", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta2 = preguntaService.crearPregunta("Capital de Francia","París", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta3 = preguntaService.crearPregunta("Capital de Estonia","Tallín", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta4 = preguntaService.crearPregunta("Capital de Portugal","Lisboa", Categoria.GEOGRAFIA, 15f);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        List<Pregunta> preguntas = partidaService.getMatchingPreguntas(partida);
        assertThat(preguntas.size()).isEqualTo(4);
        assertThat(preguntas.contains(pregunta1)).isTrue();
        assertThat(preguntas.contains(pregunta2)).isTrue();
        assertThat(preguntas.contains(pregunta3)).isTrue();
        assertThat(preguntas.contains(pregunta4)).isTrue();
    }

    @Test
    @Transactional
    public void RecuperarPreguntasMatching_1Category_NoneMatching(){
        Pregunta pregunta1 = preguntaService.crearPregunta("Capital de España","Madrid", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta2 = preguntaService.crearPregunta("Capital de Francia","París", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta3 = preguntaService.crearPregunta("Capital de Estonia","Tallín", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta4 = preguntaService.crearPregunta("Capital de Portugal","Lisboa", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        List<Pregunta> preguntas = partidaService.getMatchingPreguntas(partida);
        assertThat(preguntas.size()).isEqualTo(0);
        assertThat(preguntas.contains(pregunta1)).isFalse();
        assertThat(preguntas.contains(pregunta2)).isFalse();
        assertThat(preguntas.contains(pregunta3)).isFalse();
        assertThat(preguntas.contains(pregunta4)).isFalse();
    }

    @Test
    @Transactional
    public void RecuperarPreguntasMatching_1Category_SomeMatching(){
        Pregunta pregunta1 = preguntaService.crearPregunta("Capital de España","Madrid", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta2 = preguntaService.crearPregunta("Capital de Francia","París", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta3 = preguntaService.crearPregunta("Capital de Estonia","Tallín", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta4 = preguntaService.crearPregunta("Capital de Portugal","Lisboa", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        List<Pregunta> preguntas = partidaService.getMatchingPreguntas(partida);
        assertThat(preguntas.size()).isEqualTo(2);
        assertThat(preguntas.contains(pregunta1)).isTrue();
        assertThat(preguntas.contains(pregunta2)).isFalse();
        assertThat(preguntas.contains(pregunta3)).isTrue();
        assertThat(preguntas.contains(pregunta4)).isFalse();
    }



    @Test
    @Transactional
    public void GenerarUnaPreguntaTest() throws NotEnoughQuestionsException{
        preguntaService.crearPregunta("Capital de España","Madrid", Categoria.GEOGRAFIA, 15f);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        partidaService.generarPreguntasPartida(partida);
        List<Pregunta> preguntas = partida.getPreguntas();
        assertThat(preguntas.size()).isEqualTo(1);
        assertThat(preguntas.get(0).getCategoria()).isEqualTo(Categoria.GEOGRAFIA);
        assertThat(preguntas.get(0).getEnunciado()).isEqualTo("Capital de España");
        assertThat(preguntas.get(0).getPuntuacion()).isEqualTo(15f);
    }

    @Test
    @Transactional
    public void GenerarUnaPreguntaTest_SinPreguntasSuficientes(){
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        assertThatThrownBy(() -> partidaService.generarPreguntasPartida(partida)).isInstanceOf(NotEnoughQuestionsException.class).hasMessageContaining("recuperado 0").hasMessageContaining("de 1");
        List<Pregunta> preguntas = partida.getPreguntas();
        assertThat(preguntas.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void GenerarUnaPreguntaTest_ConPreguntasDeMas() throws NotEnoughQuestionsException{
        Pregunta pregunta1 = preguntaService.crearPregunta("Capital de España","Madrid", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta2 = preguntaService.crearPregunta("Capital de Francia","París", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta3 = preguntaService.crearPregunta("Capital de Estonia","Tallín", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta4 = preguntaService.crearPregunta("Capital de Portugal","Lisboa", Categoria.GEOGRAFIA, 15f);
        List<Pregunta> allPreguntas = List.of(pregunta1,pregunta2, pregunta3, pregunta4);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        partidaService.generarPreguntasPartida(partida);
        List<Pregunta> preguntas = partida.getPreguntas();
        assertThat(preguntas.size()).isEqualTo(modoDeJuego.getNumeroDePreguntas());
        assertThat(preguntas.get(0).getCategoria()).isEqualTo(Categoria.GEOGRAFIA);
        assertThat(allPreguntas).contains(preguntas.get(0));
    }

    @Test
    @Transactional
    public void GenerarUnaPreguntaTest_ConPreguntasDiferentesCategorias_1Matching() throws NotEnoughQuestionsException{
        Pregunta pregunta1 = preguntaService.crearPregunta("Capital de España","Madrid", Categoria.GEOGRAFIA, 15f);
        Pregunta pregunta2 = preguntaService.crearPregunta("Capital de Francia","París", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta3 = preguntaService.crearPregunta("Capital de Estonia","Tallín", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta4 = preguntaService.crearPregunta("Capital de Portugal","Lisboa", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(1, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        partidaService.generarPreguntasPartida(partida);
        List<Pregunta> preguntas = partida.getPreguntas();
        assertThat(preguntas.size()).isEqualTo(modoDeJuego.getNumeroDePreguntas());
        assertThat(preguntas.get(0).getCategoria()).isEqualTo(Categoria.GEOGRAFIA);
        assertThat(preguntas.get(0).getEnunciado()).isEqualTo("Capital de España");
    }

    @Test
    @Transactional
    public void GenerarUnaPreguntaTest_SinPreguntasSuficientesDeCategoria(){
        Pregunta pregunta1 = preguntaService.crearPregunta("Capital de España","Madrid", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta2 = preguntaService.crearPregunta("Capital de Francia","París", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta3 = preguntaService.crearPregunta("Capital de Estonia","Tallín", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        Pregunta pregunta4 = preguntaService.crearPregunta("Capital de Portugal","Lisboa", Categoria.CIENCIA_Y_NATURALEZA, 15f);
        ModoDeJuego modoDeJuego = modoDeJuegoService.crateModoDeJuego(2, "Geo", "GEOGRAFIA");
        PartidaForm partidaForm = new PartidaForm(modoDeJuego.getId(), LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        assertThatThrownBy(() -> partidaService.generarPreguntasPartida(partida)).isInstanceOf(NotEnoughQuestionsException.class).hasMessageContaining("recuperado 0").hasMessageContaining("de 2");
        List<Pregunta> preguntas = partida.getPreguntas();
        assertThat(preguntas.size()).isEqualTo(0);
    }
}
