package com.training.portal.service;

import com.training.portal.model.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    List<Category> findAll();
    Optional<Category> findByName(String name);
    Optional<Category> findById(Long id);
    Category save(Category category);
    Category update(Long id, Category incoming);
    void deleteById(Long id);
}

