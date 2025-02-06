package madstodolist.controller;

import madstodolist.model.Categoria;
import madstodolist.model.Pregunta;
import madstodolist.service.PreguntaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PreguntaController {

    @Autowired
    private PreguntaService preguntaService;

    @GetMapping("/preguntas")
    public String mostrarPreguntas(Model model) {
        List<Pregunta> preguntas = preguntaService.findAll();
        model.addAttribute("preguntas", preguntas);
        model.addAttribute("categorias", Categoria.values());
        return "preguntas";
    }

    @PostMapping("/preguntas")
    public String filtarPreguntas(@RequestParam(required = false)Categoria categoria, Model model) {
        List<Pregunta> preguntasFiltradas;
        if (categoria == null) {
            preguntasFiltradas = preguntaService.findAll();
        }else{
            preguntasFiltradas = preguntaService.findByCategoria(categoria);
        }
        model.addAttribute("preguntas", preguntasFiltradas);
        model.addAttribute("categorias", Categoria.values());
        model.addAttribute("selectedCategoria", categoria);
        return "preguntas";
    }

    @GetMapping("/preguntas/nueva")
    public String nuevaPregunta(Model model) {
        model.addAttribute("pregunta", new Pregunta());
        model.addAttribute("categorias", Categoria.values());
        return "formNuevaPregunta";
    }

    @PostMapping("/preguntas/nueva")
    public String nuevaPregunta(@ModelAttribute Pregunta pregunta, Model model, RedirectAttributes redirectAttributes) {
        preguntaService.crearPregunta(pregunta.getEnunciado(),pregunta.getRespuestaCorrecta(),pregunta.getCategoria(),pregunta.getPuntuacion());
        redirectAttributes.addFlashAttribute("mensaje", "Pregunta creada correctamente");
        return "redirect:/preguntas";
    }

    @DeleteMapping("/preguntas/{id}")
    @ResponseBody
    public String eliminarPregunta(@PathVariable(value="id") Long id, RedirectAttributes flash) {
        Pregunta pregunta = preguntaService.findById(id);
        if (pregunta == null) {
            throw new RuntimeException("La pregunta no existe");
        }
        preguntaService.deletePregunta(pregunta);
        return "OK";
    }

    @GetMapping("/preguntas/editar/{id}")
    public String editarPregunta(@PathVariable(value="id") Long id, Model model) {
        Pregunta preguntaActual = preguntaService.findById(id);
        model.addAttribute("pregunta", preguntaActual);
        model.addAttribute("categorias", Categoria.values());
        return "formEditarPregunta";
    }

    @PostMapping("/preguntas/editar/{id}")
    public String grabarPreguntaModificada(@PathVariable(value="id") Long id,@ModelAttribute Pregunta pregunta, Model model, RedirectAttributes flash) {
        Pregunta preguntaActual = preguntaService.findById(id);
        if (preguntaActual == null) {
            throw new RuntimeException("La pregunta editada no existe");
        }
        preguntaService.updatePregunta(preguntaActual,pregunta.getEnunciado(),pregunta.getRespuestaCorrecta(),pregunta.getCategoria(),pregunta.getPuntuacion());
        flash.addFlashAttribute("mensaje", "Pregunta modificada correctamente");
        return "redirect:/preguntas";

    }
}
