package madstodolist.dto;

import madstodolist.model.Pregunta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuizData {
    private Long idPartida;
    private List<Long> idsJugadores;
    private List<Pregunta> preguntasRestantes;
    private Pregunta preguntaActual;
    private Map<Long, Float> puntuaciones;
    public Boolean esFinalizado;

    public QuizData() {
    }

    public QuizData(Long idPartida, List<Long> idsJugadores, List<Pregunta> preguntasRestantes) {
        this.idPartida = idPartida;
        this.idsJugadores = idsJugadores;
        this.preguntasRestantes = preguntasRestantes;
        this.esFinalizado = false;
        this.preguntaActual = preguntasRestantes.remove(0);
        inicializarPuntuaciones();
    }

    public Long getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Long idPartida) {
        this.idPartida = idPartida;
    }

    public List<Long> getIdsJugadores() {
        return idsJugadores;
    }

    public void setIdsJugadores(List<Long> idsJugadores) {
        this.idsJugadores = idsJugadores;
    }

    public List<Pregunta> getPreguntasRestantes() {
        return preguntasRestantes;
    }

    public void setPreguntasRestantes(List<Pregunta> preguntasRestantes) {
        this.preguntasRestantes = preguntasRestantes;
    }

    public Pregunta getPreguntaActual() {
        return preguntaActual;
    }

    public void setPreguntaActual(Pregunta preguntaActual) {
        this.preguntaActual = preguntaActual;
    }

    public Map<Long, Float> getPuntuaciones() {
        return puntuaciones;
    }

    public void setPuntuaciones(Map<Long, Float> puntuaciones) {
        this.puntuaciones = puntuaciones;
    }

    public Boolean getEsFinalizado() {
        return esFinalizado;
    }

    public void setEsFinalizado(Boolean esFinalizado) {
        this.esFinalizado = esFinalizado;
    }

    private void inicializarPuntuaciones(){
        this.puntuaciones = new HashMap<Long, Float>();
        for(Long idUsuario : this.idsJugadores){
            puntuaciones.put(idUsuario, 0.0f);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QuizData quizData)) return false;
        return Objects.equals(idPartida, quizData.idPartida);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idPartida);
    }
}
