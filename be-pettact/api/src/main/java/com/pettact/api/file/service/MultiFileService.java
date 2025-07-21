package com.pettact.api.file.service;


import com.pettact.api.file.FileUtils;
import com.pettact.api.file.dto.FileDto;
import com.pettact.api.file.dto.UpdateFileDto;
import com.pettact.api.file.entity.File;
import com.pettact.api.file.repository.MultiFileRepository;
import com.pettact.api.multiFile.dto.FileUpdateDto;
import com.pettact.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MultiFileService {

    @Autowired
    private MultiFileRepository multiFileRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload.path:/tmp/uploads}")
    private String uploadPath;

    /**
     * 파일 업로드 (단일/다중 파일 모두 처리)
     */
    public List<FileDto> createFiles(File.ReferenceTable referenceTable,
                                     Long referenceNo,
                                     MultipartFile[] files,
                                     Long userNo) {
        // 사용자 검증
        userRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<FileDto> results = new ArrayList<>();

        for (MultipartFile file : files) {
            FileDto result = processFile(referenceTable, referenceNo, file, userNo);
            results.add(result);
        }

        return results;
    }

    /**
     * 파일 정보 조회
     */
    @Transactional(readOnly = true)
    public FileDto getFile(Long fileNo) {
        File file = multiFileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다."));
        return FileDto.fromEntity(file);
    }

    /**
     * 특정 참조의 모든 파일 조회
     */
    @Transactional(readOnly = true)
    public List<FileDto> getFilesByReference(File.ReferenceTable referenceTable, Long referenceNo) {
        List<File> files = multiFileRepository.findFilesByReference(
                referenceTable, referenceNo);
        return files.stream()
                .map(FileDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 파일 수정 (파일 교체 또는 메타데이터 수정)
     */
    public FileDto updateFile(Long fileNo, MultipartFile newFile, UpdateFileDto updateFileDto, Long userNo) {
        File existingFile = multiFileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다."));

        // 권한 검증
        if (!existingFile.getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("파일 수정 권한이 없습니다.");
        }

        // 파일 교체
        if (newFile != null && !newFile.isEmpty()) {
            validateFile(newFile);

            // 기존 물리적 파일 삭제
            deletePhysicalFile(existingFile.getStoredFileName());

            // 새 파일 저장
            String newStoredFileName = store(newFile);

            // 파일 정보 업데이트
            existingFile.setStoredFileName(newStoredFileName);
            existingFile.setFilePath(uploadPath + "/" + newStoredFileName);
            existingFile.setImageURL(newStoredFileName);
            existingFile.setFileName(newFile.getOriginalFilename());
            existingFile.setFileSize((int) newFile.getSize());
            existingFile.setFileMimeType(newFile.getContentType());
        }

        // 메타데이터 수정
        if (updateFileDto != null) {
            existingFile.patch(updateFileDto);
        }

        // 둘 다 없으면 에러
        if ((newFile == null || newFile.isEmpty()) && updateFileDto == null) {
            throw new IllegalArgumentException("수정할 데이터가 없습니다.");
        }

        File saved = multiFileRepository.save(existingFile);
        return FileDto.fromEntity(saved);
    }

    /**
     * 파일 삭제
     */
    public void delete(Long fileNo, Long userNo) {
        File file = multiFileRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다."));

        // 권한 검증
        if (!file.getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("파일 삭제 권한이 없습니다.");
        }

        // 물리적 파일 삭제
        deletePhysicalFile(file.getStoredFileName());

        // DB에서 삭제
        multiFileRepository.deleteById(fileNo);
        log.info("파일 삭제 완료: fileNo={}", fileNo);
    }

    /**
     * 개별 파일 처리
     */
    private FileDto processFile(File.ReferenceTable referenceTable,
                                        Long referenceNo,
                                        MultipartFile file,
                                        Long userNo) {
        validateFile(file);

        String storedFileName = store(file);
        int nextOrder = getNextFileOrder(referenceTable, referenceNo);

        File multiFile = File.builder()
                .referenceTable(referenceTable)
                .referenceNo(referenceNo)
                .userNo(userNo)
                .storedFileName(storedFileName)
                .filePath(uploadPath + "/" + storedFileName)
                .imageURL(storedFileName)
                .fileName(file.getOriginalFilename())
                .fileSize((int) file.getSize())
                .fileMimeType(file.getContentType())
                .fileOrderNo(nextOrder)
                .build();

        File saved = multiFileRepository.save(multiFile);
        log.info("파일 업로드 성공: fileId={}, originalName={}", saved.getFileNo(), saved.getFileName());

        return FileDto.fromEntity(saved);
    }

    /**
     * 파일 검증
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String mimeType = file.getContentType();
        if (FileUtils.isImageFile(mimeType)) {
            if (file.getSize() > 31 * 1024 * 1024) { // 10MB
                throw new IllegalArgumentException("이미지 파일 크기는 31MB를 초과할 수 없습니다.");
            }
        } else {
            if (file.getSize() > 50 * 1024 * 1024) { // 50MB
                throw new IllegalArgumentException("첨부파일 크기는 50MB를 초과할 수 없습니다.");
            }
        }
    }

    private String store(MultipartFile file) {
        try {
            Path uploadDir = Paths.get(uploadPath);
            log.info("업로드 디렉토리: {}", uploadDir.toAbsolutePath()); // 추가

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("디렉토리 생성됨: {}", uploadDir.toAbsolutePath()); // 추가
            }

            String originalFilename = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String storedFileName = timestamp + "_" + originalFilename;

            Path filePath = uploadDir.resolve(storedFileName);
            log.info("파일 저장 경로: {}", filePath.toAbsolutePath()); // 추가

            file.transferTo(filePath.toFile());

            log.info("파일 저장 성공: {}", filePath.toAbsolutePath()); // 추가
            return storedFileName;
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장에 실패했습니다.");
        }
    }

    /**
     * 물리적 파일 삭제
     */
    private void deletePhysicalFile(String storedFileName) {
        try {
            Path filePath = Paths.get(uploadPath, storedFileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("물리적 파일 삭제 성공: {}", filePath);
            } else {
                log.warn("물리적 파일이 존재하지 않음: {}", filePath);
            }
        } catch (IOException e) {
            log.error("물리적 파일 삭제 실패: {}", storedFileName, e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 다음 파일 순서 계산
     */
    private int getNextFileOrder(File.ReferenceTable referenceTable, Long referenceNo) {
        Integer maxOrder = multiFileRepository.findMaxOrder(referenceTable, referenceNo);
        return (maxOrder == null) ? 1 : maxOrder + 1;
    }

}

