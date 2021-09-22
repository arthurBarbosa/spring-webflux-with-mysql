package com.abcode.webflux.resources;

import com.abcode.webflux.entities.Todo;
import com.abcode.webflux.repositories.TodoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TodoResources {

    private final TodoRepository todoRepository;
    private final TransactionTemplate transactionTemplate;
    @Qualifier("jdbcScheduler")
    private final Scheduler jdbcScheduler;

    public TodoResources(TodoRepository todoRepository, TransactionTemplate transactionTemplate, Scheduler jdbcScheduler) {
        this.todoRepository = todoRepository;
        this.transactionTemplate = transactionTemplate;
        this.jdbcScheduler = jdbcScheduler;
    }

    @PostMapping
    public Mono<Todo> save(@RequestBody Todo todo) {
        Mono<Todo> op = Mono.fromCallable(() -> this.transactionTemplate.execute(action -> {
            final var save = this.todoRepository.save(todo);
            return save;
        }));
        return op;
    }

    @GetMapping("/{id}")
    public Mono<Optional<Todo>> findById(@PathVariable Long id){
        return Mono.just(this.todoRepository.findById(id));
    }
}
