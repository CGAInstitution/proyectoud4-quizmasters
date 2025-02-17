package quizmasters.controller;

import quizmasters.controller.exception.UsuarioSinPermisosException;
import quizmasters.service.AuthService;
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
