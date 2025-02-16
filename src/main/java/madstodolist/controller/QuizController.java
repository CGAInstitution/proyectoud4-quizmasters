package madstodolist.controller;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.QuizData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Partida;
import madstodolist.model.Pregunta;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.restcontroller.SseController;
import madstodolist.service.PartidaService;
import madstodolist.service.PreguntaService;
import madstodolist.service.QuizService;
import madstodolist.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private PreguntaService preguntaService;
    @Autowired
    private PartidaService partidaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ManagerUserSession managerUserSession;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SseController sseController;
    @Autowired
    private ServletContext servletContext;

    @GetMapping("/menuJuegos")
    public String showJoinablePartidas(Model model, HttpSession session) {
        Long usuarioLoggeado = (Long) session.getAttribute("idUsuarioLogeado");

        if (usuarioLoggeado == null) {
            return "redirect:/login";
        }
        Usuario usuario = modelMapper.map(usuarioService.findById(usuarioLoggeado), Usuario.class);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("partidas", partidaService.findJoinable());


        return "menuJuego";
    }

    @GetMapping("/quiz/join/{id}")
    public String joinQuiz(@PathVariable("id") Long id, Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        Partida partida = partidaService.findPartidaById(id);
        UsuarioData usuarioData = usuarioService.findById(idUsuario);
        partidaService.addUsuarioPartida(partida, usuarioData);
        managerUserSession.addPartida(id);
        model.addAttribute("idPartida", id);
        model.addAttribute("jugador", usuarioData.getNombre());
        sseController.completeSignal("jugadores" +id);
        return "salaEspera";
    }

    @GetMapping("/quiz/salir/{id}")
    public String exitQuiz(@PathVariable("id") Long id, Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        Partida partida = partidaService.findPartidaById(id);
        partidaService.deleteUsuarioPartida(partida, idUsuario);
        managerUserSession.leavePartida();
        model.addAttribute("idPartida", id);
        sseController.completeSignal("jugadores" +id);
        return "redirect:/menuJuegos";
    }


    @GetMapping("/quiz/iniciar/{id}")
    public String iniciarPartida(@PathVariable("id") Long id) {

        return "redirect:/quiz/mostrar-pregunta/" + id;
    }

    @GetMapping("/quiz/mostrar-pregunta/{id}")
    public String mostrarPregunta(@PathVariable("id") Long id, Model model) {
        QuizData quiz = (QuizData) servletContext.getAttribute("quiz" +id);
        if (quiz == null) {
            return "redirect:/quiz";
        }
        if(quiz.getEsFinalizado()){

            return "redirect:/quiz/resultado/" +id;
        }
        model.addAttribute("quiz", quiz);
        model.addAttribute("idPartida", id);
        return "formResponderPregunta";
    }

    @PostMapping("/quiz/responder/{id}")
    public String responder(@PathVariable("id") Long id, String respuesta, Model model) {
        QuizData quiz = (QuizData) servletContext.getAttribute("quiz" +id);
        Pregunta preguntaActual = quiz.getPreguntaActual();
        quizService.responderPregunta(quiz,preguntaActual, managerUserSession.usuarioLogeado(), respuesta);
        model.addAttribute("idPartida", id);
        return "salaEsperaQuiz";
    }

    @GetMapping("/quiz/resultado/{id}")
    public String mostrarResultado(@PathVariable("id") Long id, Model model) {
        QuizData quiz = (QuizData) servletContext.getAttribute("quiz" +id);
        model.addAttribute("puntuaciones", quizService.getPuntuacionesFinales(quiz));
        return "salaResultados";
    }

    
}
