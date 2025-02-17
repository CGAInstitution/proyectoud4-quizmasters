package quizmasters.service;

import quizmasters.dto.QuizData;
import quizmasters.dto.UsuarioData;
import quizmasters.model.Partida;
import quizmasters.model.Pregunta;
import quizmasters.model.Usuario;
import quizmasters.repository.PartidaRepository;
import quizmasters.repository.PreguntaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {

    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;
    PartidaRepository partidaRepository;
    PreguntaRepository preguntaRepository;

    @Autowired
    public QuizService(PartidaRepository partidaRepository, PreguntaRepository preguntaRepository, UsuarioService usuarioService, ModelMapper modelMapper) {
        this.partidaRepository = partidaRepository;
        this.preguntaRepository = preguntaRepository;
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
    }

    public QuizData iniciarQuiz(Long idPartida, List<Usuario> jugadores, List<Pregunta> preguntas) {
        Optional<Partida> optPartida = partidaRepository.findById(idPartida);
        if(optPartida.isEmpty()) {
            throw new RuntimeException("Partida no encontrada");
        }
        if(preguntas.isEmpty()) {
            throw new RuntimeException("Preguntas vacías al iniciar la partida");
        }
        Partida partida = optPartida.get();
        partida.setJoinable(false);
        partidaRepository.save(partida);
        List<Long> idJugadores = new ArrayList<>();
        for (Usuario jugador : jugadores) {
            idJugadores.add(jugador.getId());
        }
        QuizData quiz = new QuizData(idPartida, idJugadores, preguntas);

        return quiz;
    }

    public QuizData responderPregunta(QuizData quizActual, Pregunta pregunta, Long idJugador, String respuesta ) {
        if(quizActual == null) {
            throw new RuntimeException("Quiz no está presente al responder");
        }
        if (pregunta == null) {
            throw new RuntimeException("Pregunta no encontrada al responder");
        }
        if(idJugador == null) {
            throw new RuntimeException("Jugador no encontrado");
        }
        if(respuesta == null) {
            throw new RuntimeException("Se ha producido un error en la respuesta.");
        }
        if(respuesta.equalsIgnoreCase(pregunta.getRespuestaCorrecta())){
            quizActual.actualizarPuntuacion(pregunta.getPuntuacion(), idJugador);
        }
        return quizActual;
    }



    public QuizData avanzarPregunta(QuizData quizActual){
        if(quizActual == null) {
            throw new RuntimeException("Quiz no encontrado para avanzar pregunta");
        }
        if(!quizActual.getPreguntasRestantes().isEmpty()) {
            quizActual.setPreguntaActual(quizActual.getPreguntasRestantes().remove(0));
        }
        else{
            quizActual.setEsFinalizado(true);
        }
        return quizActual;
    }

    public List<Map.Entry<String, Float>> getPuntuacionesFinales(QuizData quizData){
        List<Map.Entry<Long, Float>> puntuaciones = quizData.getPuntuaciones().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).toList();
        List<Map.Entry<String, Float>> puntuacionesUsuarios = new ArrayList<>();
        for (Map.Entry<Long, Float> puntuacion :  puntuaciones){
            puntuacionesUsuarios.add(Map.entry(usuarioService.findById(puntuacion.getKey()).getNombre(), puntuacion.getValue()));
        }
        Collections.reverse(puntuacionesUsuarios);
        return puntuacionesUsuarios;
    }

    public Usuario getGanador(QuizData quizData){
        List<Map.Entry<Long, Float>> puntuaciones = quizData.getPuntuaciones().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue)).toList();
        UsuarioData usuario = usuarioService.findById(puntuaciones.get(0).getKey());
        return modelMapper.map(usuario, Usuario.class);
    }

}
