package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioSinPermisosException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UsuarioController {

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

    @GetMapping("/usuarios")
    public String getAllUsuario(Model model){
        comprobarUsuarioAdministrador();
        model.addAttribute("usuarios", usuarioService.findAll());
        return "listUsuarios";
    }

    @GetMapping("usuarios/cambiarPermisos/{id}")
    public String giveAdmin(@PathVariable("id") Long id, Model model){
        comprobarUsuarioAdministrador();
        UsuarioData usuarioData = usuarioService.findById(id);
        usuarioData.setAdmin(!usuarioData.isAdmin());
        usuarioService.updateUsuario(usuarioData);
        return "redirect:/usuarios";
    }
}
