package madstodolist.controller;

import jakarta.servlet.http.HttpSession;
import madstodolist.dto.PartidaForm;
import madstodolist.model.Partida;
import madstodolist.model.Usuario;
import madstodolist.repository.ModoDeJuegoRepository;
import madstodolist.repository.PartidaRepository;
import madstodolist.repository.UsuarioRepository;
import madstodolist.service.PartidaService;
import madstodolist.service.UsuarioService;
import madstodolist.service.exception.NotEnoughQuestionsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @GetMapping("/partida/new")
    public String newPartida(Model model) {
        model.addAttribute("partidaForm", new PartidaForm());
        model.addAttribute("modosDeJuego", modoDeJuegoRepository.findAll());
        return "formPartida";
    }

    @PostMapping("/partida/new")
    public String guardarPartida(@ModelAttribute PartidaForm partidaForm){
        partidaForm.setDateTime(LocalDateTime.now());
        partidaService.guardarPartida(partidaForm);
        return "redirect:/partida/list";
    }

    @GetMapping("/login/adminToUser")
    public String deAdminAUser (){
        return "redirect:/partida/unirse";
    }

    @GetMapping("/partida/edit/{id}")
    public String editarPartida(@PathVariable("id") Long id, Model model){
        Partida partida = partidaService.findPartidaById(id);
        model.addAttribute("partida", partida);
        model.addAttribute("modosDeJuego", modoDeJuegoRepository.findAll());
        return "formPartidaEdit";
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

    @GetMapping("/partida/unirse")
    public String showJoinablePartidas(Model model, HttpSession session) {
        Long usuarioLoggeado = (Long) session.getAttribute("idUsuarioLogeado");

        if (usuarioLoggeado == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioRepository.findById(usuarioLoggeado).orElse(null);

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("partidas", partidaService.findJoinable());

        return "unirsePartida";
    }

}
