package com.pettact.api.file.controller;


import com.pettact.api.file.dto.FileDto;
import com.pettact.api.file.dto.UpdateFileDto;
import com.pettact.api.file.entity.File;
import com.pettact.api.file.service.MultiFileService;
import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.multiFile.service.FileService;
import com.pettact.api.security.vo.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/v1/multifile")
public class MultiFileController {

    @Autowired
    private MultiFileService multiFileService;

    /**
     * 파일 업로드 (단일/다중 파일 모두 처리)
     */
    @PostMapping
    public ResponseEntity<List<FileDto>> uploadFiles(
            @RequestParam("referenceTable") String referenceTableStr,
            @RequestParam("referenceNo") Long referenceNo,
            @RequestPart("files") MultipartFile[] files,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            File.ReferenceTable referenceTable = File.ReferenceTable.valueOf(referenceTableStr);
            Long userNo = userDetails.getUser().getUserNo();

            List<FileDto> results = multiFileService.createFiles(referenceTable, referenceNo, files, userNo);
            return ResponseEntity.status(HttpStatus.CREATED).body(results);
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * 파일 정보 조회
     */
    @GetMapping("/{fileNo}")
    public ResponseEntity<FileDto> getFile(@PathVariable Long fileNo) {
        FileDto responseDto = multiFileService.getFile(fileNo);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 특정 참조의 모든 파일 조회
     */
    @GetMapping
    public ResponseEntity<List<FileDto>> getFilesByReference(
            @RequestParam("referenceTable") String referenceTableStr,
            @RequestParam("referenceNo") Long referenceNo
    ) {
        File.ReferenceTable referenceTable = File.ReferenceTable.valueOf(referenceTableStr);
        List<FileDto> files = multiFileService.getFilesByReference(referenceTable, referenceNo);
        return ResponseEntity.ok(files);
    }

    /**
     * 파일 수정 (파일 교체 또는 메타데이터 수정)
     */
    @PatchMapping("/{fileNo}")
    public ResponseEntity<FileDto> updateFile(
            @PathVariable Long fileNo,
            @RequestPart(value = "file", required = false) MultipartFile newFile,
            @RequestPart(value = "data", required = false) UpdateFileDto updateFileDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userNo = userDetails.getUser().getUserNo();
        FileDto fileDto = multiFileService.updateFile(fileNo, newFile, updateFileDto, userNo);
        return ResponseEntity.ok(fileDto);
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/{fileNo}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long fileNo,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userNo = userDetails.getUser().getUserNo();
        multiFileService.delete(fileNo, userNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 이미지 파일 조회/다운로드
     */

    // MultiFileController (이미지 조회 API)에서
    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;

    @GetMapping("/image/{fileNo}")
    public ResponseEntity<Resource> getImage(@PathVariable Long fileNo) {
        try {
            log.info("=== 이미지 요청: fileNo={} ===", fileNo);

            FileDto fileDto = multiFileService.getFile(fileNo);
            log.info("DB 파일 경로: {}", fileDto.getFilePath());

            // DB에 저장된 전체 경로 사용
            Path filePath = Paths.get(fileDto.getFilePath());
            log.info("실제 파일 경로: {}", filePath.toAbsolutePath());
            log.info("파일 존재: {}", Files.exists(filePath));

            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileDto.getFileMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileDto.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("이미지 조회 실패", e);
            throw new RuntimeException("이미지 조회 실패");
        }
    }

    /**
     * 파일 다운로드
     */
    @GetMapping("/download/{fileNo}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileNo) {
        try {
            FileDto fileDto = multiFileService.getFile(fileNo);

            Path filePath = Paths.get(fileDto.getFilePath());
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new RuntimeException("파일을 읽을 수 없습니다.");
            }

            Resource resource = new UrlResource(filePath.toUri());

            String encodedFileName = URLEncoder.encode(fileDto.getFileName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + encodedFileName)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .body(resource);

        } catch (Exception e) {
            log.error("파일 다운로드 실패: fileNo={}", fileNo, e);
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.");
        }
    }
}
