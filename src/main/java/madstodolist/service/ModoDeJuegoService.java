package madstodolist.service;

import madstodolist.model.ModoDeJuego;
import madstodolist.repository.ModoDeJuegoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ModoDeJuegoService {

    @Autowired
    private ModoDeJuegoRepository modoDeJuegoRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ModoDeJuegoService(ModoDeJuegoRepository modoDeJuegoRepository) {
        this.modoDeJuegoRepository = modoDeJuegoRepository;
    }

    @Transactional(readOnly = true)
    public ModoDeJuego findById(Long idJuego) {
        ModoDeJuego modoDeJuego = modoDeJuegoRepository.findById(idJuego).orElse(null);
        if (modoDeJuego == null) return null;
        else return modelMapper.map(modoDeJuego, ModoDeJuego.class);
    }

    @Transactional
    public void deleteModoDeJuego(Long idJuego) {

        ModoDeJuego modoDeJuego = modoDeJuegoRepository.findById(idJuego).orElse(null);
        if (modoDeJuego == null) {
            throw new IllegalArgumentException();
        }
        modoDeJuegoRepository.delete(modoDeJuego);
    }

    @Transactional
    public ModoDeJuego crateModoDeJuego (int numeroDePreguntas, String nombre, List<String> categoriasIncluidas) {

        modoDeJuegoRepository.findByNombre(nombre).ifPresent(modoDeJuego -> {
            throw new IllegalArgumentException("Ya existe un modo de juego con ese nombre");
        });

        ModoDeJuego modoDeJuego = new ModoDeJuego( numeroDePreguntas,  nombre, categoriasIncluidas);
        modoDeJuegoRepository.save(modoDeJuego);
        return modelMapper.map(modoDeJuego, ModoDeJuego.class);

    }

    @Transactional
    public ModoDeJuego updateModoDeJuego(Long idJuego, int numeroDePreguntas, String nombre, List<String> categoriasIncluidas) {

        ModoDeJuego modoDeJuego = modoDeJuegoRepository.findById(idJuego)
                .orElseThrow(() -> new IllegalArgumentException("Modo de juego no encontrado con ID: " + idJuego));

        if (modoDeJuegoRepository.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe un modo de juego con el nombre: " + nombre);
        }

        modoDeJuego.setNumeroDePreguntas(numeroDePreguntas);
        modoDeJuego.setNombre(nombre);
        modoDeJuego.setCategoriasIncluidas(categoriasIncluidas);

        return modoDeJuego;
    }


    @Transactional
    public ModoDeJuego updateModoDeJuego(ModoDeJuego modoDeJuego, int numeroDePreguntas, String nombre, List<String> categoriasIncluidas) {

        if (modoDeJuegoRepository.findByNombre(nombre).filter(mj -> !mj.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un modo de juego con ese nombre");
        }

        modoDeJuego.setNumeroDePreguntas(numeroDePreguntas);
        modoDeJuego.setNombre(nombre);
        modoDeJuego.setCategoriasIncluidas(categoriasIncluidas);

        return modoDeJuego; // JPA sincroniza automáticamente los cambios dentro de @Transactional
    }










}
