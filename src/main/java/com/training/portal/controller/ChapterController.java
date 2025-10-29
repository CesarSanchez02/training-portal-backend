package com.training.portal.controller;

import com.training.portal.model.Chapter;
import com.training.portal.service.IChapterService;
import com.training.portal.service.IMinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final IChapterService chapterService;
    private final IMinioService minioService;

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getByCourse(@PathVariable Long courseId) {
        List<Chapter> list = chapterService.findByCourse(courseId);
        return ResponseEntity.ok(Map.of("status", 200, "data", list));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Chapter chapter = chapterService.findById(id);

            if (chapter == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "status", 404,
                        "message", "Capítulo no encontrado"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "data", chapter
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "message", "Error al obtener el capítulo",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createChapter(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            String fileUrl = null;

            if (file != null && !file.isEmpty()) {
                long maxSize = 50 * 1024 * 1024; // 50 MB
                if (file.getSize() > maxSize) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", 400,
                            "message", "El archivo excede el tamaño máximo permitido (50 MB)."
                    ));
                }

                fileUrl = minioService.uploadFile(file, "chapters");
            }

            Chapter created = chapterService.createChapter(title, description, courseId, fileUrl);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Capítulo creado correctamente.",
                    "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "message", "Error al crear el capítulo.",
                    "error", e.getMessage()
            ));
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateChapter(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            String fileUrl = null;

            if (file != null && !file.isEmpty()) {
                long maxSize = 50 * 1024 * 1024; // 50 MB
                if (file.getSize() > maxSize) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "status", 400,
                            "message", "El archivo excede el tamaño máximo permitido (50 MB)."
                    ));
                }
                fileUrl = minioService.uploadFile(file, "chapters");
            }

            Chapter updated = new Chapter();
            updated.setTitle(title);
            updated.setDescription(description);
            updated.setFileUrl(fileUrl);

            Chapter result = chapterService.update(id, updated);

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Capítulo actualizado correctamente.",
                    "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", 500,
                    "message", "Error al actualizar el capítulo.",
                    "error", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteChapter(@PathVariable Long id) {
        chapterService.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Capítulo eliminado correctamente."
        ));
    }


}
