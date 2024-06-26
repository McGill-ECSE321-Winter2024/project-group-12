package ca.mcgill.ecse321.sportcenter.integration;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.sql.Time;

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
import ca.mcgill.ecse321.sportcenter.dto.BillingAccountListDTO;
import ca.mcgill.ecse321.sportcenter.dto.BillingAccountRequestDTO;
import ca.mcgill.ecse321.sportcenter.dto.BillingAccountResponseDTO;
import ca.mcgill.ecse321.sportcenter.dto.LoginRequestDTO;
import ca.mcgill.ecse321.sportcenter.dto.LoginResponseDTO;
import ca.mcgill.ecse321.sportcenter.model.Customer;
import ca.mcgill.ecse321.sportcenter.repository.CustomerRepository;
import ca.mcgill.ecse321.sportcenter.service.AccountService;
import ca.mcgill.ecse321.sportcenter.service.SportCenterManagementService;

/*
* <p> Integration testing for the BillingAccount use cases with the controller. <p>
* <p> Service class in charge of managing billing accounts. It implements following use cases: </p>
* <p> Create, update, delete a billing account </p>
* @author Anjali
*/
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public class BillingAccountIntegrationTests extends CommonTestSetup {

    @Autowired
    private TestRestTemplate client;

    @Autowired
    private AccountService accountService;

    @Autowired
    SportCenterManagementService sportCenterService;

    @Autowired
    private CustomerRepository customerRepository;

    //---------------------Headers------------------------------

	private String LOGIN_EMAIL = "julia@mail.com";
    private String LOGIN_PASSWORD = "secret1456165";

	//------------------------- Customer ----------------------

	String email = "olivia@mail.com";
    String password = "secretPassword";
    String instructorName = "Olivia";
    String imageURL = "pfp.png";
	Customer customer;

	String newEmail = "dada@mail.com";
    String newPassword = "notsosecretPassword";
    String newInstructorName = "Dada";
    String newImageURL = "dada.png";
	Customer newCustomer;
    
    //------------------------ BillingAccount -------------------------

    String cardHolder = "Mary Jane";
    String billingAddress = "1234, Sherbrooke Street, Montreal";
    String cardNumber =  "1234567891234567";
    Integer cvv = 372;
    boolean isDefault = true;
    LocalDate expirationDate = LocalDate.parse("2026-02-18");
    int validId = 0;

    String newCardHolder = "Bob Smith";
    String newBillingAddress = "9, Wellington Street, Montreal";
    String newCardNumber =  "2000007891234000";
    Integer newCvv = 407;
    boolean newIsDefault = false;
    LocalDate newExpirationDate = LocalDate.parse("2028-11-01");


    //---------------login -------------------------------

    @Test
    @Order(0)
    public void login() {
        sportCenterService.createSportCenter("Fithub", Time.valueOf("6:0:0"), Time.valueOf("23:0:0"), "16", "sportcenter@mail.com", "455-645-4566");
		customer = accountService.createCustomerAccount(email, password, instructorName, imageURL, "");
		newCustomer = accountService.createCustomerAccount(newEmail, newPassword, newInstructorName, newImageURL, "");

        // Save one account in the system
        accountService.createCustomerAccount(LOGIN_EMAIL, LOGIN_PASSWORD, "Julia", "Doritos.png", "");
        
        // Login into that account
        LoginRequestDTO request = new LoginRequestDTO(LOGIN_EMAIL, LOGIN_PASSWORD);
        ResponseEntity<LoginResponseDTO> response = client.postForEntity("/login", request, LoginResponseDTO.class);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    //--------------------------// General Empty Result Tests //--------------------------//

    @Test
	@Order(1)
	public void testFindAllBillingAccountEmptyResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<BillingAccountListDTO> response = client.exchange("/billing-accounts", HttpMethod.GET, requestEntity, BillingAccountListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty

	}

    @Test
	@Order(2)
	public void testFindBillingAccountByCustomerEmptyResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
		String url = "/customers/" + customer.getId() + "/billing-accounts";

		ResponseEntity<BillingAccountListDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, BillingAccountListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty

	}

    @Test
	@Order(3)
	public void testFindDefaultBillingAccountByCustomerEmptyResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
		String url = "/customers/" + customer.getId() + "/billing-account";

		ResponseEntity<BillingAccountResponseDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, BillingAccountResponseDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Should be empty

	}

    //------------------------------ Create ------------------------------

	@Test
	@Order(4)
	public void testCreateValidBillingAccount(){
		
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
		
        BillingAccountRequestDTO accountParam = new BillingAccountRequestDTO();

        assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
        accountParam.setBillingAddress(billingAddress);
        accountParam.setCardHolder(cardHolder);
        accountParam.setCardNumber(cardNumber);
        accountParam.setCvv(cvv);
        accountParam.setIsDefault(isDefault);
        accountParam.setExpirationDate(expirationDate);

        HttpEntity<BillingAccountRequestDTO> requestEntity = new HttpEntity<BillingAccountRequestDTO>(accountParam, headers);

		String url = "/customers/" + customer.getId() + "/billing-accounts";

		ResponseEntity<BillingAccountResponseDTO> response = client.exchange(url, HttpMethod.POST, requestEntity, BillingAccountResponseDTO.class);
		
        assertNotNull(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BillingAccountResponseDTO createdBillingaccount = response.getBody();
		validId = createdBillingaccount.getId();
        assertEquals(cardNumber, createdBillingaccount.getCardNumber());
        assertEquals(cardHolder, createdBillingaccount.getCardHolder());
        assertEquals(cvv, createdBillingaccount.getCvv());
        assertEquals(billingAddress, createdBillingaccount.getBillingAddress());
        assertEquals(isDefault, createdBillingaccount.getIsDefault());
        assertEquals(expirationDate, createdBillingaccount.getExpirationDate());
		
	}

     //------------------------------ Read and Get with valid requests ------------------------------

    @Test
    @Order(5)
    public void testReadBillingAccountByValidId() {
        
        // Set up authentication for this test
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<BillingAccountResponseDTO> response = client.exchange("/billing-accounts/" + validId, HttpMethod.GET, requestEntity, BillingAccountResponseDTO.class);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        BillingAccountResponseDTO createdBillingaccount = response.getBody();
        assertEquals(cardNumber, createdBillingaccount.getCardNumber());
        assertEquals(cardHolder, createdBillingaccount.getCardHolder());
        assertEquals(cvv, createdBillingaccount.getCvv());
        assertEquals(billingAddress, createdBillingaccount.getBillingAddress());
        assertEquals(isDefault, createdBillingaccount.getIsDefault());
        assertEquals(expirationDate, createdBillingaccount.getExpirationDate());
	}

    /* 
	@Test
    @Order(6)
    public void testReadSessionByCustomer() {
		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
		String url = "/customers/" + customer.getId() + "/billing-accounts";

		ResponseEntity<BillingAccountListDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, BillingAccountListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should not be empty
        
	}
    */

    @Test
	@Order(7)
	public void testFindAllBillingAccountValidResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		
		ResponseEntity<BillingAccountListDTO> response = client.exchange("/billing-accounts", HttpMethod.GET, requestEntity, BillingAccountListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should be empty

	}

     
    /*
    @Test
	@Order(8)
	public void testFindBillingAccountByCustomerValidResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
		String url = "/customers/" + customer.getId() + "/billing-accounts";

		ResponseEntity<BillingAccountListDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, BillingAccountListDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should be empty

	}
     */

    @Test
	@Order(9)
	public void testFindDefaultBillingAccountByCustomerValidResult(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
		String url = "/customers/" + customer.getId() + "/billing-account";

		ResponseEntity<BillingAccountResponseDTO> response = client.exchange(url, HttpMethod.GET, requestEntity, BillingAccountResponseDTO.class);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Should be empty
        BillingAccountResponseDTO createdBillingaccount = response.getBody();
        assertEquals(cardNumber, createdBillingaccount.getCardNumber());
        assertEquals(cardHolder, createdBillingaccount.getCardHolder());
        assertEquals(cvv, createdBillingaccount.getCvv());
        assertEquals(billingAddress, createdBillingaccount.getBillingAddress());
        assertEquals(isDefault, createdBillingaccount.getIsDefault());
        assertEquals(expirationDate, createdBillingaccount.getExpirationDate());
	}


    //---------------------------------- Update ---------------------------

	@Test
	@Order(10)
	public void testUpdateValidBillingAccount(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);

		BillingAccountRequestDTO accountParam = new BillingAccountRequestDTO();

        assertNotNull(customerRepository.findCustomerById(customer.getId()));
		
        accountParam.setBillingAddress(newBillingAddress);
        accountParam.setCardHolder(newCardHolder);
        accountParam.setCardNumber(newCardNumber);
        accountParam.setCvv(newCvv);
        accountParam.setIsDefault(newIsDefault);
        accountParam.setExpirationDate(newExpirationDate);

        HttpEntity<BillingAccountRequestDTO> requestEntity = new HttpEntity<BillingAccountRequestDTO>(accountParam,headers);

		String url = "/customers/" + customer.getId() + "/billing-accounts/" + validId;
		ResponseEntity<BillingAccountResponseDTO> response = client.exchange(url, HttpMethod.PUT, requestEntity, BillingAccountResponseDTO.class);

		assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        BillingAccountResponseDTO createdBillingaccount = response.getBody();
        assertEquals(newCardNumber, createdBillingaccount.getCardNumber());
        assertEquals(newCardHolder, createdBillingaccount.getCardHolder());
        assertEquals(newCvv, createdBillingaccount.getCvv());
        assertEquals(newBillingAddress, createdBillingaccount.getBillingAddress());
        assertEquals(newIsDefault, createdBillingaccount.getIsDefault());
        assertEquals(newExpirationDate, createdBillingaccount.getExpirationDate());
	}


    //---------------------------------- Delete ---------------------------


    @Test
	@Order(11)
	public void testDeleteValidBillingAccount(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        assertNotNull(customerRepository.findCustomerById(customer.getId()));

		String url = "/customers/" + customer.getId() + "/billing-accounts/" + validId;
		ResponseEntity<BillingAccountResponseDTO> response = client.exchange(url, HttpMethod.DELETE, requestEntity, BillingAccountResponseDTO.class);

		assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<BillingAccountResponseDTO> responseRead = client.exchange("/billing-accounts/" + validId, HttpMethod.GET, requestEntity, BillingAccountResponseDTO.class);
        BillingAccountResponseDTO billingAccount = responseRead.getBody();
        assertNull(billingAccount.getCardHolder());

	}

    @Test
	@Order(12)
	public void testDeleteInvalidBillingAccount(){

		HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(LOGIN_EMAIL, LOGIN_PASSWORD);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        assertNotNull(customerRepository.findCustomerById(customer.getId()));

        int invalidId = 100;

		String url = "/customers/" + customer.getId() + "/billing-accounts/" + invalidId;
		ResponseEntity<BillingAccountResponseDTO> response = client.exchange(url, HttpMethod.DELETE, requestEntity, BillingAccountResponseDTO.class);

		assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

	}

}
