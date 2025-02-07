package madstodolist.controller;

import madstodolist.dto.PartidaForm;
import madstodolist.repository.PartidaRepository;
import madstodolist.service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
public class PartidaController {
    @Autowired
    PartidaRepository partidaRepository;
    @Autowired
    private PartidaService partidaService;

    @GetMapping("/partida/new")
    public String newPartida(Model model) {
        model.addAttribute("partidaForm", new PartidaForm());
        return "formPartida";
    }

    @PostMapping("/partida/new")
    public String guardarPartida(@ModelAttribute PartidaForm partidaForm){
        partidaForm.setDateTime(LocalDateTime.now());
        partidaService.guardarPartida(partidaForm);
        return "redirect:/partida/list";
    }

    @GetMapping("/partida/list")
    public String showAllPartidas(Model model){
        model.addAttribute("partidas", partidaRepository.findAll());
        return "listaPartidas";
    }

}
