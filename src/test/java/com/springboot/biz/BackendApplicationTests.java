package com.springboot.biz;

import com.springboot.biz.domain.Todo;
import com.springboot.biz.dto.TodoDto;
import com.springboot.biz.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.springboot.biz.repository.TodoRepository;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private TodoService todoService;
	
	@Test
	void contextLoads() {
		
		/*
		 * for(int i = 1; i <= 100; i++) { Todo t = Todo.builder() .title("빌더제목 : " + i)
		 * .writer("빌더사용자") .dueDate(LocalDate.now()) .build(); todoRepository.save(t);
		 * }
		 */
		
		/*
		 * Todo todo = new Todo(); long tno = 1L; Optional<Todo> result =
		 * todoRepository.findById(tno); todo = result.get();
		 * todo.setDueDate(LocalDate.now()); todo.setTitle("수정제목");
		 * todo.setWriter("홍길동"); todoRepository.save(todo);
		 */
		
		/*
		 * long tno = 1L; todoRepository.deleteById(tno);
		 */

	}

}
