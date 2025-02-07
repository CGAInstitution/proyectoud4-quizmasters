package madstodolist.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "modos_de_juego")
public class ModoDeJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_de_preguntas")
    private int numeroDePreguntas;

    @Column(name = "nombre")
    private String nombre;


    @Column(name = "categorias")
    private String categorias;

    public ModoDeJuego(int numeroDePreguntas, String nombre, String categorias) {
        this.numeroDePreguntas = numeroDePreguntas;
        this.nombre = nombre;
        this.categorias = categorias;
    }

    public ModoDeJuego() {

    }

    public Long getId() {
        return id;
    }

    public String getCategorias() {
        return categorias;
    }

    public void setCategorias(String categorias) {
        this.categorias = categorias;
    }

    public int getNumeroDePreguntas() {
        return numeroDePreguntas;
    }

    public void setNumeroDePreguntas(int numeroDePreguntas) {
        this.numeroDePreguntas = numeroDePreguntas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
