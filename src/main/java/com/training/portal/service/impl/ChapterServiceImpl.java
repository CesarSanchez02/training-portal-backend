package com.training.portal.service.impl;

import com.training.portal.model.Chapter;
import com.training.portal.model.Course;
import com.training.portal.repository.ChapterRepository;
import com.training.portal.repository.CourseRepository;
import com.training.portal.service.IChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements IChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<Chapter> findByCourse(Long courseId) {
        return chapterRepository.findByCourseId(courseId);
    }

    @Override
    public Chapter findById(Long id) {
        return chapterRepository.findById(id).orElse(null);
    }


    @Override
    public List<Chapter> getByCourse(Long courseId) {
        return chapterRepository.findByCourseId(courseId);
    }

    @Override
    public Chapter createChapter(String title, String description, Long courseId, String fileUrl) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + courseId));

        if (chapterRepository.existsByTitleIgnoreCaseAndCourseId(title, courseId)) {
            throw new RuntimeException("Ya existe un capítulo con el título '" + title + "' en este curso.");
        }

        Chapter chapter = Chapter.builder()
                .title(title)
                .description(description)
                .fileUrl(fileUrl)
                .createdAt(LocalDate.now().atStartOfDay())
                .course(course)
                .build();

        return chapterRepository.save(chapter);
    }

    @Override
    public Chapter update(Long id, Chapter updated) {
        Chapter current = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capítulo no encontrado con id " + id));

        if (updated.getTitle() != null
                && !updated.getTitle().equalsIgnoreCase(current.getTitle())
                && chapterRepository.existsByTitleIgnoreCaseAndCourseIdAndIdNot(
                updated.getTitle(),
                current.getCourse().getId(),
                id)) {
            throw new RuntimeException("Ya existe otro capítulo con el título '" + updated.getTitle() + "' en este curso.");
        }

        if (updated.getTitle() != null) current.setTitle(updated.getTitle());
        if (updated.getDescription() != null) current.setDescription(updated.getDescription());
        if (updated.getFileUrl() != null) current.setFileUrl(updated.getFileUrl());

        return chapterRepository.save(current);
    }

    @Override
    public void deleteById(Long id) {
        chapterRepository.deleteById(id);
    }
}
