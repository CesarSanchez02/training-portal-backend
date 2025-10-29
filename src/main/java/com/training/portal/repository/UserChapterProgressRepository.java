package com.training.portal.repository;

import com.training.portal.model.User;
import com.training.portal.model.Chapter;
import com.training.portal.model.Course;
import com.training.portal.model.UserChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChapterProgressRepository extends JpaRepository<UserChapterProgress, Long> {
    boolean existsByUserAndChapter(User user, Chapter chapter);
    long countByUserAndChapter_Course(User user, Course course);
    List<UserChapterProgress> findByUser(User user);
}
