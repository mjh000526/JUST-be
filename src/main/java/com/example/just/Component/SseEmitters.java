package com.example.just.Component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitters {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void add(SseEmitter emitter) {
        this.emitters.add(emitter);
    }

    public void remove(SseEmitter emitter) {
        this.emitters.remove(emitter);
    }

    public void sendNotification() {
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data("Your post has been updated!"));
            } catch (IOException e) {
                this.emitters.remove(emitter);
            }
        }
    }
}