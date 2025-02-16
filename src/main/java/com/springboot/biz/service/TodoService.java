package com.springboot.biz.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.springboot.biz.domain.Todo;
import com.springboot.biz.dto.PageRequestDTO;
import com.springboot.biz.dto.PageResponseDTO;
import com.springboot.biz.dto.TodoDto;
import com.springboot.biz.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    private final ModelMapper modelMapper;

    public long register(TodoDto todoDto){
        Todo todo = modelMapper.map(todoDto, Todo.class);

        Todo savedTodo = todoRepository.save(todo);

        return savedTodo.getTno();
    }

    public TodoDto get(long tno){
        Optional<Todo> result = todoRepository.findById(tno);

        Todo todo = result.orElseThrow();

        TodoDto dto = modelMapper.map(todo, TodoDto.class);
        return dto;
    }

    public void modify(TodoDto todoDto){
        Optional<Todo> result = todoRepository.findById(todoDto.getTno());

        Todo todo = result.orElseThrow();

        todo.setTitle(todoDto.getTitle());
        todo.setWriter(todoDto.getWriter());
        todo.setComplete(todoDto.isComplete());

        todoRepository.save(todo);
    }

    public void remove(long tno){
        todoRepository.deleteById(tno);
    }

    public PageResponseDTO<TodoDto> list(PageRequestDTO pageRequestDTO){

        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1 ,
                pageRequestDTO.getSize(),
                Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);

        List<TodoDto> dtoList = result.getContent().stream()
                .map(todo -> modelMapper.map(todo, TodoDto.class))
                .collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<TodoDto> responseDTO =
                PageResponseDTO.<TodoDto>withAll()
                        .dtoList(dtoList)
                        .pageRequestDTO(pageRequestDTO)
                        .totalCount(totalCount)
                        .build();
        return responseDTO;
    }
}
