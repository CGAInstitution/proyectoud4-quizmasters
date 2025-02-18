package quizmasters.repository;

import jakarta.transaction.Transactional;
import quizmasters.model.ModoDeJuego;
import quizmasters.model.Partida;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PartidaTest {

    @Autowired
    PartidaRepository partidaRepository;
    @Autowired
    private ModoDeJuegoRepository modoDeJuegoRepository;

    @Test
    @Transactional
    public void crearPartida(){
        ModoDeJuego modoDeJuego = new ModoDeJuego();
        modoDeJuego.setNombre("Clásico");
        Partida partida = new Partida(LocalDateTime.now(), true, modoDeJuego);

        partidaRepository.save(partida);

        assertThat(partida.getId()).isNotNull();

        assertThat(partidaRepository.findById(partida.getId())).isPresent();

        Partida partidaDB = partidaRepository.findById(partida.getId()).get();

        assertThat(partidaDB.getModoDeJuego().getNombre()).isEqualTo("Clásico");
    }

    @Test
    @Transactional
    public void filterJoinablePartidas(){
        ModoDeJuego modoDeJuego = new ModoDeJuego();
        modoDeJuegoRepository.save(modoDeJuego);
        Partida partida1 = new Partida(LocalDateTime.now(), true, modoDeJuego);
        Partida partida2 = new Partida(LocalDateTime.now(), false, modoDeJuego);
        partidaRepository.save(partida1);
        partidaRepository.save(partida2);

        List<Partida> partidas = partidaRepository.findAll();
        assertThat(partidas).contains(partida1);
        assertThat(partidas).contains(partida2);
        List<Partida> partidasJoineables = partidaRepository.findByJoinable(true);
        assertThat(partidasJoineables).contains(partida1);
        assertThat(partida2).isNotIn(partidasJoineables);
    }
}
