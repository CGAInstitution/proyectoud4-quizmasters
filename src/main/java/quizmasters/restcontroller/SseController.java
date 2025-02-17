package quizmasters.restcontroller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new HashMap<>();

    @GetMapping("/sse/start/{id}")
    @Async
    public SseEmitter streamEmpezarPartida(@PathVariable("id") Long id) {
        return addEmitter("start" + id);
    }

    @GetMapping("/sse/pasarPregunta/{id}")
    @Async
    public SseEmitter streamPasarPregunta(@PathVariable("id") Long id) {
        return addEmitter("pregunta" + id);
    }

    @GetMapping("/sse/joinable")
    @Async
    public SseEmitter streamRecargarPartidas() {
        return addEmitter("joinable");
    }

    @GetMapping("/sse/jugadores/{id}")
    @Async
    public SseEmitter streamRecargarJugadores(@PathVariable("id") Long id) {
        return addEmitter("jugadores" + id);
    }

    @GetMapping("/sse/cancel/{id}")
    @Async
    public SseEmitter streamCancelarPartida(@PathVariable("id") Long id) {
        return addEmitter("cancel" + id);
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
                emitters.get(type).remove(emitter); // Remove emitter if it fails
            }
        }
    }
    public void cleanEmitters(String type) {
        for (SseEmitter emitter : emitters.get(type)) {
            try{
            if (emitter != null) {
                emitter.complete();
                }
            }
            catch (IllegalStateException e) {
                emitters.get(type).remove(emitter);
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