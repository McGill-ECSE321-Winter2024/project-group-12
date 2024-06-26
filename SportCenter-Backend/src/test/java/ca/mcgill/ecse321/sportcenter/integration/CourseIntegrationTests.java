package ca.mcgill.ecse321.sportcenter.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.AfterTestClass;

import ca.mcgill.ecse321.sportcenter.dto.CourseListDTO;
import ca.mcgill.ecse321.sportcenter.dto.CourseRequestDTO;
import ca.mcgill.ecse321.sportcenter.dto.CourseResponseDTO;
import ca.mcgill.ecse321.sportcenter.dto.LoginRequestDTO;
import ca.mcgill.ecse321.sportcenter.dto.LoginResponseDTO;
import ca.mcgill.ecse321.sportcenter.model.Course;
import ca.mcgill.ecse321.sportcenter.repository.BillingAccountRepository;
import ca.mcgill.ecse321.sportcenter.repository.CourseRepository;
import ca.mcgill.ecse321.sportcenter.repository.RegistrationRepository;
import ca.mcgill.ecse321.sportcenter.repository.SessionRepository;
import ca.mcgill.ecse321.sportcenter.repository.SportCenterRepository;
import ca.mcgill.ecse321.sportcenter.service.AccountService;
import ca.mcgill.ecse321.sportcenter.service.CourseService;
import ca.mcgill.ecse321.sportcenter.service.SportCenterManagementService;


/*
* <p> Integration testing for the Course use cases with the controller. <p>
* <p> Service class in charge of managing courses. It implements following use cases: </p>
* <p> Create and update a course</p>
* <p> Getting courses according to their difficuly and their status.</p>
* <p> Propose new type of courses </p>
* <p> Approve a new course </p>
* <p> Disapprove a pending course </p>
* <p> Close an approved ourse </p>
* <p> User views course detail </p>
* @author Sahar
*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CourseIntegrationTests extends CommonTestSetup{
    @Autowired
    private TestRestTemplate client;

    @Autowired
    AccountService accountService;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseRepository courseRepo;

    @Autowired
    BillingAccountRepository billingAccountRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    SportCenterRepository sportCenterRepo;

    @Autowired
    SportCenterManagementService sportCenterService;

    private String LOGIN_EMAIL = "julia@mail.com";
    private String LOGIN_PASSWORD = "secret1456165";

    private static final List<Course> COURSES = new ArrayList<>();

    // helper function
    public void clearCourses() {
        sportCenterRepo.deleteAll();
        courseRepo.deleteAll();

        // Save one account in the system
        Time openingTime = Time.valueOf("6:0:0");
        Time closingTime = Time.valueOf("23:59:0");
        sportCenterService.createSportCenter("Fithub", openingTime, closingTime, "16", "sportcenter@mail.com", "455-645-4566");
        
        accountService.createCustomerAccount(LOGIN_EMAIL, LOGIN_PASSWORD, "Julia", "Doritos.png", "");
        
        // Login into that account
        LoginRequestDTO request = new LoginRequestDTO(LOGIN_EMAIL, LOGIN_PASSWORD);
        ResponseEntity<LoginResponseDTO> response = client.postForEntity("/login", request, LoginResponseDTO.class);
    }

    @Test
    @Order(0)
    public void login() {
        // Save one account in the system
        Time openingTime = Time.valueOf("6:0:0");
        Time closingTime = Time.valueOf("23:59:0");
        sportCenterService.createSportCenter("Fithub", openingTime, closingTime, "16", "sportcenter@mail.com", "455-645-4566");
        
        accountService.createCustomerAccount(LOGIN_EMAIL, LOGIN_PASSWORD, "Julia", "Doritos.png", "");
        
        // Login into that account
        LoginRequestDTO request = new LoginRequestDTO(LOGIN_EMAIL, LOGIN_PASSWORD);
        ResponseEntity<LoginResponseDTO> response = client.postForEntity("/login", request, LoginResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    //Testing the non-valid requests where the elements don't exist

    @Test
    @Order(1)
    public void testFindAllCoursesEmpty() {
        clearCourses();
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseListDTO> response = client.exchange("/courses", HttpMethod.GET, requestEntity, CourseListDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty
    }

    @Test
    @Order(2)
    public void testFindAllCoursesByDifficultyEmpty() {
        clearCourses();
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseListDTO> response = client.exchange("/courses?difficulty=Intermediate", HttpMethod.GET, requestEntity, CourseListDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty
    }

    @Test
    @Order(3)
    public void testFindAllCoursesByStatusEmpty() {
        clearCourses();
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseListDTO> response = client.exchange("/courses?status=Approved", HttpMethod.GET, requestEntity, CourseListDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty
    }

    
    @Test
    @Order(4)
    public void testFindCourseByNameEmpty() {
        clearCourses();
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseResponseDTO> response = client.exchange("/courses?name=baba", HttpMethod.GET, requestEntity, CourseResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty
    }

    @Test
    @Order(5)
    public void testFindCourseByIdEmpty() {
        clearCourses();
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseResponseDTO> response = client.exchange("/courses/4", HttpMethod.GET, requestEntity, CourseResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty
    }

    //Testing the valid tests which should be returning content

    @Test
    @Order(6)
    public void testCreateCourse() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);

        CourseRequestDTO courseRequest = new CourseRequestDTO();
        courseRequest.setName("Test Course");
        courseRequest.setDescription("Test Description");
        courseRequest.setDifficulty(CourseRequestDTO.Difficulty.Beginner.toString());
        courseRequest.setStatus(CourseRequestDTO.Status.Closed.toString());
        courseRequest.setPricePerHour(1);

        HttpEntity<CourseRequestDTO> requestEntity = new HttpEntity<>(courseRequest, headers);

        // When
        ResponseEntity<CourseResponseDTO> response = client.exchange("/courses", HttpMethod.POST, requestEntity, CourseResponseDTO.class);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        CourseResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals("test course", responseBody.getName());
        assertEquals("Test Description", responseBody.getDescription());
        assertEquals("Beginner", responseBody.getDifficulty().toString());
        assertEquals("Closed", responseBody.getStatus().toString());
        // Add assertions for other fields as needed
    }

    @Test
    @Order(7)
    public void testUpdateValidCourse() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);

        Course course3 = courseService.createCourse("Course 3", "Description 3", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1, "none","none");

        // Now, update the created course
        CourseResponseDTO updatedCourseRequest = new CourseResponseDTO(course3);
        updatedCourseRequest.setName("Updated Course Name");
        updatedCourseRequest.setDescription("Updated Course Description");
        updatedCourseRequest.setDifficulty(CourseRequestDTO.Difficulty.Advanced.toString());
        updatedCourseRequest.setStatus(CourseRequestDTO.Status.Approved.toString());
    
        // Set up authentication for the update request
        HttpEntity<CourseResponseDTO> updateRequestEntity = new HttpEntity<>(updatedCourseRequest, headers);
    
        // Perform the update request
        ResponseEntity<CourseResponseDTO> updateResponse = client.exchange("/courses/" + course3.getId(), HttpMethod.PUT, updateRequestEntity, CourseResponseDTO.class);
    
        // Verify response
        assertEquals(HttpStatus.ACCEPTED, updateResponse.getStatusCode());
        CourseResponseDTO updatedCourse = updateResponse.getBody();
        assertNotNull(updatedCourse);
        assertEquals("updated course name", updatedCourse.getName());
        // Add assertions for other fields as needed
        
    }


    @Test
    @Order(8)
    public void testFindAllCourses() {
        clearCourses();
        
        // Create sample courses
        Course course1 = courseService.createCourse("Course 1", "Description 1", Course.Difficulty.Beginner.toString(), Course.Status.Approved.toString(), 1, "none","none");
        Course course2 = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1,  "none","none");
        Course course3 = courseService.createCourse("Course 3", "Description 3", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1, "none","none");
        
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseListDTO> response = client.exchange("/courses", HttpMethod.GET, requestEntity, CourseListDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should return OK, as there are courses in the
    }

    @Test
    @Order(9)
    public void testFindCourseByName() {
        clearCourses();
        // Create sample courses
        Course course1 = courseService.createCourse("Course 1", "Description 1", Course.Difficulty.Beginner.toString(), Course.Status.Approved.toString(), 1,  "none","none");
        
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<CourseResponseDTO> response = client.exchange("/courses?name=Course 1", HttpMethod.GET, requestEntity, CourseResponseDTO.class);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode()); // Should return FOUND, as there is a course with the given name
        assertEquals("course 1", response.getBody().getName()); // Make additional assertions as needed
    }
    

    @Test
    @Order(10)
    public void testFindAllCoursesDifficulty() {
        clearCourses();
        // Create sample courses
        Course course1 = courseService.createCourse("Course 1", "Description 1", Course.Difficulty.Beginner.toString(), Course.Status.Approved.toString(), 1, "none","none");
        Course course2 = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1, "none","none");

        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<CourseListDTO> response = client.exchange("/courses?difficulty=Intermediate", HttpMethod.GET, requestEntity, CourseListDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should return OK, as there are courses in the database
        assertFalse(response.getBody().getCourses() == null); // Ensure that courses are returned
        assertEquals(CourseResponseDTO.Difficulty.Intermediate.toString(), response.getBody().getCourses().get(0).getDifficulty());
    }

    @Test
    @Order(11)
    public void testFindAllCoursesStatus() {
        clearCourses();
        // Create sample courses
        Course course1 = courseService.createCourse("Course 1", "Description 1", Course.Difficulty.Beginner.toString(), Course.Status.Approved.toString(), 1, "none","none");
        Course course2 = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1, "none","none");

        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<CourseListDTO> response = client.exchange("/courses?status=Approved", HttpMethod.GET, requestEntity, CourseListDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should return OK, as there are courses in the database
        assertFalse(response.getBody().getCourses() == null); // Ensure that courses are returned
        assertEquals(CourseResponseDTO.Status.Approved.toString(), response.getBody().getCourses().get(0).getStatus());
        assertEquals(response.getBody().getCourses().size(), 2);
    }


    //Tests for approving, disapproving and closing
    @Test
    @Order(12)
    public void testApproveCourse() {
        clearCourses();
        // Create sample course
        Course course = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Pending.toString(), 1, "none","none");
        CourseRequestDTO courseReq = new CourseRequestDTO(course); 
    
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
    
        // Include the value parameter in the URL
        String url = String.format("/course-approval/%d?value=14", course.getId());
    
        // Create the request entity
        HttpEntity<CourseRequestDTO> requestEntity = new HttpEntity<>(courseReq, headers);
    
        // Act
        ResponseEntity<CourseResponseDTO> response = client.exchange(url, HttpMethod.PUT, requestEntity, CourseResponseDTO.class);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode()); // Should return ACCEPTED
        assertEquals(Course.Status.Approved.toString(), response.getBody().getStatus()); // Make additional assertions as needed
    }
    
    @Test
    @Order(13)
    public void testDisapproveCourse() {
        clearCourses();
        // Create sample course
        Course course = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Pending.toString(), 1,  "none","none");
        CourseRequestDTO courseReq = new CourseRequestDTO(course); 

        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<CourseRequestDTO> requestEntity = new HttpEntity<>(courseReq, headers);

        // Act
        ResponseEntity<CourseResponseDTO> response = client.exchange("/course-disapproval/" + course.getId(), HttpMethod.PUT, requestEntity, CourseResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode()); // Should return ACCEPTED
        assertEquals(Course.Status.Disapproved.toString(), response.getBody().getStatus()); // Make additional assertions as needed
    }

    @Test
    @Order(14)
    public void testCloseCourse() {
        clearCourses();
        // Create sample course
        Course course = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1,  "none","none");
        CourseRequestDTO courseReq = new CourseRequestDTO(course); 

        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<CourseRequestDTO> requestEntity = new HttpEntity<>(courseReq, headers);

        // Act
        ResponseEntity<CourseResponseDTO> response = client.exchange("/course-closing/" + course.getId(), HttpMethod.PUT, requestEntity, CourseResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode()); // Should return ACCEPTED
        assertEquals(Course.Status.Closed.toString(), response.getBody().getStatus()); // Make additional assertions as needed
    }

    @Test
    @Order(15)
    public void testDeleteValidCourse() {
        clearCourses();
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        Course course = courseService.createCourse("Course 2", "Description 2", Course.Difficulty.Intermediate.toString(), Course.Status.Approved.toString(), 1, "none","none");
        assertNotNull(course);
        // Act
        ResponseEntity<String> response = client.exchange("/courses/" + course.getId(), HttpMethod.DELETE, requestEntity, String.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<CourseResponseDTO> responseRead = client.exchange("/courses/"+course.getId(), HttpMethod.GET, requestEntity, CourseResponseDTO.class);

        // Assert
        assertNotNull(responseRead);
        assertEquals(HttpStatus.NO_CONTENT, responseRead.getStatusCode()); // Should be empty
    }

}
