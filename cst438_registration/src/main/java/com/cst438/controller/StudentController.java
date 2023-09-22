package com.cst438.controller;

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
	@Transactional
	public int createStudent(@RequestBody StudentDTO studentDTO) {
		System.out.println("/Creating new student");
		Student student = new Student();
		//student.setStudent_id(5);
		student.setName("Test Student");
		student.setEmail("anothertest@csumb.edu");
		student.setStatusCode(0);
		student.setStatus("Hold");
		studentRepository.save(student);
		StudentDTO newStudent = createStudent(student);
		
		return newStudent.id();
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
	@DeleteMapping("/student/{student_id}")
	@Transactional
	public void dropStudent(  @PathVariable int student_id, @RequestParam("force") boolean force  ) {
		System.out.println("/drop student id: " + student_id);
		String student_email = "test@csumb.edu";   // student's email 

		Student student = studentRepository.findByEmail(student_email);
		Enrollment enrollment = enrollmentRepository.findById(student_id).orElse(null);
		
		if (enrollment != null && enrollment.getStudent().getEmail().equals(student_email)) {
			System.out.println("WARNING: Student " + student_email + " has enrollment in one or more classes.");
			studentRepository.delete(student);
		}else if (enrollment == null){
			System.out.println("Student " + student_email + " has no enrollment.");
			studentRepository.delete(student);
		}else {
			studentRepository.delete(student);
		}
		
		/*
        if (force) {
            // Perform the deletion without any checks or warnings
            // ...
            return ResponseEntity.ok("Resource deleted.");
        } else {
            // Display a warning or perform some checks before deletion
            // ...
            return ResponseEntity.badRequest().body("Deletion canceled. Use 'force=true' to delete.");
        }
		*/	
	}
	
    @PutMapping("/student/{student_id}")
    public void updateStudent(@PathVariable int student_id /*, @RequestBody StudentDTO studentDTO*/) {
        System.out.println("/Updating Student status");
        Student student = studentRepository.findById(student_id).orElse(null);
        Student updateStudent = new Student();
        updateStudent.setStudent_id(student.getStudent_id());
        updateStudent.setName(student.getName());
        updateStudent.setEmail(student.getEmail());
        updateStudent.setStatus("Updated");
        updateStudent.setStatusCode(1);
        studentRepository.save(updateStudent);
        
    	// Perform the update operation using updatedResource
        // ...
        //return ResponseEntity.ok(updatedResource);
    }
	
	
	/*
	// list all students
	@GetMapping("/students")
	public StudentDTO[] getAllStudents(@RequestParam())
	*/
	@GetMapping("/students")
	@Transactional
	public StudentDTO[] getAllStudents(@RequestParam("semester") Optional<String> semester) {
		//StudentDTO[] allStudents = new 
		System.out.println("/student list called");
		
		List <Student> students = (List<Student>) studentRepository.findAll();
		StudentDTO[] stu = createStudents(students);
		return stu;
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
	
	
	

