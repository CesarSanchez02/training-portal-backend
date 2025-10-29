package com.training.portal.controller;

import com.training.portal.model.Course;
import com.training.portal.service.ICourseService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getAllCourses() {
        List<Course> courses = courseService.findAll();

        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpServletResponse.SC_NO_CONTENT)
                    .body(Map.of(
                            "status", 204,
                            "message", "No hay cursos registrados."
                    ));
        }

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Listado de cursos obtenido correctamente.",
                "data", courses
        ));
    }

    @GetMapping("/category/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getCoursesByCategory(@PathVariable Long id) {
        try {
            List<Course> courses = courseService.findByCategory(id);

            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpServletResponse.SC_NO_CONTENT)
                        .body(Map.of(
                                "status", 204,
                                "message", "No hay cursos disponibles para esta categoría."
                        ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Cursos obtenidos correctamente.",
                    "data", courses
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", 500,
                            "message", "Error al obtener los cursos por categoría.",
                            "error", e.getMessage()
                    ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            Course course = courseService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Curso obtenido correctamente.",
                    "data", course
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "message", "Error al obtener el curso.",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCourse(@RequestBody Map<String, Object> body) {
        try {
            String title = (String) body.get("title");
            String description = (String) body.get("description");
            Long categoryId = ((Number) body.get("categoryId")).longValue();

            Course created = courseService.createCourse(title, description, categoryId);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Curso creado correctamente.",
                    "data", created
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "message", "Error al crear el curso.",
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course incoming) {
        try {
            Course updated = courseService.update(id, incoming);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Curso actualizado correctamente.",
                    "data", updated
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "message", "Error al actualizar el curso.",
                    "error", e.getMessage()
            ));
        }
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Curso eliminado correctamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", "No se pudo eliminar el curso. Verifica que exista.",
                            "error", e.getMessage()
                    ));
        }
    }
}
