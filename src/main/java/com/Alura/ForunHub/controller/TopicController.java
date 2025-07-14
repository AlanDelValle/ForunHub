package com.Alura.ForunHub.controller;

import com.Alura.ForunHub.dto.TopicDTO;
import com.Alura.ForunHub.model.Topic;
import com.Alura.ForunHub.model.User;
import com.Alura.ForunHub.repository.TopicRepository;
import com.Alura.ForunHub.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    // Listagem de todos os tópicos com paginação e ordenação por creationDate
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<Topic>> listTopics(
            @PageableDefault(size = 10, sort = "creationDate", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Topic> topics = topicRepository.findAll(pageable);
        return ResponseEntity.ok(topics);
    }

    // Listagem de tópicos filtrados por nome do curso e ano
    @GetMapping("/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Topic>> listTopicsByCourseAndYear(
            @RequestParam String courseName,
            @RequestParam Integer year) {
        List<Topic> topics = topicRepository.findByCourseNameAndYear(courseName, year);
        return ResponseEntity.ok(topics);
    }

    // Detalhamento de um tópico por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido.");
        }
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico não encontrado."));
        return ResponseEntity.ok(topic);
    }

    // Cadastro de tópico
    @PostMapping
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Topic> createTopic(@RequestBody @Valid TopicDTO topicDTO, Authentication authentication) {
        boolean topicExists = topicRepository.existsByTitleAndMessage(topicDTO.getTitle(), topicDTO.getMessage());
        if (topicExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tópico com mesmo título e mensagem já existe.");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        Topic topic = new Topic();
        topic.setTitle(topicDTO.getTitle());
        topic.setMessage(topicDTO.getMessage());
        topic.setCreationDate(topicDTO.getCreationDate());
        topic.setStatus(topicDTO.getStatus());
        topic.setAuthor(user); // Associa o usuário autenticado
        topic.setCourse(topicDTO.getCourse());

        topicRepository.save(topic);
        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }

    // Atualização de tópico
    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody @Valid TopicDTO topicDTO) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido.");
        }

        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (!optionalTopic.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico não encontrado.");
        }

        Topic topic = optionalTopic.get();

        boolean topicExists = topicRepository.existsByTitleAndMessageAndIdNot(
                topicDTO.getTitle(), topicDTO.getMessage(), id);
        if (topicExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tópico com mesmo título e mensagem já existe.");
        }

        topic.setTitle(topicDTO.getTitle());
        topic.setMessage(topicDTO.getMessage());
        topic.setCreationDate(topicDTO.getCreationDate());
        topic.setStatus(topicDTO.getStatus());
        topic.setAuthor(topicDTO.getAuthor());
        topic.setCourse(topicDTO.getCourse());

        topicRepository.save(topic);
        return ResponseEntity.ok(topic);
    }

    // Exclusão de tópico
    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido.");
        }

        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (!optionalTopic.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tópico não encontrado.");
        }

        topicRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}