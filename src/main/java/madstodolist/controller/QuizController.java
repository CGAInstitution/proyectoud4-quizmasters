package madstodolist.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.PartidaDto;
import madstodolist.dto.QuizData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Partida;
import madstodolist.model.Pregunta;
import madstodolist.model.Usuario;
import madstodolist.restcontroller.SseController;
import madstodolist.service.PartidaService;
import madstodolist.service.PreguntaService;
import madstodolist.service.QuizService;
import madstodolist.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/quiz/join/{id}")
    public String joinQuiz(@PathVariable("id") Long id, Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        Partida partida = partidaService.findPartidaById(id);
        UsuarioData usuarioData = usuarioService.findById(idUsuario);
        partidaService.addUsuarioPartida(partida, usuarioData);
        managerUserSession.addPartida(id);
        model.addAttribute("idPartida", id);
        model.addAttribute("jugador", usuarioData.getNombre());
        return "formQuiz";
    }

    @GetMapping("/quiz/salir/{id}")
    public String exitQuiz(@PathVariable("id") Long id, Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        Partida partida = partidaService.findPartidaById(id);
        partidaService.deleteUsuarioPartida(partida, idUsuario);
        managerUserSession.leavePartida();
        model.addAttribute("idPartida", id);
        return "redirect:/partida/unirse";
    }

    @GetMapping("/quiz/prepare/{id}")
    public String prepareQuiz(@PathVariable("id") Long id, Model model) {
        model.addAttribute("idPartida", id);
        Partida partida = partidaService.findPartidaById(id);
        partidaService.setJoinable(partida, true);
        model.addAttribute("jugadores", partida.getUsuarios());
        managerUserSession.addPartida(id);
        return "quizPrepare";
    }

    @GetMapping("/quiz/prepare/cancel/{id}")
    public String cancelarQuiz(@PathVariable("id") Long id, Model model) {
        Partida partida = partidaService.findPartidaById(id);
        partidaService.setJoinable(partida, false);
        partidaService.cleanUsuariosPartida(partida);
        return "redirect:/partida/list";
    }

    @GetMapping("/iniciar-partida/{id}")
    public String empezarPartida(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Partida partida = partidaService.findPartidaById(id);
        List<Pregunta> preguntas = partida.getPreguntas();
        List<Long> idUsuarios = new ArrayList<>();
        idUsuarios.add(1l);

        QuizData quiz = quizService.iniciarQuiz(partida.getId(),idUsuarios,preguntas);
        session.setAttribute("quiz", quiz);
        return "redirect:/mostrar-pregunta";
    }

    @MessageMapping("/quiz/start/")
    @SendTo("/topic/start")
    public PartidaDto arrancarPartida() {
        Long idPartida = managerUserSession.getPartida();
        return new PartidaDto(idPartida);
    }

    @GetMapping("/iniciar-partida")
    public String iniciarPartida(HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Partida partida = partidaService.findPartidaById(managerUserSession.getPartida());
        List<Pregunta> preguntas = partida.getPreguntas();
        List<Long> idUsuarios = new ArrayList<>();
        idUsuarios.add(1l);

        QuizData quiz = quizService.iniciarQuiz(partida.getId(),idUsuarios,preguntas);
        session.setAttribute("quiz", quiz);
        return "redirect:/mostrar-pregunta";
    }

    @GetMapping("/mostrar-pregunta")
    public String mostrarPregunta(HttpSession session, Model model) {
        QuizData quiz = (QuizData) session.getAttribute("quiz");
        if (quiz == null) {
            return "redirect:/quiz";
        }
        if(quiz.getEsFinalizado()){
            return "redirect:/resultado";
        }
        model.addAttribute("quiz", quiz);
        return "formResponder";
    }

    @PostMapping("/responder")
    public String responder(String respuesta,HttpSession session) {
        QuizData quiz = (QuizData) session.getAttribute("quiz");
        Pregunta preguntaActual = quiz.getPreguntaActual();
        quiz = quizService.responderPregunta(quiz,preguntaActual,1L, respuesta);
        quiz = quizService.avanzarPregunta(quiz);
        session.setAttribute("quiz", quiz);
        return "redirect:/mostrar-pregunta";
    }

    @GetMapping("/resultado")
    public String mostrarResultado(HttpSession session, Model model) {
        QuizData quiz = (QuizData) session.getAttribute("quiz");
        Float puntuacion = quiz.getPuntuaciones().get(1l);
        model.addAttribute("puntuacion", puntuacion);
        return "resultado";
    }
}
