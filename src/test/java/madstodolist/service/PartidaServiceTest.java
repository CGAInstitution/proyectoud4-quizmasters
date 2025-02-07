package madstodolist.service;

import jakarta.transaction.Transactional;
import madstodolist.dto.PartidaForm;
import madstodolist.dto.UsuarioData;
import madstodolist.model.ModoDeJuego;
import madstodolist.model.Partida;
import madstodolist.repository.ModoDeJuegoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@SpringBootTest
public class PartidaServiceTest {

    @Autowired
    PartidaService partidaService;

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    private ModoDeJuegoRepository modoDeJuegoRepository;

    @Test
    @Transactional
    public void addPartida(){
        ModoDeJuego modoDeJuego = new ModoDeJuego();
        modoDeJuego.setNombre("Clásico");
        modoDeJuegoRepository.save(modoDeJuego);
        PartidaForm partidaForm = new PartidaForm("Clásico", LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        assertThat(partida.getId()).isNotNull();
        Partida partidaDB = partidaService.findPartidaById(partida.getId());
        assertThat(partidaDB).isNotNull();
        assertThat(partidaDB.getModoDeJuego().getNombre()).isEqualTo("Clásico");
        assertThat(partidaDB.getUsuarios()).isEmpty();
    }

    @Test
    @Transactional
    public void addUsuario(){
        PartidaForm partidaForm = new PartidaForm("Clásico", LocalDateTime.now(), true);
        Partida partida = partidaService.guardarPartida(partidaForm);
        Partida partidaDB = partidaService.findPartidaById(partida.getId());

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        usuarioService.registrar(usuario);
        UsuarioData usuarioDB = usuarioService.findByEmail(usuario.getEmail());

        partidaService.addUsuarioPartida(partidaDB, usuarioDB);

        Partida partidaConUsuario = partidaService.findPartidaById(partida.getId());
        assertThat(partidaConUsuario.getUsuarios()).isNotEmpty();
        assertThat(partidaConUsuario.getUsuarios().get(0).getPassword()).isEqualTo("12345678");
    }
}
