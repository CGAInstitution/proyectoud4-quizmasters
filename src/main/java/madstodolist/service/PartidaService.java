package madstodolist.service;

import jakarta.transaction.Transactional;
import madstodolist.dto.PartidaForm;
import madstodolist.dto.UsuarioData;
import madstodolist.model.*;
import madstodolist.repository.ModoDeJuegoRepository;
import madstodolist.repository.PartidaRepository;
import madstodolist.repository.PreguntaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartidaService {
    @Autowired
    PartidaRepository partidaRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private ModoDeJuegoRepository modoDeJuegoRepository;
    @Autowired
    private PreguntaRepository preguntaRepository;

    @Transactional
    public Partida guardarPartida(PartidaForm partidaForm){
        Optional<ModoDeJuego> modoDeJuego = modoDeJuegoRepository.findById(partidaForm.getModoDeJuegoId());
        if (modoDeJuego.isPresent()) {
            Partida partidaNueva = new Partida();
            partidaNueva.setDateTime(partidaForm.getDateTime());
            partidaNueva.setJoinable(partidaForm.isJoinable());
            partidaNueva.setModoDeJuego(modoDeJuego.get());
            partidaRepository.save(partidaNueva);
            return partidaNueva;
        } else {
            return null;
        }

    }

    public Partida findPartidaById(long id){
        return partidaRepository.findById(id).get();
    }

    @Transactional
    public Partida addUsuarioPartida(Partida partida, Usuario usuario){
        partida.addUsuario(usuario);
        partidaRepository.save(partida);
        return partida;
    }

    public void generarPreguntasPartida(Partida partida){
        ModoDeJuego modoDeJuego = partida.getModoDeJuego();
        List<Pregunta> preguntas = preguntaRepository.findByCategoria(Categoria.valueOf(modoDeJuego.getCategorias()));
        for (int i = 0; i<modoDeJuego.getNumeroDePreguntas(); i++){
            Optional<Pregunta> pregunta = preguntas.stream().findAny();
            if (pregunta.isPresent()){
                partida.addPregunta(pregunta.get());
            }
        }
    }

    @Transactional
    public Partida addUsuarioPartida(Partida partida, UsuarioData usuario){
        return addUsuarioPartida(partida, modelMapper.map(usuario, Usuario.class));
    }
}
