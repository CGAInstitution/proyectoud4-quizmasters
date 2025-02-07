package madstodolist.controller;

import jakarta.servlet.http.HttpSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.ModoDeJuego;
import madstodolist.service.ModoDeJuegoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ModoDeJuegoController {

    @Autowired
    ModoDeJuegoService modoDeJuegoService;

    @GetMapping("/modosDeJuego")
    public String listadoTareas(Model model) {


        List<ModoDeJuego> modosDeJuego = modoDeJuegoService.findAllModosDeJuego();
        model.addAttribute("modosDeJuego", modosDeJuego);

        return "listaModosDeJuego";

    }


    @DeleteMapping("/modosDeJuego/{id}")
    @ResponseBody
    public String eliminarTarea (@PathVariable(value = "id") Long id) {

        modoDeJuegoService.deleteModoDeJuego(id);

        return "ok";

    }
}
