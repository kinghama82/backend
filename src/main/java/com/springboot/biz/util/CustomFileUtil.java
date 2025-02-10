package com.springboot.biz.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

@Component   //특정기능이 없는 객체를 만들어주는 어노테이션
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil { //파일 데이터의 입출력 담당

    @Value("${org.zerock.upload.path}") //프로퍼타이즈에 설정된 경로를 불러옴
    private String uploadPath;

    @PostConstruct    //의존성주입 먼저 수행하고 클래스 객체가 만들어 질 때 사용하는 어노테이션
    //생성자가 호출되었을때 빈은 초기화 되지 않았을 수도 있음. 이때 postConstruct를 사용하면 의존성주입 후 메소드 실행
    //어플리케이션이 실행될때 한번만 실행
    public void init(){
        //지정한 경로에 파일을 생성해줌
        File temFolder = new File(uploadPath);

        //파일이 존재하는지 검사하는 메소드 exists()
        if(temFolder.exists() == false){
            //파일이 없으면 생성해주는 메소드 mkdirs() -> 파일생성하고 true반환
            temFolder.mkdirs();
        }
        //파일이 존재하면 해당 경로 String으로 반환
        uploadPath = temFolder.getAbsolutePath();

        log.info("----------------------");
        log.info(uploadPath);
    }

    //파일 저장
    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException{
        if(files == null || files.size() == 0){
            return List.of();
        }
        List<String> uploadNames = new ArrayList<>();

        //다른 값과 중복되지 않는 고유값을 생성하는 난수 생성 uuid
        //실제로 중복값이 발생 할 수 있으나 확률이 매우 희박함
        for(MultipartFile multipartFile : files){
            String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();

            // MultipartFile 인터페이스
            // getName() : 넘어온 파라미터 명
            // getOriginalFilename() : 업로드 파일명
            // getContentType() : 파일의 컨텐츠 타입
            // isEmpty() : 파일이 비어있는지 확인
            // getsize() : 파일의 크기
            // getbytes() : 바이트 배열로 저장된 파일의 내용
            // getInputStream() : 파일의 내용을 읽기 위한 inputstream 반환
            // transferTo() : 파일 저장

            // 절대경로 + 파일이름 을 합쳐서 경로를 만들어줌
            Path savePath = Paths.get(uploadPath, savedName);

            try {
                //multipartfile 입력 스트림으로 읽어와서 savepath에 복사
                //업로드한 파일을 저장 경로에 복사해서 저장
                Files.copy(multipartFile.getInputStream(), savePath);

                //파일의 타입을 확인해서 이미지 파일이면 썸네일 생성
                String contentType = multipartFile.getContentType();
                if(contentType != null && contentType.startsWith("image")){
                    //썸네일 파일명 생성
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                    //썸네일 생성할 이미지 파일을 읽어와서 200*200 크기로 변경 후 저장
                    Thumbnails.of(savePath.toFile())
                            .size(200, 200)
                            .toFile(thumbnailPath.toFile());
                }

                uploadNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return uploadNames;
    }

    //파일 조회
    //resource 스프링 프레임워크에서 제공하는 추상화된 리소스 인터페이스, 다양한 유형의 리소스를 표현할 수 있다
    public ResponseEntity<Resource> getFile(String fileName){
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

        // Resource extends InputStreamSource
        // getinputStream()
        // exists() : 리소스가 존재하는지 확인
        // isReadable() : 리소스가 읽을 수 있는지 확인
        // isFile() : 리소스가 파일인지 확인
        // isOpen() : 리소스가 열려있는지 확인
        // getDescription() : 전체 경로 포함한 파일 이름 또는 실제 URL


        if(! resource.isReadable()){
            //uploadPath + File.separaator 
            resource = new FileSystemResource(uploadPath + File.separator + "123.jpg");
        }

        //httpheaders를 이용해서 해당 파일의 mime 유형을 추가(파일의 확장자에 따라서 파일의 유형을 결정)
        //headers를 통해서 mime유형을 추가해 주지 않으면 클라이언트가 서버로부터 전송되는 데이터의 형식을
        //정확히 인식할 수 없음 -> 파일이나 이미지가 깨져서 나올 수 있다
        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch (Exception e) {
            //응용 프로그램이나 서버 내부에서 에러가 발생했을때 발생하는 응답코드를 만들어 줌
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    public void deleteFiles(List<String> fileNames){
        if(fileNames == null || fileNames.size() == 0){
            return;
        }

       fileNames.forEach(fileName -> {
            String thumbnailFileName = "s_" + fileName;
            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath, fileName);

            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(thumbnailPath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
       });
    }

}
