package madstodolist.service;

import jakarta.transaction.Transactional;
import madstodolist.dto.PartidaForm;
import madstodolist.dto.UsuarioData;
import madstodolist.model.*;
import madstodolist.repository.ModoDeJuegoRepository;
import madstodolist.repository.PartidaRepository;
import madstodolist.repository.PreguntaRepository;
import madstodolist.service.exception.NotEnoughQuestionsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    @Autowired
    private UsuarioService usuarioService;

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
        if (partida.getUsuarios().contains(usuario)){
            return null;
        }
        partida.addUsuario(usuario);
        partidaRepository.save(partida);
        return partida;
    }

    @Transactional
    public void deleteUsuarioPartida(Partida partida, Long id){
        Usuario usuario = modelMapper.map(usuarioService.findById(id), Usuario.class);
        partida.deleteUsuario(usuario);
        partidaRepository.save(partida);
    }

    @Transactional
    public Partida cleanUsuariosPartida(Partida partida){
        partida.setUsuarios(new ArrayList<>());
        partidaRepository.save(partida);
        return partida;
    }

    @Transactional
    public void generarPreguntasPartida(Partida partida) throws NotEnoughQuestionsException {
        partida.setPreguntas(new ArrayList<>());
        ModoDeJuego modoDeJuego = partida.getModoDeJuego();
        List<Pregunta> preguntas = getMatchingPreguntas(partida);
        if (preguntas.size() < modoDeJuego.getNumeroDePreguntas()) throw new NotEnoughQuestionsException(String.format("El numero de preguntas recuperado %d es menor al necesario de %d", preguntas.size(), modoDeJuego.getNumeroDePreguntas()));
        for (int i = 0; i<modoDeJuego.getNumeroDePreguntas(); i++){
            Random random = new Random();
            Pregunta pregunta = preguntas.remove(random.nextInt(preguntas.size()));
            partida.addPregunta(pregunta);
        }
        partidaRepository.save(partida);
    }

    public List<Pregunta> getMatchingPreguntas(Partida partida){
        List<Pregunta> preguntas = new ArrayList<>();
        for (Categoria categoria: partida.getModoDeJuego().getCategorias()){
            preguntas.addAll(preguntaRepository.findByCategoria(categoria));
        }
        return preguntas;
    }

    @Transactional
    public Partida addUsuarioPartida(Partida partida, UsuarioData usuario){
        return addUsuarioPartida(partida, modelMapper.map(usuario, Usuario.class));
    }

    @Transactional
    public List<Partida> findAll(){
        return partidaRepository.findAll();
    }

    @Transactional
    public List<Partida> findJoinable(){
        return partidaRepository.findByJoinable(true);
    }

    @Transactional
    public void setJoinable(Partida partida, boolean joinable){
        partida.setJoinable(joinable);
        partidaRepository.save(partida);
    }
}
