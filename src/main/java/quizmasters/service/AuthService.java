package quizmasters.service;

import quizmasters.authentication.ManagerUserSession;
import quizmasters.controller.exception.UsuarioNoLogeadoException;
import quizmasters.dto.UsuarioData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    ManagerUserSession managerUserSession;
    @Autowired
    private UsuarioService usuarioService;

    public boolean esUsuarioAdmin(){
        Long idUsuarioLogueado = managerUserSession.usuarioLogeado();
        UsuarioData usuarioData = usuarioService.findById(idUsuarioLogueado);
        if(usuarioData == null){
            throw new UsuarioNoLogeadoException();
        } else if (!usuarioData.isAdmin()) {
            return false;
        }
        return true;
    }

}
