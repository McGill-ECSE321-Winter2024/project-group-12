package ca.mcgill.ecse321.sportcenter.dto;

import ca.mcgill.ecse321.sportcenter.model.BillingAccount;

import java.time.LocalDate;

public class BillingAccountResponseDTO {
    

    private String cardNumber;
    private String cardHolder;
    private String billingAddress;
    private Integer cvv; 
    private Integer id;
    private boolean isDefault;
    private LocalDate expirationDate;
    private CustomerResponseDTO customer;

    private String error;

    public BillingAccountResponseDTO(BillingAccount aAccount){

        this.id = aAccount.getId();
        this.cardNumber = aAccount.getCardNumber();
        this.cardHolder = aAccount.getCardHolder();
        this.billingAddress = aAccount.getBillingAddress();
        this.expirationDate = aAccount.getExpirationDate();
        this.cvv = aAccount.getCvv();
        this.isDefault = aAccount.getIsDefault();
        this.customer = new CustomerResponseDTO(aAccount.getCustomer());

    }

    public BillingAccountResponseDTO(){
        
    }

    public BillingAccountResponseDTO(String error) { 
        this.error = error;
    }

    //--------------------- Getters -------------------//

    public String getCardNumber(){
        return cardNumber;
    }

    public String getCardHolder(){
        return cardHolder;
    }

    public String getBillingAddress(){
        return billingAddress;
    }

    public Integer getCvv(){
        return cvv;
    }

    public LocalDate getExpirationDate(){
        return expirationDate;
    }

    public boolean getIsDefault(){
        return isDefault;
    }

    public CustomerResponseDTO getCustomer(){
        return customer;
    }

    public Integer getId(){
        return id;
    }

    public String getError() {
        return error;
    }

    //--------------------- Setters -------------------//

    public void setCardHolder(String aCardHolder){
        this.cardHolder = aCardHolder;
    }

    public void setCardNumber(String aCardNumber){
        this.cardNumber = aCardNumber;
    }

    public void setBillingAddress(String aBillingAddress){
        this.billingAddress = aBillingAddress;
    }

    public void setCvv(Integer aCvv){
        this.cvv = aCvv;
    }

    public void setExpirationDate(LocalDate aExpirationDate){
        this.expirationDate = aExpirationDate;
    }

    public void setIsDefault(boolean aIsDefault){
        this.isDefault = aIsDefault;
    }

    public void setCustomer(CustomerResponseDTO aCustomer){
        this.customer = aCustomer;
    }

    public void setId(Integer aId){
        this.id = aId;
    }
    public void setError(String error) {
        
    }
}
