package com.example.just.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Repository
public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);
    void saveEventCache(String eventCacheId, Object Event);
    Map<String, SseEmitter> findAllEmitterStartWithById(String id);
    Map<String, Object> findAllEventCacheStartWithById(String id);
    void deleteById(String id);
    void deleteAllEmitterStartWithId(String id);
    void deleteAllEventCacheStartWithId(String id);
}
