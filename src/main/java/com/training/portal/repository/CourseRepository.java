package com.training.portal.repository;

import com.training.portal.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategory_Id(Long categoryId);
    Optional<Course> findByTitle(String title);
    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);
}
