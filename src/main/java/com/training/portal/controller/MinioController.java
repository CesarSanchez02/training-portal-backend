package com.training.portal.controller;

import com.training.portal.service.IMinioService;
import io.minio.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.ConnectException;
import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestController
@RequestMapping("/api/minio")
public class MinioController {


    private final IMinioService minioService;

    public MinioController(IMinioService minioService) {
        this.minioService = minioService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {

        try {

            if (file.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", 400,
                        "message", "El archivo excede el tamaño máximo permitido de 50 MB."
                ));
            }


            minioService.uploadFile(file, "courses");

            return ResponseEntity.ok(Map.of(
                    "status", 200,
                    "message", "Archivo subido correctamente.",
                    "data", Map.of(
                            "fileName", file.getOriginalFilename(),
                            "contentType", file.getContentType(),
                            "size", file.getSize()
                    )
            ));

        } catch (ErrorResponseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "message", "Error de MinIO: " + e.errorResponse().message()
            ));
        } catch (ConnectException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "status", 503,
                    "message", "No se pudo conectar al servidor MinIO. Verifica que esté en ejecución."
            ));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "status", 403,
                    "message", "No tienes permisos para subir archivos a MinIO."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", 500,
                    "message", "Ocurrió un error inesperado al subir el archivo: " + e.getMessage()
            ));
        }
    }
}
