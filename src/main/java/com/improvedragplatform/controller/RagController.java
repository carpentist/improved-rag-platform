package com.improvedragplatform.controller;

import com.improvedragplatform.service.RagServer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagServer ragServer;

    @PostMapping("/ask")
    public String ask(@RequestBody AskRequest request) {
        return ragServer.ask(request.question());
    }

    public record AskRequest(String question) {}
}
