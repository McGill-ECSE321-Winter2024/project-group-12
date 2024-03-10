package ca.mcgill.ecse321.sportcenter.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.sportcenter.model.Course;
import ca.mcgill.ecse321.sportcenter.model.Course.Difficulty;
import ca.mcgill.ecse321.sportcenter.model.Course.Status;
import ca.mcgill.ecse321.sportcenter.model.Customer;
import ca.mcgill.ecse321.sportcenter.repository.CourseRepository;

/*
* <p>Service class in charge of managing courses. It implements following use cases: </p>
* <p>Create, update, delete a course</p>
* @author Sahar
*/
@Service
public class CourseService {
    @Autowired
	CourseRepository courseRepository;

    //--------------------------// Create Course //--------------------------//
    @Transactional
    public Course createCourse(String name, String description, Difficulty diff, Status status) {
        // Accumulate error messages
        StringBuilder errorMessage = new StringBuilder();
    
        // Input validation checks
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Course name cannot be empty! ");
        }
        if (description == null || description.trim().isEmpty()) {
            errorMessage.append("Course description cannot be empty! ");
        }
        if (diff == null) {
            errorMessage.append("Course difficulty cannot be null! ");
        }
        if (status == null) {
            errorMessage.append("Course status cannot be null! ");
        }
        // Ensure the uniqueness of each course
        if (courseRepository.existsByName(name)) {
            errorMessage.append("Course already exists! ");
        }
    
        // If there are any errors, throw an exception
        if (errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString().trim());
        }
    
        // If no errors, create and save the course
        Course course = new Course();
        course.setName(name);
        course.setDescription(description);
        course.setDifficulty(diff);
        course.setStatus(status);
        courseRepository.save(course);
        return course;
    }    
    
    //--------------------------// Update Course //--------------------------//

    @Transactional
    public Course updateCourse(Integer id, String name, String description, Difficulty diff, Status status) {
        // Accumulate error messages
        StringBuilder errorMessage = new StringBuilder();
    
        // Input validation checks
        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Course name cannot be empty! ");
        }
        if (description == null || description.trim().isEmpty()) {
            errorMessage.append("Course description cannot be empty! ");
        }
        if (diff == null) {
            errorMessage.append("Course difficulty cannot be null! ");
        }
        if (status == null) {
            errorMessage.append("Course status cannot be null! ");
        }
    
        // If there are any errors, throw an exception
        if (errorMessage.length() > 0) {
            throw new IllegalArgumentException(errorMessage.toString().trim());
        }
    
        // If no errors, create and save the course
        Course course = findCourseById(id);
        course.setName(name.toLowerCase());
        course.setDescription(description);
        course.setDifficulty(diff);
        course.setStatus(status);
        courseRepository.save(course);
        return course;
    }

    //--------------------------// Getters //--------------------------//  

    @Transactional 
    public Course findCourseById(Integer id){
        Course course = courseRepository.findCourseById(id);
        if (course == null){
            throw new IllegalArgumentException("There is no course with ID " + id +".");
        }
        return course;
    }

    @Transactional 
    public Course findCourseByName(String name){
        Course course = courseRepository.findCourseByName(name.toLowerCase());
        if (course == null){
            throw new IllegalArgumentException("There is no course with name " + name +".");
        }
        return course;
    }

    @Transactional
    public List<Course> getAllCourses() {
        return toList(courseRepository.findAll());
    }

    @Transactional
    public List<Course> findCoursesByDifficulty(Course.Difficulty difficulty){
        List<Course> coursesByDifficulty = new ArrayList<>();
        for (Course course : courseRepository.findAll()) {
            if (course.getDifficulty().equals(difficulty)){
                coursesByDifficulty.add(course);
            }
        }

        //If there are no courses of that difficulty existing, then stop the query. 
        if (coursesByDifficulty.size() == 0){
            throw new IllegalArgumentException("No courses of that difficulty exist!");
        }
        return coursesByDifficulty;
    }

    @Transactional
    public List<Course> findCoursesByStatus(Course.Status status){
        List<Course> coursesByStatus = new ArrayList<>();
        for (Course course : courseRepository.findAll()) {
            if (course.getStatus().equals(status)){
                coursesByStatus.add(course);
            }
        }
        //If there are no courses of that status existing, then stop the query. 
        if (coursesByStatus.size() == 0){
            throw new IllegalArgumentException("No courses of that status exist!");
        }
        return coursesByStatus;
    }
     
    //--------------------------// Helper functions //--------------------------//

    private <T> List<T> toList(Iterable<T> iterable){
		List<T> resultList = new ArrayList<T>();
		for (T t : iterable) {
			resultList.add(t);
		}
		return resultList;
	}

}
