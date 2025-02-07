package madstodolist.service;

import jakarta.transaction.Transactional;
import madstodolist.dto.PartidaForm;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Partida;
import madstodolist.model.Usuario;
import madstodolist.repository.PartidaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartidaService {
    @Autowired
    PartidaRepository partidaRepository;
    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public Partida guardarPartida(PartidaForm partidaForm){
        Partida partidaNueva = modelMapper.map(partidaForm, Partida.class);
        partidaRepository.save(partidaNueva);
        return partidaNueva;
    }

    public Partida findPartidaById(long id){
        return partidaRepository.findById(id).get();
    }

    @Transactional
    public Partida addUsuarioPartida(Partida partida, Usuario usuario){
        partida.addUsuario(usuario);
        partidaRepository.save(partida);
        return partida;
    }

    @Transactional
    public Partida addUsuarioPartida(Partida partida, UsuarioData usuario){
        return addUsuarioPartida(partida, modelMapper.map(usuario, Usuario.class));
    }
}
