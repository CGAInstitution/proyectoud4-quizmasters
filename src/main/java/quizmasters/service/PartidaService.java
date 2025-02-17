package quizmasters.service;

import jakarta.transaction.Transactional;
import quizmasters.dto.PartidaForm;
import quizmasters.dto.UsuarioData;
import quizmasters.model.*;
import quizmasters.repository.PartidaRepository;
import quizmasters.service.exception.NotEnoughQuestionsException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PartidaService {
    @Autowired
    PartidaRepository partidaRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ModoDeJuegoService modoDeJuegoService;
    @Autowired
    private PreguntaService preguntaService;

    @Transactional
    public Partida guardarPartida(PartidaForm partidaForm){
        ModoDeJuego modoDeJuego = modoDeJuegoService.findById(partidaForm.getModoDeJuegoId());
        if (modoDeJuego!= null) {
            Partida partidaNueva = new Partida();
            partidaNueva.setDateTime(partidaForm.getDateTime());
            partidaNueva.setJoinable(partidaForm.isJoinable());
            partidaNueva.setModoDeJuego(modoDeJuego);
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
            preguntas.addAll(preguntaService.findByCategoria(categoria));
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
    public List<Partida> findPlayable(){
        return partidaRepository.findByFinished(false);
    }

    @Transactional
    public void setJoinable(Partida partida, boolean joinable){
        partida.setJoinable(joinable);
        partidaRepository.save(partida);
    }

    @Transactional
    public void setFinished(Partida partida, boolean finished){
        partida.setFinished(finished);
        partidaRepository.save(partida);
    }

    @Transactional
    public void setGanador(Partida partida, Usuario usuario){
        partida.setGanador(usuario);
        partida.setFinished(true);
        partidaRepository.save(partida);
    }

    public List<ModoDeJuego> findAllModosDeJuego(){
        return modoDeJuegoService.findAllModosDeJuego();
    }
}
