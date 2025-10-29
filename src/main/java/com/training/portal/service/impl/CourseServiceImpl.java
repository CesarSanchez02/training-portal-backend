package com.training.portal.service.impl;

import com.training.portal.model.Course;
import com.training.portal.model.User;
import com.training.portal.repository.CourseRepository;
import com.training.portal.repository.CategoryRepository;
import com.training.portal.repository.UserRepository;
import com.training.portal.service.ICourseService;
import com.training.portal.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements ICourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> findByCategory(Long categoryId) {
        return courseRepository.findByCategory_Id(categoryId);
    }

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course createCourse(String title, String description, Long categoryId) {

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + categoryId));

        Course course = Course.builder()
                .title(title)
                .description(description)
                .category(category)
                .createdAt(LocalDate.now().atStartOfDay())
                .build();

        Course savedCourse = save(course);

        List<User> users = userRepository.findAll()
                .stream()
                .filter(u -> "ROLE_USER".equalsIgnoreCase(u.getRole()))
                .toList();

        for (User user : users) {
            CompletableFuture.runAsync(() -> {
                try {
                    emailService.sendNewCourseEmail(
                            user.getEmail(),
                            user.getFullName(),
                            savedCourse.getTitle(),
                            "http://localhost:3000/userDashboard"
                    );
                    System.out.println("Notificación enviada a: " + user.getEmail());

                } catch (Exception e) {
                    System.err.println("Error al enviar correo a " + user.getEmail() + ": " + e.getMessage());
                }
            });
        }

        return savedCourse;
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public Course update(Long id, Course incoming) {
        Course current = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con id " + id));

        if (incoming.getTitle() != null && !incoming.getTitle().equalsIgnoreCase(current.getTitle())) {
            if (courseRepository.existsByTitleIgnoreCaseAndIdNot(incoming.getTitle(), id)) {
                throw new RuntimeException("Ya existe un curso con el título: " + incoming.getTitle());
            }
            current.setTitle(incoming.getTitle());
        }

        if (incoming.getDescription() != null) {
            current.setDescription(incoming.getDescription());
        }

        if (incoming.getCategory() != null && incoming.getCategory().getId() != null) {
            var newCat = categoryRepository.findById(incoming.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id " + incoming.getCategory().getId()));
            current.setCategory(newCat);
        }

        return courseRepository.save(current);
    }

    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }
}
