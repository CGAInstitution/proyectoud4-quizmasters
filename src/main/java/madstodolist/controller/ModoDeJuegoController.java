package madstodolist.controller;


import madstodolist.model.Categoria;
import madstodolist.model.ModoDeJuego;
import madstodolist.model.Pregunta;
import madstodolist.service.ModoDeJuegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/modosDeJuego/nuevo")
    public String nuevaPregunta(Model model) {
        model.addAttribute("modoDeJuego", new ModoDeJuego());
        model.addAttribute("listaDeCategoria", Categoria.values());
        return "formNuevoModoDeJuego";
    }

    @PostMapping("/modosDeJuego/nuevo")
    public String nuevaPregunta(@ModelAttribute ModoDeJuego modoDeJuego, Model model, RedirectAttributes redirectAttributes) {
        modoDeJuegoService.crateModoDeJuego(modoDeJuego.getNumeroDePreguntas(), modoDeJuego.getNombre(), modoDeJuego.getCategorias());
        redirectAttributes.addFlashAttribute("mensaje", "Pregunta creada correctamente");
        return "redirect:/modosDeJuego";
    }
}
