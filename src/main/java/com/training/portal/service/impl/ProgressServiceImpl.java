package com.training.portal.service.impl;

import com.training.portal.model.*;
import com.training.portal.repository.*;
import com.training.portal.service.IProgressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements IProgressService {

    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final UserChapterProgressRepository progressRepository;
    private final UserBadgeRepository badgeRepository;

    @Override
    @Transactional
    public void markChapterAsCompleted(Long userId, Long chapterId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Capítulo no encontrado"));

        if (progressRepository.existsByUserAndChapter(user, chapter)) {
            return;
        }

        UserChapterProgress progress = UserChapterProgress.builder()
                .user(user)
                .chapter(chapter)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        progressRepository.save(progress);

        checkCourseCompletion(user, chapter.getCourse());
    }

    private void checkCourseCompletion(User user, Course course) {
        long totalChapters = course.getChapters().size();
        long completed = progressRepository.countByUserAndChapter_Course(user, course);

        if (completed == totalChapters && totalChapters > 0) {
            if (!badgeRepository.existsByUserAndCourse(user, course)) {
                UserBadge badge = UserBadge.builder()
                        .user(user)
                        .course(course)
                        .badgeName("🏅 " + course.getTitle())
                        .badgeIcon("https://cdn-icons-png.flaticon.com/512/2583/2583346.png")
                        .earnedAt(LocalDateTime.now())
                        .build();
                badgeRepository.save(badge);
            }
        }
    }
}
