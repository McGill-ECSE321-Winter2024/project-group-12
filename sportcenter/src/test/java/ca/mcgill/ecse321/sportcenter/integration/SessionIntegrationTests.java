package ca.mcgill.ecse321.sportcenter.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.sql.Time;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.AfterTestClass;

import ca.mcgill.ecse321.sportcenter.dto.AccountListDTO;
import ca.mcgill.ecse321.sportcenter.dto.AccountRequestDTO;
import ca.mcgill.ecse321.sportcenter.dto.CustomerResponseDTO;
import ca.mcgill.ecse321.sportcenter.dto.SessionListDTO;
import ca.mcgill.ecse321.sportcenter.dto.SessionRequestDTO;
import ca.mcgill.ecse321.sportcenter.dto.SessionResponseDTO;
import ca.mcgill.ecse321.sportcenter.model.Course;
import ca.mcgill.ecse321.sportcenter.model.Instructor;
import ca.mcgill.ecse321.sportcenter.model.Location;
import ca.mcgill.ecse321.sportcenter.model.Course.Difficulty;
import ca.mcgill.ecse321.sportcenter.model.Course.Status;
import ca.mcgill.ecse321.sportcenter.repository.CourseRepository;
import ca.mcgill.ecse321.sportcenter.repository.InstructorRepository;
import ca.mcgill.ecse321.sportcenter.repository.LocationRepository;
import ca.mcgill.ecse321.sportcenter.repository.SessionRepository;
import ca.mcgill.ecse321.sportcenter.repository.SportCenterRepository;
import ca.mcgill.ecse321.sportcenter.service.AccountService;
import ca.mcgill.ecse321.sportcenter.service.CourseService;
import ca.mcgill.ecse321.sportcenter.service.LocationService;
import ca.mcgill.ecse321.sportcenter.service.SessionService;
import ca.mcgill.ecse321.sportcenter.service.SportCenterManagementService;

//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestInstance(Lifecycle.PER_CLASS)

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SessionIntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private SessionService sessionService;

	@Autowired
    private AccountService accountService;

	@Autowired
    private LocationService locationService;

	@Autowired
    private CourseService courseService;

    @Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private InstructorRepository instructorRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private SportCenterRepository sportCenterRepository;

	//---------------------Headers------------------------------

	private String LOGIN_EMAIL = "julia@mail.com";
    private String LOGIN_PASSWORD = "secret1456165";

	//------------------------- Instrucor ----------------------
	String email = "olivia@mail.com";
    String password = "secretPassword";
    String instructorName = "Olivia";
    String imageURL = "pfp.png";
	Instructor supervisor;


	//------------------------ Location ------------------------

	String floor = "2";
    String room = "200";
	Location location;


	//------------------------ CourseType ----------------------

	String courseName = "a Name";
    String description = "a Description.";
    Difficulty diff = Difficulty.Beginner;
    Status status = Status.Approved;
	Course course;


	//------------------------ Session -------------------------

	Time startTime = Time.valueOf("08:00:00");
    Time endTime = Time.valueOf("09:00:00");
    Date date = Date.valueOf("2024-02-18");
    Integer capacity = 10;
	int validId = 0;



	private final int INVALID_ID = 0;

    @BeforeAll
    @AfterTestClass
	public void clearDatabase() {
		sportCenterRepository.deleteAll();
		sessionRepository.deleteAll();
		courseRepository.deleteAll();
		instructorRepository.deleteAll();
		locationRepository.deleteAll();

		
	}

	@BeforeAll
	public void createAssociations() {
		location = locationService.createLocation(floor, room);
		course = courseService.createCourse(courseName, description, diff, status);
		supervisor = accountService.createInstructorAccount(email, password, courseName, imageURL);
	}

	//--------------------------// General Empty Result Tests //--------------------------//

	@Test
	@Order(0)
	public void testFindAllSessionEmptyResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<SessionListDTO> response = client.exchange("/sessions", HttpMethod.GET, requestEntity, SessionListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty

	}

	@Test
	@Order(1)
	public void testFindSessionsByInstructorEmptyResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		
		String url = "/sessions/instructors/" + supervisor.getId();

		ResponseEntity<SessionListDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, SessionListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty

	}

	@Test
	@Order(2)
	public void testFindSessionsByCourseEmptyResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		

		String url = "/sessions/courses/" + course.getId();
		
		
		ResponseEntity<SessionListDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, SessionListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty

	}

	//------------------------------ Create ------------------------------

	@Test
	@Order(3)
	public void testCreateValidSession(){
		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
		SessionRequestDTO sessionParam = new SessionRequestDTO();
		sessionParam.setCapacity(capacity);
		sessionParam.setDate(date);
		sessionParam.setEndTime(endTime);
		sessionParam.setStartTime(startTime);
        HttpEntity<SessionRequestDTO> requestEntity = new HttpEntity<>(sessionParam, headers);

		///sessions/{iId}/{cId}/{lId}"
		String url = "/sessions/" + supervisor.getId() + "/" + course.getId() + "/" + location.getId();

		ResponseEntity<SessionResponseDTO> response = client.exchange(url, HttpMethod.POST, requestEntity, SessionResponseDTO.class);
		assertNotNull(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
        SessionResponseDTO createdSession = response.getBody();
		

	}

	//---------------------------------- Read by Id, Instructor, Course  ---------------------------
	@Test
    @Order(4)
    public void testReadSessionByValidId() {
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<SessionResponseDTO> response = client.exchange("/sessions/" + validId, HttpMethod.GET, requestEntity, SessionResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
	}

	@Test
    @Order(5)
    public void testReadSessionByInstructor() {
	}

	@Test
    @Order(6)
    public void testReadSessionByCourse() {
	}

	//---------------------------------- Update ---------------------------

	@Test
	@Order(7)
	public void testUpdateValidSession(){

	}

	@Test
	@Order(8)
	public void testUpdateValidSessionInstructor(){

	}

	@Test
	@Order(9)
	public void testUpdateValidSessionLocation(){

	}

	// ----------------------- Delete -----------------

	@Test
	@Order(10)
	public void testDeleteValidSession(){

	}



    
}