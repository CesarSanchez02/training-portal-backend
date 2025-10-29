package com.training.portal.controller;

import com.training.portal.service.IProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final IProgressService progressService;

    @PostMapping("/complete/{chapterId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> completeChapter(
            @PathVariable Long chapterId,
            @RequestParam Long userId
    ) {
        progressService.markChapterAsCompleted(userId, chapterId);

        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Capítulo marcado como completado."
        ));
    }
}
