package madstodolist.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse/start")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Keeps connection open
        emitters.add(emitter);

        // Remove emitter when completed
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    // Method to send updates to all active emitters
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
}