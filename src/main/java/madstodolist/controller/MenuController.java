package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioSinPermisosException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.AuthService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @Autowired
    AuthService authService;

    @GetMapping("/menuAdmin")
    public String showMenu(){
        if(authService.esUsuarioAdmin()){
            return "menuAdministrador";
        }
        else{
            throw new UsuarioSinPermisosException();
        }
    }
}
