package com.training.portal.service;

import com.training.portal.model.Course;

import java.util.List;
import java.util.Optional;

public interface ICourseService {
    List<Course> findAll();
    List<Course> findByCategory(Long categoryId);
    Course save(Course course);
    Course createCourse(String title, String description, Long categoryId);
    Course update(Long id, Course updatedCourse);
    void deleteById(Long id);
    Optional<Course> findById(Long id);



}
