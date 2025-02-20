package quizmasters.service;

import quizmasters.controller.exception.UltimoAdministradorException;
import quizmasters.dto.UsuarioData;
import quizmasters.model.Partida;
import quizmasters.model.Usuario;
import quizmasters.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Se añade un usuario en la aplicación.
    // El email y password del usuario deben ser distinto de null
    // El email no debe estar registrado en la base de datos
    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else if (usuario.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (usuario.getPassword() == null)
            throw new UsuarioServiceException("El usuario no tiene password");
        else {
            Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);
            usuarioNuevo = usuarioRepository.save(usuarioNuevo);
            return modelMapper.map(usuarioNuevo, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public void updateUsuario(UsuarioData usuarioData){
        Usuario usuario = modelMapper.map(usuarioData, Usuario.class);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void updateUsuario(Long id, UsuarioData usuarioData){
        UsuarioData usuario = findById(id);
        usuario.setNombre(usuarioData.getNombre());
        usuario.setFechaNacimiento(usuarioData.getFechaNacimiento());
        usuarioRepository.save(modelMapper.map(usuario, Usuario.class));
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAdmins(){
        return usuarioRepository.findByIsAdmin(true);
    }

    @Transactional
    public void cambiarPermisosUsuario(Long id){
        UsuarioData usuarioData = findById(id);
        usuarioData.setAdmin(!usuarioData.isAdmin());
        updateUsuario(usuarioData);
        if (getAdmins().isEmpty()) {
            throw new UltimoAdministradorException();
        }
    }

    @Transactional
    public void deleteUsuario(Long id){
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        usuario.ifPresent(value -> usuarioRepository.delete(value));
    }


    public Integer getNumeroPartidasGanadas(Long id){
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return usuario.get().getPartidasGanadas().size();
        }
        else{
            return 0;
        }
    }
}
