package com.Alura.ForunHub.repository;

import com.Alura.ForunHub.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTitleAndMessage(String title, String message);

    boolean existsByTitleAndMessageAndIdNot(String title, String message, Long id);

    @Query("SELECT t FROM Topic t WHERE t.course.name = :courseName AND YEAR(t.creationDate) = :year")
    List<Topic> findByCourseNameAndYear(String courseName, Integer year);
}