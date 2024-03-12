package ca.mcgill.ecse321.sportcenter.dto;

import ca.mcgill.ecse321.sportcenter.model.Account;
import ca.mcgill.ecse321.sportcenter.model.Customer;
import ca.mcgill.ecse321.sportcenter.model.Instructor;
import ca.mcgill.ecse321.sportcenter.model.Owner;

public class AccountResponseDTO {
    private Integer id;
    private AccountType type;
    private String email;
    private String password;
    private String name;
    private String imageURL;

    public AccountResponseDTO(Customer customer) {
        this.id = customer.getId();
        this.type = AccountType.CUSTOMER;
        this.email = customer.getEmail();
        this.password = customer.getPassword();
        this.name = customer.getName();
        this.imageURL = customer.getImageURL();
    }

    public AccountResponseDTO(Instructor instructor) {
        this.id = instructor.getId();
        this.type = AccountType.INSTRUCTOR;
        this.email = instructor.getEmail();
        this.password = instructor.getPassword();
        this.name = instructor.getName();
        this.imageURL = instructor.getImageURL();
    }

    public AccountResponseDTO(Owner owner) {
        this.id = owner.getId();
        this.type = AccountType.OWNER;
        this.email = owner.getEmail();
        this.password = owner.getPassword();
        this.name = owner.getName();
        this.imageURL = owner.getImageURL();
    }

    public static AccountResponseDTO create(Account account) {
        if (account instanceof Customer) {
            return new CustomerResponseDTO((Customer) account);
        } else if (account instanceof Instructor) {
            return new InstructorResponseDTO((Instructor) account);
        } else if (account instanceof Owner) {
            return new OwnerResponseDTO((Owner) account);
        } else {
            throw new IllegalArgumentException("Unknown account type.");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
