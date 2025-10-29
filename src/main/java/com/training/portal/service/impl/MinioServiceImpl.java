package com.training.portal.service.impl;

import com.training.portal.service.IMinioService;
import io.minio.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class MinioServiceImpl implements IMinioService {

    private static final String MINIO_URL = "http://localhost:9000";
    private static final String ACCESS_KEY = "minioadmin";
    private static final String SECRET_KEY = "minioadmin";
    private static final String BUCKET_NAME = "training-bucket";

    private final MinioClient minioClient;

    public MinioServiceImpl() {
        this.minioClient = MinioClient.builder()
                .endpoint(MINIO_URL)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            String originalName = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + originalName;

            InputStream uploadStream;
            String contentType;

            if (originalName != null && originalName.toLowerCase().endsWith(".docx")) {
                ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
                convertWordToPdf(file.getInputStream(), pdfOutput);
                uploadStream = new ByteArrayInputStream(pdfOutput.toByteArray());
                fileName = fileName.replace(".docx", ".pdf");
                contentType = "application/pdf";
            } else {
                uploadStream = file.getInputStream();
                contentType = file.getContentType();
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(folder + "/" + fileName)
                            .stream(uploadStream, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );

            return String.format("%s/%s/%s/%s", MINIO_URL, BUCKET_NAME, folder, fileName);

        } catch (Exception e) {
            System.err.println("Error al subir el archivo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al subir el archivo a MinIO: " + e.getMessage(), e);
        }
    }

    private void convertWordToPdf(InputStream input, OutputStream output) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(input);
             PDDocument pdfDoc = new PDDocument()) {

            PDPage page = new PDPage();
            pdfDoc.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);

            float y = 750;

            for (var paragraph : docx.getParagraphs()) {
                String text = paragraph.getText();
                if (text == null || text.isBlank()) continue;

                text = text.replaceAll("[^\\x20-\\x7EáéíóúÁÉÍÓÚñÑüÜ]", "?");

                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText(text);
                contentStream.endText();

                y -= 15;
                if (y < 50) {
                    contentStream.close();
                    page = new PDPage();
                    pdfDoc.addPage(page);
                    contentStream = new PDPageContentStream(pdfDoc, page);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    y = 750;
                }
            }

            contentStream.close();
            pdfDoc.save(output);
        }
    }


}
