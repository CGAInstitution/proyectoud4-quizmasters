package madstodolist.controller;

import jakarta.validation.Valid;
import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UltimoAdministradorException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioSinPermisosException;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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
        return "listaUsuarios";
    }

    @GetMapping("/usuarios/new")
    public String toNewUsuarioForm(Model model){
        comprobarUsuarioAdministrador();
        model.addAttribute("usuarioData", new UsuarioData());
        return "formNuevoUsuario";
    }

    @GetMapping("/usuarios/edit/{id}")
    public String toEditUsuario(@PathVariable("id") Long id, Model model){
        comprobarUsuarioAdministrador();
        UsuarioData usuarioData = usuarioService.findById(id);
        model.addAttribute("usuarioData", usuarioData);
        return "formEditarUsuario";
    }

    @PostMapping("/usuarios/new")
    public String createNewUsuario(@Valid UsuarioData usuarioData, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            return "formNuevoUsuario";
        }
        if (usuarioService.findByEmail(usuarioData.getEmail()) != null) {
            model.addAttribute("usuarioData", usuarioData);
            model.addAttribute("error", "El usuario " + usuarioData.getEmail() + " ya existe");
            return "formNuevoUsuario";
        }
        usuarioService.registrar(usuarioData);
        redirectAttributes.addFlashAttribute("resultado", "Usuario creado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/edit/{id}")
    public String modificarUsuario(@PathVariable("id") Long id, @Valid UsuarioData usuarioData, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()){
            return "formEditarUsuario";
        }
        comprobarUsuarioAdministrador();
        usuarioService.updateUsuario(id, usuarioData);
        redirectAttributes.addFlashAttribute("resultado", "Usuario modificado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("usuarios/cambiarPermisos/{id}")
    public String giveAdmin(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes){
        comprobarUsuarioAdministrador();
        if (managerUserSession.usuarioLogeado() == id){
            redirectAttributes.addFlashAttribute("mensaje", "Un administrador no se puede quitar permisos de administrador");
            return "redirect:/usuarios";
        }
        try {
            usuarioService.cambiarPermisosUsuario(id);
        } catch (UltimoAdministradorException e){
            redirectAttributes.addFlashAttribute("mensaje", "Debe haber al menos un usuario administrador");
        }
        return "redirect:/usuarios";
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Map<String, String>> deleteUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        comprobarUsuarioAdministrador();
        if (managerUserSession.usuarioLogeado() == id ){
            redirectAttributes.addFlashAttribute("mensaje", "Un administrador no se puede eliminar a sí mismo");
        } else {
            usuarioService.deleteUsuario(id);
            redirectAttributes.addFlashAttribute("resultado", "Usuario borrado correctamente");
        }
        Map<String,String> response = new HashMap<>();
        response.put("redirectUrl","/usuarios");
        return ResponseEntity.ok(response);
    }
}
