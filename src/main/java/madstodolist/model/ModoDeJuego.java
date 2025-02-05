package madstodolist.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "modos_de_juego")
public class ModoDeJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numero_de_preguntas")
    private int numeroDePreguntas;

    @Column(name = "nombre")
    private String nombre;

    @ElementCollection
    @Column(name = "categorias_incluidas")
    private List<String> categoriasIncluidas;

    public ModoDeJuego(int numeroDePreguntas, String nombre, List<String> categoriasIncluidas) {
        this.numeroDePreguntas = numeroDePreguntas;
        this.nombre = nombre;
        this.categoriasIncluidas = categoriasIncluidas;
    }

    public ModoDeJuego() {

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

    public List<String> getCategoriasIncluidas() {
        return categoriasIncluidas;
    }

    public void setCategoriasIncluidas(List<String> categoriasIncluidas) {
        this.categoriasIncluidas = categoriasIncluidas;
    }
}
