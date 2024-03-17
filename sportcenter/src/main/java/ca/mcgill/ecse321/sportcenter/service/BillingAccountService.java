package ca.mcgill.ecse321.sportcenter.service;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.sportcenter.model.BillingAccount;
import ca.mcgill.ecse321.sportcenter.model.Customer;
import ca.mcgill.ecse321.sportcenter.repository.BillingAccountRepository;

import jakarta.transaction.Transactional;

/*
* <p> Service class in charge of managing billing accounts. It implements following use cases: </p>
* <p> Create, update, delete a billing account </p>
* @author Anjali
*/
@Service
public class BillingAccountService {
    
    @Autowired
    private BillingAccountRepository billingAccountRepo; 

    //--------------------------// Create Billing Account //--------------------------//

    @Transactional
    public BillingAccount createBillingAccount(BigInteger cardNumber, String cardHolder, String billingAddress, Integer cvv, boolean isDefault, Date expirationDate, Customer customer){
        
        //Input validation check
        validBillingAccountInfo(cardNumber, cardHolder, billingAddress, cvv, expirationDate);

        if (customer == null){
            throw new IllegalArgumentException("Customer account does not exist");
        }

        List<BillingAccount> accounts = findBillingAccountByCustomer(customer);
        if (accounts.size() != 0){
            for (BillingAccount account : accounts){
                
                //check if the same card number exists for the given customer account
                if (account.getCardNumber().compareTo(cardNumber)==0){
                    throw new IllegalArgumentException("This card already exists");
                }

                //if another billing account is default, instead make this default
                if (isDefault == true){
                    if(account.getIsDefault() == true){
                        account.setIsDefault(false);
                    }
                }      
            }
        }

        BillingAccount billingAccount = new BillingAccount();
        billingAccount.setBillingAddress(billingAddress);
        billingAccount.setCardNumber(cardNumber);
        billingAccount.setCardHolder(cardHolder);
        billingAccount.setCvv(cvv);
        billingAccount.setExpirationDate(expirationDate);
        billingAccount.setIsDefault(isDefault);
        billingAccount.setCustomer(customer);

        return billingAccountRepo.save(billingAccount);

    }

    //--------------------------// Update Account //--------------------------//

    @Transactional
    public BillingAccount updateBillingAccount(Integer id, BigInteger cardNumber, String cardHolder, String billingAddress, Integer cvv, boolean isDefault, Date expirationDate){
        
        validBillingAccountInfo(cardNumber, cardHolder, billingAddress, cvv, expirationDate);

        BillingAccount account = findBillingAccountById(id);

        List<BillingAccount> accounts = findBillingAccountByCustomer(account.getCustomer());
        if (accounts.size() != 0){
            for (BillingAccount acc : accounts){
                
                //check if the same card number exists for the given customer account
                if (acc.getCardNumber().compareTo(cardNumber)==0){
                    throw new IllegalArgumentException("This card already exists");
                }

                //if another billing account is default, instead make this default
                if (isDefault == true){
                    if(account.getIsDefault() == true){
                        account.setIsDefault(false);
                    }
                }      
            }
        }

        account.setCardNumber(cardNumber);
        account.setCardHolder(cardHolder);
        account.setBillingAddress(billingAddress);
        account.setCvv(cvv);
        account.setExpirationDate(expirationDate);
        account.setIsDefault(isDefault);

        return billingAccountRepo.save(account);

    }

    //--------------------------// Delete Account //--------------------------//

    @Transactional
    public void deleteBillingAccount(Integer id){
        billingAccountRepo.delete(findBillingAccountById(id));
    }

    //--------------------------// Getters //--------------------------//

    @Transactional
    public BillingAccount findBillingAccountById(Integer id){
        BillingAccount account = billingAccountRepo.findBillingAccountById(id);
        if (account == null){
            throw new IllegalArgumentException("There is no billing account with ID " + id + ".");
        }
        return account;
    }

    @Transactional
    public List<BillingAccount> findBillingAccountByCustomer(Customer customer){
        List<BillingAccount> account = billingAccountRepo.findBillingAccountByCustomer(customer);
        if (account == null){
            throw new IllegalArgumentException("There is no billing account with for this customer.");
        }
        return account;
    }

    @Transactional
    public BillingAccount findDefaultBillingAccountOfCustomer(Customer customer){
        List<BillingAccount> accounts = findBillingAccountByCustomer(customer);
        for (BillingAccount account: accounts){
            if (account.getIsDefault() == true){
                return account;
            }
        }
        return null;
    }

    @Transactional
    public Iterable<BillingAccount> findAllBillingAccounts() {
        return billingAccountRepo.findAll();
    }

    //--------------------------// Input validations //--------------------------//

    private void validBillingAccountInfo(BigInteger cardNumber, String cardHolder, String billingAddress, Integer cvv, Date expirationDate){
        if (cardNumber == null || cardHolder.isEmpty() || cardHolder.trim().length() == 0 || billingAddress.isEmpty() || billingAddress.trim().length() == 0 || cvv == null || expirationDate == null){
            throw new IllegalArgumentException("Empty fields for cardNumber, cardHolder, billingAddress, cvv or expirationDate are not valid");
        }
        if (cardNumber.toString().length() != 16){
            throw new IllegalArgumentException("Invalid cardNumber; needs to be exactly 16 digits");
        }
        if (Integer.toString(cvv).length() != 3){
            throw new IllegalArgumentException("Invalid cvv; needs to be exactly 3 digits");
        }
        if (expirationDate.before(java.sql.Date.valueOf(LocalDate.now()))){
            throw new IllegalArgumentException("Invalid expirationDate");
        }

    }

}
