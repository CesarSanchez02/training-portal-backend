package com.training.portal.service.impl;

import com.training.portal.model.Category;
import com.training.portal.repository.CategoryRepository;
import com.training.portal.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Long id, Category incoming) {
        Category current = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id " + id));

        if (incoming.getName() != null && !incoming.getName().equalsIgnoreCase(current.getName())) {
            if (categoryRepository.existsByNameIgnoreCaseAndIdNot(incoming.getName(), id)) {
                throw new RuntimeException("Ya existe una categoría con el nombre: " + incoming.getName());
            }
            current.setName(incoming.getName());
        }

        if (incoming.getDescription() != null) {
            current.setDescription(incoming.getDescription());
        }

        return categoryRepository.save(current);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
