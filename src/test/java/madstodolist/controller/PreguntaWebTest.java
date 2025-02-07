package madstodolist.controller;

import madstodolist.model.Categoria;
import madstodolist.model.Pregunta;
import madstodolist.service.PreguntaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
public class PreguntaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreguntaService preguntaService;

    @Test
    public void preguntaWebTest() throws Exception {
        //GIVEN
        //Moqueamos la llamada al servicio
        Pregunta preguntaGeografia_1 = new Pregunta("¿Cúal es la capital de Bután?","Timbu", Categoria.GEOGRAFIA,10f);
        Pregunta preguntaGeografia_2 = new Pregunta("¿Cúal es la capital de Francia?","Paris",Categoria.GEOGRAFIA,10f);
        Pregunta preguntaGeografia_3 = new Pregunta("¿Cúal es la capital de Rusia?","Moscú",Categoria.GEOGRAFIA,10f);
        Pregunta preguntaHistoria_1 = new Pregunta("¿Qué año estalló la Revolución Francesa?","1789",Categoria.HISTORIA,10f);
        List<Pregunta> preguntas  = new ArrayList<Pregunta>();
        preguntas.add(preguntaGeografia_1);
        preguntas.add(preguntaGeografia_2);
        preguntas.add(preguntaGeografia_3);
        preguntas.add(preguntaHistoria_1);

        when(preguntaService.findAll()).thenReturn(preguntas);

        //WHEN
        mockMvc.perform(get("/preguntas"))
                .andExpect(status().isOk())
                .andExpect(view().name("preguntas"))
                .andExpect(content().string(containsString("¿Cúal es la capital de Francia?")))
                .andExpect(content().string(containsString("¿Cúal es la capital de Rusia?")));
    }

    @Test
    public void test_nuevaPregunta() throws Exception {
        String urlPeticion = "/preguntas/nueva";
        String urlAction = "action=\"/preguntas/nueva";

        this.mockMvc.perform(get(urlPeticion))
                .andExpect((content().string(allOf(
                        containsString("form method=\"post\""),
                        containsString(urlAction)
                ))));
    }
}
