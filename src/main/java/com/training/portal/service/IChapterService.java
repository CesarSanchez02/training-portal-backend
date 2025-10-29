package com.training.portal.service;

import com.training.portal.model.Chapter;
import java.util.List;

public interface IChapterService {
    List<Chapter> findByCourse(Long courseId);

    List<Chapter> getByCourse(Long courseId);

    Chapter createChapter(String title, String description, Long courseId, String fileUrl);
    Chapter update(Long id, Chapter updatedChapter);
    void deleteById(Long id);
    Chapter findById(Long id);

}
