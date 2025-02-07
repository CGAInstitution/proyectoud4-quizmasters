package madstodolist.service;

import madstodolist.model.Categoria;
import madstodolist.model.Pregunta;
import madstodolist.repository.PreguntaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PreguntaService {

    Logger logger = LoggerFactory.getLogger(PreguntaService.class);

    @Autowired
    private PreguntaRepository preguntaRepository;

    public Pregunta crearPregunta(String enunciado, String respuestaCorrecta, Categoria categoria, Float puntuacion){
        if(preguntaRepository.findByEnunciado(enunciado).isPresent()){
            logger.debug("No se puede crear pregunta. Ya existe ese enunciado");

            throw new RuntimeException("Ya existe enunciado de pregunta");
        }
        if(!tieneCamposObligatorios(enunciado,respuestaCorrecta,categoria,puntuacion)){
            logger.debug("No se puede crear pregunta. Hay campos con datos sin cubrir");
            throw new RuntimeException("Hay campos con datos sin cubrir en la pregunta");
        }
        Pregunta pregunta = new Pregunta(enunciado, respuestaCorrecta, categoria, puntuacion);
        preguntaRepository.save(pregunta);
        return pregunta;
    }

    private Boolean tieneCamposObligatorios(String enunciado, String respuestaCorrecta, Categoria categoria, Float puntuacion){
        if(enunciado == null || enunciado.isEmpty() || respuestaCorrecta.isEmpty() || respuestaCorrecta == null || categoria == null || puntuacion == null){
            return false;
        }else{
            return true;
        }
    }

    public Pregunta findById(Long id){
        return preguntaRepository.findById(id).orElse(null);
    }

    public List<Pregunta> findByCategoria(Categoria categoria){
        return preguntaRepository.findByCategoria(categoria);
    }

    public List<Pregunta> findAll(){
        return (List<Pregunta>) preguntaRepository.findAll();
    }

    public Pregunta updatePregunta(Pregunta pregunta, String newEnunciado,String newRespuesta, Categoria newCategoria, Float newPuntuacion){
        if(pregunta == null){
            logger.debug("No se puede actualizar pregunta. La pregunta está vacía");
            throw new RuntimeException("No se puede actualizar pregunta. Pregunta null");
        }
        if(!newEnunciado.equals(pregunta.getEnunciado()) && preguntaRepository.findByEnunciado(newEnunciado).isPresent()){
            logger.debug("No se puede actualizar pregunta. Ya existe ese enunciado");

            throw new RuntimeException("Ya existe enunciado de pregunta");
        }
        if(newEnunciado != null && !newEnunciado.equals(pregunta.getEnunciado())){
            pregunta.setEnunciado(newEnunciado);
        }
        if(newRespuesta != null && !newRespuesta.equals(pregunta.getRespuestaCorrecta())){
            pregunta.setRespuestaCorrecta(newRespuesta);
        }
        if(newCategoria != null && !newCategoria.equals(pregunta.getCategoria())){
            pregunta.setCategoria(newCategoria);
        }
        if(newPuntuacion != null && !newPuntuacion.equals(pregunta.getPuntuacion())){
            pregunta.setPuntuacion(newPuntuacion);
        }
        preguntaRepository.save(pregunta);
        return pregunta;
    }

    public void deletePregunta(Pregunta pregunta){
        if(pregunta == null){
            throw new RuntimeException("La pregunta a eliminar es null");
        }
        preguntaRepository.delete(pregunta);
    }
}
