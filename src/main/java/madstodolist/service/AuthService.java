package madstodolist.service;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
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
