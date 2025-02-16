package com.springboot.biz.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.biz.dto.PageRequestDTO;
import com.springboot.biz.dto.PageResponseDTO;
import com.springboot.biz.dto.TodoDto;
import com.springboot.biz.service.TodoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    //상세보기
    @GetMapping("/{tno}")
    public TodoDto get(@PathVariable(name="tno")long tno){
        return todoService.get(tno);
    }
    //목록
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER', 'ROLE_ADMIN')")
    public PageResponseDTO<TodoDto> list(PageRequestDTO pageRequestDTO){
        log.info("list------:" + pageRequestDTO);
        return todoService.list(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(@RequestBody TodoDto todoDto){
        //System.out.println("TodoDTO : " + todoDto);

        Long tno = todoService.register(todoDto);
        return Map.of("tno", tno);
    }
    
    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable(name="tno")Long tno, @RequestBody TodoDto todoDto){
        todoDto.setTno(tno);
        
        todoService.modify(todoDto);
        return Map.of("RESULT : ", "성공");
    }
    
    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable(name="tno")Long tno){
        todoService.remove(tno);
        return Map.of("결과", "성공");
    }
}
