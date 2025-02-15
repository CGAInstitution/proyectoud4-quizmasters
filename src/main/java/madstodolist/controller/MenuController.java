package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioSinPermisosException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @Autowired
    ManagerUserSession managerUserSession;
    @Autowired
    private UsuarioService usuarioService;

    private void comprobarUsuarioAdministrador() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        UsuarioData usuarioData = usuarioService.findById(idUsuarioLogeado);
        if (usuarioData == null)
            throw new UsuarioNoLogeadoException();
        else if (!usuarioData.isAdmin()) {
            throw new UsuarioSinPermisosException();
        }
    }

    @GetMapping("/menuAdmin")
    public String showMenu(){
        comprobarUsuarioAdministrador();
        return "menuAdministrador";
    }
}
