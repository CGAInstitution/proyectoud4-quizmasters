package madstodolist.restcontroller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@RestController
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SseEmitter> emittersPasarPregunta = new CopyOnWriteArrayList<>();

    @GetMapping("/sse/start")
    @Async
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Keeps connection open
        emitters.add(emitter);

        // Remove emitter when completed
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    @GetMapping("/sse/pasarPregunta")
    @Async
    public SseEmitter streamPasarPregunta() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Keeps connection open
        emittersPasarPregunta.add(emitter);

        // Remove emitter when completed
        emitter.onCompletion(() -> emittersPasarPregunta.remove(emitter));
        emitter.onTimeout(() -> emittersPasarPregunta.remove(emitter));

        return emitter;
    }

    public void sendUpdate(String message) {
        for (SseEmitter emitter : emitters) {
            try {
                if (emitter != null) {
                    emitter.send(SseEmitter.event().data(message));
                }
            } catch (IOException | IllegalStateException e) {
                emitter.complete();
                emitters.remove(emitter); // Remove emitter if it fails
            }
        }
    }

    public void cleanEmitters() {
        for (SseEmitter emitter : emitters) {
            if (emitter != null) {
                emitter.complete();
            }
        }
    }

    // Method to send updates to all active emitters
    public void sendUpdatePreguntas(String message) {
        for (SseEmitter emitter : emittersPasarPregunta) {
            try {
                if (emitter != null) {
                    emitter.send(SseEmitter.event().data(message));
                }
            } catch (IOException | IllegalStateException e) {
                emitter.complete();
                emittersPasarPregunta.remove(emitter); // Remove emitter if it fails
            }
        }
    }

    public void cleanEmittersPreguntas() {
        for (SseEmitter emitter : emittersPasarPregunta) {
            if (emitter != null) {
                emitter.complete();
            }
        }
    }
}