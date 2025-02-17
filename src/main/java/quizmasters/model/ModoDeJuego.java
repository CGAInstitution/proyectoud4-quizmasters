package quizmasters.model;

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

    @ElementCollection(targetClass = Categoria.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "modo_juego_categorias", joinColumns = @JoinColumn(name = "modo_de_juego_id"))
    @Column(name = "categoria")
    @Enumerated(EnumType.STRING)
    private List<Categoria> categorias;

    public ModoDeJuego() {
    }

    public ModoDeJuego(int numeroDePreguntas, String nombre, List<Categoria> categorias) {
        this.numeroDePreguntas = numeroDePreguntas;
        this.nombre = nombre;
        this.categorias = categorias;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categoria) {
        this.categorias = categoria;
    }

    public Long getId() {
        return id;
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
