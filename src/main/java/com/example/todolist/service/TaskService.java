package com.example.todolist.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    public String testFlux() {

        Flux<String> flux = Flux.fromArray(new String[]{"Hello", "World", "From", "Flux"})
        .flatMap(x -> Mono.just(x.toUpperCase()))
        .doOnNext(System.out::println)
        .flatMap(x -> {
            Optional<String> optional = Optional.empty();
            return optional.map(Mono::just).orElse(Mono.empty());
        })
        .flatMap(x -> Mono.just(x.toLowerCase()))
        .doOnNext(System.out::println);

        flux.subscribe();
        return "done";
    }
}
