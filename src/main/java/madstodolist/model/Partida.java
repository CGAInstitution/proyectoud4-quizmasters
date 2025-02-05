package madstodolist.model;

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

    @Column(name = "modo_juego")
    private String modoDeJuego;

    @ManyToMany
    @JoinTable(name = "jugadores_partidas", joinColumns = @JoinColumn(name = "partida_id"), inverseJoinColumns = @JoinColumn(name = "jugador_id"))
    private List<Usuario> usuarios =  new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "ganador")
    private Usuario ganador;

    public Partida(long id, LocalDateTime dateTime, boolean joinable, List<Usuario> usuarios, String modoDeJuego, Usuario ganador) {
        this.id = id;
        this.dateTime = dateTime;
        this.joinable = joinable;
        this.usuarios = usuarios;
        this.modoDeJuego = modoDeJuego;
        this.ganador = ganador;
    }

    public Partida(LocalDateTime dateTime, boolean joinable, String modoDeJuego) {
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

    public String getModoDeJuego() {
        return modoDeJuego;
    }

    public void setModoDeJuego(String modoDeJuego) {
        this.modoDeJuego = modoDeJuego;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario getGanador() {
        return ganador;
    }

    public void setGanador(Usuario ganador) {
        this.ganador = ganador;
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
