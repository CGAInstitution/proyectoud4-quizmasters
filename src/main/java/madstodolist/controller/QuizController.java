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
        return "salaEspera";
    }

    @GetMapping("/quiz/salir/{id}")
    public String exitQuiz(@PathVariable("id") Long id, Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        Partida partida = partidaService.findPartidaById(id);
        partidaService.deleteUsuarioPartida(partida, idUsuario);
        managerUserSession.leavePartida();
        model.addAttribute("idPartida", id);
        return "redirect:/menuJuegos";
    }


    /*@GetMapping("/quiz/iniciar/{id}")
    public String empezarPartida(@PathVariable("id") Long id, ServletContext context, RedirectAttributes redirectAttributes, Model model) {
        Partida partida = partidaService.findPartidaById(id);
        List<Pregunta> preguntas = partida.getPreguntas();

        return "redirect:/quiz/mostrar-pregunta";
    }*/


    @GetMapping("/quiz/iniciar")
    public String iniciarPartida() {

        return "redirect:/quiz/mostrar-pregunta";
    }

    @GetMapping("/quiz/mostrar-pregunta")
    public String mostrarPregunta(HttpSession session, Model model) {
        QuizData quiz = (QuizData) servletContext.getAttribute("quiz");
        if (quiz == null) {
            return "redirect:/quiz";
        }
        if(quiz.getEsFinalizado()){
            return "redirect:/quiz/resultado";
        }
        model.addAttribute("quiz", quiz);
        return "formResponderPregunta";
    }

    @PostMapping("/quiz/responder")
    public String responder(String respuesta,HttpSession session) {
        QuizData quiz = (QuizData) servletContext.getAttribute("quiz");
        Pregunta preguntaActual = quiz.getPreguntaActual();
        quizService.responderPregunta(quiz,preguntaActual, managerUserSession.usuarioLogeado(), respuesta);
        return "salaEsperaQuiz";
    }

    @GetMapping("/quiz/resultado")
    public String mostrarResultado(HttpSession session, Model model) {
        QuizData quiz = (QuizData) servletContext.getAttribute("quiz");
        Float puntuacion = quiz.getPuntuaciones().get(managerUserSession.usuarioLogeado());
        model.addAttribute("puntuacion", puntuacion);
        return "salaResultados";
    }

    
}
