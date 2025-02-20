package quizmasters.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import quizmasters.controller.exception.UsuarioSinPermisosException;
import quizmasters.dto.PartidaForm;
import quizmasters.dto.QuizData;
import quizmasters.model.Partida;
import quizmasters.model.Pregunta;
import quizmasters.restcontroller.SseController;
import quizmasters.service.*;
import quizmasters.service.exception.NotEnoughQuestionsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PartidaController {
    @Autowired
    private PartidaService partidaService;
    @Autowired
    private SseController sseController;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private QuizService quizService;
    @Autowired
    AuthService authService;

    @GetMapping("/partida/new")
    public String newPartida(Model model) {
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        model.addAttribute("partidaForm", new PartidaForm());
        model.addAttribute("modosDeJuego", partidaService.findAllModosDeJuego());
        return "formNuevaPartida";
    }

    @PostMapping("/partida/new")
    public String guardarPartida(@ModelAttribute PartidaForm partidaForm){
        partidaForm.setDateTime(LocalDateTime.now());
        partidaService.guardarPartida(partidaForm);
        return "redirect:/partida/list";
    }

    @GetMapping("/login/adminToUser")
    public String deAdminAUser (){
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        return "redirect:/menuJuegos";
    }

    @GetMapping("/partida/edit/{id}")
    public String editarPartida(@PathVariable("id") Long id, Model model){
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        Partida partida = partidaService.findPartidaById(id);
        model.addAttribute("partida", partida);
        model.addAttribute("modosDeJuego", partidaService.findAllModosDeJuego());
        return "formEditarPartida";
    }

    @RequestMapping("/partida/regenerate/{id}")
    public String regenrarPreguntas(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        Partida partida = partidaService.findPartidaById(id);
        try{
            if (partida != null){
                partidaService.generarPreguntasPartida(partida);
            }
        } catch (NotEnoughQuestionsException e){
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
        }
        return "redirect:/partida/edit/" + String.valueOf(id);
    }

    @GetMapping("/partida/list")
    public String showAllPartidas(Model model){
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        model.addAttribute("partidas", partidaService.findAll());
        return "listaPartidas";
    }

    @GetMapping("/partida/prepare/{id}")
    public String prepareQuiz(@PathVariable("id") Long id, Model model) {
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        model.addAttribute("idPartida", id);
        Partida partida = partidaService.findPartidaById(id);
        partidaService.setJoinable(partida, true);
        model.addAttribute("jugadores", partida.getUsuarios());
        sseController.completeSignal("joinable");
        return "menuPrepararPartida";
    }

    @GetMapping("/partida/cancel/{id}")
    public String cancelarQuiz(@PathVariable("id") Long id, Model model) {
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        Partida partida = partidaService.findPartidaById(id);
        partidaService.setJoinable(partida, false);
        partidaService.cleanUsuariosPartida(partida);
        sseController.completeSignal("joinable");
        sseController.completeSignal("cancel"+id);
        return "redirect:/partida/list";
    }

    @GetMapping("/partida/start/{id}")
    public String arrancarPartida(@PathVariable("id") Long id, Model model) {
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        Partida partida = partidaService.findPartidaById(id);
        List<Pregunta> preguntas = partida.getPreguntas();
        if (preguntas.isEmpty()){
            try {
                partidaService.generarPreguntasPartida(partida);
                preguntas = partida.getPreguntas();
            } catch (NotEnoughQuestionsException e){
                return "redirect:/partida/cancel/" + id;
            }
        }

        QuizData quiz = quizService.iniciarQuiz(partida.getId(), partida.getUsuarios() ,preguntas);
        servletContext.setAttribute("quiz" +id, quiz);
        sseController.completeSignal("start" + id);
        model.addAttribute("idPartida", id);
        model.addAttribute("pregunta", quiz.getPreguntaActual().getEnunciado());
        return "menuSiguientePregunta";
    }

    @GetMapping("/partida/avanzarPregunta/{id}")
    public String avanzarPregunta(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if(!authService.esUsuarioAdmin()){
            throw new UsuarioSinPermisosException();
        }
        QuizData quizData = (QuizData) servletContext.getAttribute("quiz" +id); // <---
        quizService.avanzarPregunta(quizData);
        sseController.completeSignal("pregunta"+ id);
        if (!quizData.getEsFinalizado()){
            model.addAttribute("idPartida", id);
            model.addAttribute("pregunta", quizData.getPreguntaActual().getEnunciado());
            return "menuSiguientePregunta";
        } else {
            Partida partida = partidaService.findPartidaById(id);
            model.addAttribute("puntuaciones", quizService.getPuntuacionesFinales(quizData));
            partidaService.setGanador(partida, quizService.getGanador(quizData));
            return "salaResultados";
        }

    }

}
