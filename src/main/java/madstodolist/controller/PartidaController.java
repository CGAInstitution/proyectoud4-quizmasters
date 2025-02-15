package madstodolist.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import madstodolist.dto.PartidaForm;
import madstodolist.dto.QuizData;
import madstodolist.model.Partida;
import madstodolist.model.Pregunta;
import madstodolist.model.Usuario;
import madstodolist.repository.ModoDeJuegoRepository;
import madstodolist.repository.UsuarioRepository;
import madstodolist.restcontroller.SseController;
import madstodolist.service.PartidaService;
import madstodolist.service.QuizService;
import madstodolist.service.UsuarioService;
import madstodolist.service.exception.NotEnoughQuestionsException;
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
    private ModoDeJuegoRepository modoDeJuegoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private SseController sseController;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private QuizService quizService;

    @GetMapping("/partida/new")
    public String newPartida(Model model) {
        model.addAttribute("partidaForm", new PartidaForm());
        model.addAttribute("modosDeJuego", modoDeJuegoRepository.findAll());
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
        return "redirect:/menuJuegos";
    }

    @GetMapping("/partida/edit/{id}")
    public String editarPartida(@PathVariable("id") Long id, Model model){
        Partida partida = partidaService.findPartidaById(id);
        model.addAttribute("partida", partida);
        model.addAttribute("modosDeJuego", modoDeJuegoRepository.findAll());
        return "formEditarPartida";
    }

    @RequestMapping("/partida/regenerate/{id}")
    public String regenrarPreguntas(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        Partida partida = partidaService.findPartidaById(id);
        try{
            if (partida != null){
                partidaService.generarPreguntasPartida(partida);
                partida.getPreguntas().forEach(System.out::println);
            }
        } catch (NotEnoughQuestionsException e){
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
        }
        return "redirect:/partida/edit/" + String.valueOf(id);
    }

    @GetMapping("/partida/list")
    public String showAllPartidas(Model model){
        model.addAttribute("partidas", partidaService.findAll());
        return "listaPartidas";
    }

    @GetMapping("/partida/prepare/{id}")
    public String prepareQuiz(@PathVariable("id") Long id, Model model) {
        model.addAttribute("idPartida", id);
        Partida partida = partidaService.findPartidaById(id);
        partidaService.setJoinable(partida, true);
        model.addAttribute("jugadores", partida.getUsuarios());
        return "menuPrepararPartida";
    }

    @GetMapping("/partida/cancel/{id}")
    public String cancelarQuiz(@PathVariable("id") Long id, Model model) {
        Partida partida = partidaService.findPartidaById(id);
        partidaService.setJoinable(partida, false);
        partidaService.cleanUsuariosPartida(partida);
        return "redirect:/partida/list";
    }

    @GetMapping("/partida/start/{id}")
    public String arrancarPartida(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Partida partida = partidaService.findPartidaById(id);
        List<Pregunta> preguntas = partida.getPreguntas();
        QuizData quiz = quizService.iniciarQuiz(partida.getId(), partida.getUsuarios() ,preguntas);
        servletContext.setAttribute("quiz", quiz);
        sseController.sendUpdate("empezar");
        sseController.cleanEmitters();
        model.addAttribute("idPartida", id);
        return "menuSiguientePregunta";
    }

    @GetMapping("/partida/avanzarPregunta/{id}")
    public String avanzarPregunta(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        QuizData quizData = (QuizData) servletContext.getAttribute("quiz");
        quizService.avanzarPregunta(quizData);
        sseController.sendUpdatePreguntas("empezar");
        sseController.cleanEmittersPreguntas();
        model.addAttribute("idPartida", id);
        return "menuSiguientePregunta";
    }

}
