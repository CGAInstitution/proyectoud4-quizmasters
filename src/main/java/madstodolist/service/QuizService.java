package madstodolist.service;

import madstodolist.dto.QuizData;
import madstodolist.model.Partida;
import madstodolist.model.Pregunta;
import madstodolist.model.Usuario;
import madstodolist.repository.PartidaRepository;
import madstodolist.repository.PreguntaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    PartidaRepository partidaRepository;
    PreguntaRepository preguntaRepository;

    @Autowired
    public QuizService(PartidaRepository partidaRepository, PreguntaRepository preguntaRepository) {
        this.partidaRepository = partidaRepository;
        this.preguntaRepository = preguntaRepository;
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

}
