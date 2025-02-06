package madstodolist.controller;

import madstodolist.model.Pregunta;
import madstodolist.service.PreguntaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @GetMapping("/preguntas")
    public String mostrarPreguntas(Model model) {
        List<Pregunta> preguntas = preguntaService.findAll();
        model.addAttribute("preguntas", preguntas);
        return "preguntas";
    }
}
