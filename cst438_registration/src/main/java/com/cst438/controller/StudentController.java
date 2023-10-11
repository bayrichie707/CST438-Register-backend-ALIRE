package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
@RestController 
@CrossOrigin
//@RequestMapping("/api/resources")



public class StudentController {

	@Autowired
	StudentRepository studentRepository;
	EnrollmentRepository enrollmentRepository;
		
	// create a new student and return the system generated student_id
	@PostMapping("/student")
	public int createStudent(@RequestBody StudentDTO sdto) {
		System.out.println("Called POST /student to add new student");
		Student check = studentRepository.findByEmail(sdto.email());
		if (check != null) {
			// error.  email exists.
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+sdto.email());
		}
		Student s = new Student();
		s.setEmail(sdto.email());
		s.setName(sdto.name());
		s.setStatusCode(sdto.statusCode());
		s.setStatus(sdto.status());
		studentRepository.save(s);
		// return the database generated student_id 
		return s.getStudent_id();
	}
	
	/*
	 * delete a student from the student table
	 */
	/*
	@DeleteMapping("/student/{student_id}")
	@Transactional
	public void dropStudent(  @PathVariable int student_id  ) {
		System.out.println("/drop student id: " + student_id);
		String student_email = "test@csumb.edu";   // student's email 

		Enrollment student = enrollmentRepository.findEnrollmentByStudentId(student_id);
		
		enrollmentRepository.delete(student);
	}
	*/
	@DeleteMapping("/student/{id}")
	public void deleteStudent(@PathVariable("id") int id, @RequestParam("force") Optional<String> force) {
		System.out.println("Called DELETE /student/{id} to delete student given ID");
		Student s = studentRepository.findById(id).orElse(null);
		if (s!=null) {
			// are there enrollments?
			List<Enrollment> list = enrollmentRepository.findByStudentId(id);
			if (list.size()>0 && force.isEmpty()) {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student has enrollments");
			} else {
				studentRepository.deleteById(id);
			}
		} else {
			// if student does not exist.  do nothing
			return;
		}
		
	}
	
	@PutMapping("/student/{id}") 
	public void updateStudent(@PathVariable("id")int id, @RequestBody StudentDTO sdto) {
		Student s = studentRepository.findById(id).orElse(null);
		if (s==null) {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "student not found "+id);
		}
		// has email been changed, check that new email does not exist in database
		if (!s.getEmail().equals(sdto.email())) {
		// update name, email.  new email must not exist in database
			Student check = studentRepository.findByEmail(sdto.email());
			if (check != null) {
				// error.  email exists.
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+sdto.email());
			}
		}
		s.setEmail(sdto.email());
		s.setName(sdto.name());
		s.setStatusCode(sdto.statusCode());
		s.setStatus(sdto.status());
		studentRepository.save(s);
	}
	
	
	/*
	// list all students
	@GetMapping("/students")
	public StudentDTO[] getAllStudents(@RequestParam())
	*/
	@GetMapping("/student")
	public StudentDTO[] getStudents() {
		System.out.println("Called GET /student to list all students");
		Iterable<Student> list = studentRepository.findAll();
		ArrayList<StudentDTO> alist = new ArrayList<>();
		for (Student s : list) {
			StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus());
			alist.add(sdto);
		}
		return alist.toArray(new StudentDTO[alist.size()]);
	}
	
	// Get student by ID
	@GetMapping("/student/{id}")
	public StudentDTO getStudent(@PathVariable("id") int id) {
		Student s = studentRepository.findById(id).orElse(null);
		if (s!=null) {
			StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus());
			return sdto;
		} else {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "student not found "+id);
		}
	}
	/* 
	 * helper method to transform course, enrollment, student entities into 
	 * a an instances of ScheduleDTO to return to front end.
	 * This makes the front end less dependent on the details of the database.
	 */
	private StudentDTO[] createStudents(/*int year, String semester, Student s,*/ List<Student> students) {
		StudentDTO[] result = new StudentDTO[students.size()];
		for (int i=0; i<students.size(); i++) {
			StudentDTO dto =createStudent(students.get(i));
			result[i] = dto;
		}
		return result;
	}
	
	private StudentDTO createStudent(Student e) {
		StudentDTO dto = new StudentDTO(
			e.getStudent_id(),
			e.getName(),
			e.getEmail(),
			e.getStatusCode(),
			e.getStatus());
		   
		return dto;
	}
}
	
	
	

