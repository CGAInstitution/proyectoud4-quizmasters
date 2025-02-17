package quizmasters.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "partidas")
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "datetime")
    private LocalDateTime dateTime;

    @Column(name = "joinable")
    private boolean joinable;

    @Column(name = "finished")
    private boolean finished = false;

    @ManyToOne
    @JoinColumn(name = "modo_juego")
    private ModoDeJuego modoDeJuego;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "preguntas_partidas", joinColumns = @JoinColumn(name = "partida_id"), inverseJoinColumns = @JoinColumn(name = "pregunta_id"))
    private List<Pregunta> preguntas =  new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "jugadores_partidas", joinColumns = @JoinColumn(name = "partida_id"), inverseJoinColumns = @JoinColumn(name = "jugador_id"))
    private List<Usuario> usuarios =  new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "ganador")
    private Usuario ganador;

    public Partida(long id, LocalDateTime dateTime, boolean joinable, List<Usuario> usuarios, ModoDeJuego modoDeJuego, Usuario ganador) {
        this.id = id;
        this.dateTime = dateTime;
        this.joinable = joinable;
        this.usuarios = usuarios;
        this.modoDeJuego = modoDeJuego;
        this.ganador = ganador;
    }

    public Partida(LocalDateTime dateTime, boolean joinable, ModoDeJuego modoDeJuego) {
        this.dateTime = dateTime;
        this.joinable = joinable;
        this.modoDeJuego = modoDeJuego;
    }

    public Partida() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ModoDeJuego getModoDeJuego() {
        return modoDeJuego;
    }

    public void setModoDeJuego(ModoDeJuego modoDeJuego) {
        this.modoDeJuego = modoDeJuego;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public void addUsuario(Usuario usuario){
        getUsuarios().add(usuario);
    }

    public void deleteUsuario(Usuario usuario){
        getUsuarios().remove(usuario);
    }

    public Usuario getGanador() {
        return ganador;
    }

    public void setGanador(Usuario ganador) {
        this.ganador = ganador;
    }



    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    public void addPregunta(Pregunta pregunta){
        getPreguntas().add(pregunta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Partida partida)) return false;
        return id == partida.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
