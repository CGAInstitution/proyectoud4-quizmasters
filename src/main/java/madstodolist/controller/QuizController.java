package madstodolist.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import madstodolist.dto.QuizData;
import madstodolist.model.Partida;
import madstodolist.model.Pregunta;
import madstodolist.service.PartidaService;
import madstodolist.service.PreguntaService;
import madstodolist.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/quiz/{id}")
    public String quiz(@PathVariable("id") Long id, Model model) {
        model.addAttribute("idPartida", id);
        return "formQuiz";
    }

    @GetMapping("/iniciar-partida/{id}")
    public String iniciarPartida(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Partida partida = partidaService.findPartidaById(id);
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
