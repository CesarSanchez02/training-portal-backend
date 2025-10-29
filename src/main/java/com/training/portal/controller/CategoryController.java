package com.training.portal.controller;

import com.training.portal.model.Category;
import com.training.portal.service.ICategoryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<Category> categories = categoryService.findAll();

        if (categories.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "No hay categorías registradas.",
                    "data", List.of()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Categorías obtenidas correctamente.",
                "data", categories
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(category -> ResponseEntity.ok(Map.of(
                        "status", 200,
                        "message", "Categoría obtenida correctamente.",
                        "data", category
                )))
                .orElseGet(() -> ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(Map.of(
                        "status", 404,
                        "message", "Categoría no encontrada con ID: " + id
                )));
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {

            if (categoryService.findByName(category.getName()).isPresent()) {
                return ResponseEntity.status(HttpServletResponse.SC_CONFLICT)
                        .body(Map.of(
                                "status", 409,
                                "message", "La categoría ya está registrada."
                        ));
            }

            Category newCategory = categoryService.save(category);

            return ResponseEntity.status(HttpServletResponse.SC_CREATED)
                    .body(Map.of(
                            "status", 201,
                            "message", "Categoría creada exitosamente.",
                            "data", newCategory
                    ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", 500,
                            "message", "Ocurrió un error al crear la categoría.",
                            "error", e.getMessage()
                    ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            Category updated = categoryService.update(id, category);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Categoría actualizada correctamente.",
                    "data", updated
            ));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Error en actualización de categoría";
            int status = msg.startsWith("Ya existe una categoría") ? HttpServletResponse.SC_CONFLICT : HttpServletResponse.SC_NOT_FOUND;
            return ResponseEntity.status(status).body(Map.of("status", status, "message", msg));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500, "message", "Error al actualizar la categoría.", "error", e.getMessage()
            ));
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Categoría eliminada correctamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(Map.of(
                            "status", 404,
                            "message", "No se pudo eliminar la categoría. Verifica que exista.",
                            "error", e.getMessage()
                    ));
        }
    }
}
