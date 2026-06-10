package com.example.todolist.controllers;

import com.example.todolist.service.UniqueKeyDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unique-key-demo")
@RequiredArgsConstructor
public class UniqueKeyDemoController {

    private final UniqueKeyDemoService service;

    // POST /unique-key-demo/seed?codes=A,B,C
    @PostMapping("/seed")
    public String seed(@RequestParam List<String> codes) {
        service.seed(codes);
        return "Seeded: " + codes;
    }

    // POST /unique-key-demo/broken?codes=A,B,C
    // Expect: DataIntegrityViolationException — duplicate entry
    @PostMapping("/broken")
    public String broken(@RequestParam List<String> codes) {
        service.deleteAndReinsertBroken(codes);
        return "Done (won't reach here — constraint will fire)";
    }

    // POST /unique-key-demo/fixed?codes=A,B,C
    // Expect: success — explicit flush orders DELETE before INSERT
    @PostMapping("/fixed")
    public String fixed(@RequestParam List<String> codes) {
        service.deleteAndReinsertFixed(codes);
        return "Done — no constraint violation";
    }
}
