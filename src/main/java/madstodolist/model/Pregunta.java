package madstodolist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "preguntas")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enunciado",nullable = false, unique = true)
    private String enunciado;

    @Column(name = "respuestaCorrecta",nullable = false)
    private String respuestaCorrecta;

    @Column(name = "categoria",nullable = false)
    private Categoria categoria;

    @Column(name="puntuacion",nullable = false)
    private Float puntuacion;

    public Pregunta() {
    }

    public Pregunta(String enunciado, String respuestaCorrecta, Categoria categoria, Float puntuacion) {
        this.enunciado = enunciado;
        this.respuestaCorrecta = respuestaCorrecta;
        this.categoria = categoria;
        this.puntuacion = puntuacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Float getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Float puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pregunta pregunta)) return false;
        return Objects.equals(id, pregunta.id) && Objects.equals(enunciado, pregunta.enunciado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enunciado);
    }

    @Override
    public String toString() {
        return "Pregunta{" +
                "id=" + id +
                ", enunciado='" + enunciado + '\'' +
                ", respuestaCorrecta='" + respuestaCorrecta + '\'' +
                ", categoria=" + categoria +
                ", puntuacion=" + puntuacion +
                '}';
    }
}
