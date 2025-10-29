package com.training.portal.repository;

import com.training.portal.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByCourseId(Long courseId);
    boolean existsByTitleIgnoreCaseAndCourseId(String title, Long courseId);
    boolean existsByTitleIgnoreCaseAndCourseIdAndIdNot(String title, Long courseId, Long id);


}
