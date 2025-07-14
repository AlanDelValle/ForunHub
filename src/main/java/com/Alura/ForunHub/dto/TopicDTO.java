package com.Alura.ForunHub.dto;

import com.Alura.ForunHub.model.Course;
import com.Alura.ForunHub.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicDTO {
    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotBlank(message = "A mensagem é obrigatória")
    private String message;

    @NotNull(message = "A data de criação é obrigatória")
    private LocalDateTime creationDate;

    @NotBlank(message = "O status é obrigatório")
    private String status;

    @NotNull(message = "O autor é obrigatório")
    private User author;

    @NotNull(message = "O curso é obrigatório")
    private Course course;
}