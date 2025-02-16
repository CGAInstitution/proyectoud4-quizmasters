package madstodolist.restcontroller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

//    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
//    private final CopyOnWriteArrayList<SseEmitter> emittersPasarPregunta = new CopyOnWriteArrayList<>();
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new HashMap<>();

    @GetMapping("/sse/start")
    @Async
    public SseEmitter streamEmpezarPartida() {
        return addEmitter("start");
    }

    @GetMapping("/sse/pasarPregunta")
    @Async
    public SseEmitter streamPasarPregunta() {
        return addEmitter("pregunta");
    }

    @GetMapping("/sse/joinable")
    @Async
    public SseEmitter streamRecargarPartidas() {
        return addEmitter("joinable");
    }

    @GetMapping("/sse/jugadores")
    @Async
    public SseEmitter streamRecargarJugadores() {
        return addEmitter("jugadores");
    }

    @GetMapping("/sse/cancel")
    @Async
    public SseEmitter streamCancelarPArtida() {
        return addEmitter("cancel");
    }

    public SseEmitter addEmitter(String type){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.putIfAbsent(type, new CopyOnWriteArrayList<>());// Keeps connection open
        emitters.get(type).add(emitter);

        // Remove emitter when completed
        emitter.onCompletion(() -> emitters.get(type).remove(emitter));
        emitter.onTimeout(() -> emitters.get(type).remove(emitter));
        return emitter;
    }

    public void sendUpdate(String type) {
        for (SseEmitter emitter : emitters.get(type)) {
            try {
                if (emitter != null) {
                    emitter.send(SseEmitter.event().data(""));
                }
            } catch (IOException | IllegalStateException e) {
                emitter.complete();
                emitters.get(type).remove(emitter); // Remove emitter if it fails
            }
        }
    }
    public void cleanEmitters(String type) {
        for (SseEmitter emitter : emitters.get(type)) {
            if (emitter != null) {
                emitter.complete();
            }
        }
    }

    public void completeSignal(String type){
        try {
            sendUpdate(type);
            cleanEmitters(type);
        } catch (NullPointerException ne){

        }
    }
}