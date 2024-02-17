package ca.mcgill.ecse321.sportcenter.model;
import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;

@Entity
public class BillingAccount
{
  @Id
  @GeneratedValue
  private int id;
  private int cardNumber;
  private String cardHolder;
  private String billingAddress;
  private int cvv;
  private Date expirationDate;
  private boolean isDefault;

  //BillingAccount Associations
  @ManyToOne(cascade = CascadeType.ALL)
  private Customer customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  protected BillingAccount() {

  }

  public BillingAccount(int aCardNumber, String aCardHolder, String aBillingAddress, int aCvv, Date aExpirationDate, boolean aIsDefault, Customer aCustomer)
  {
    cardNumber = aCardNumber;
    cardHolder = aCardHolder;
    billingAddress = aBillingAddress;
    cvv = aCvv;
    expirationDate = aExpirationDate;
    isDefault = aIsDefault;
    if (!setCustomer(aCustomer))
    {
      throw new RuntimeException("Unable to create BillingAccount due to aCustomer. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  public boolean setCardNumber(int aCardNumber)
  {
    boolean wasSet = false;
    cardNumber = aCardNumber;
    wasSet = true;
    return wasSet;
  }

  public boolean setCardHolder(String aCardHolder)
  {
    boolean wasSet = false;
    cardHolder = aCardHolder;
    wasSet = true;
    return wasSet;
  }

  public boolean setBillingAddress(String aBillingAddress)
  {
    boolean wasSet = false;
    billingAddress = aBillingAddress;
    wasSet = true;
    return wasSet;
  }

  public boolean setCvv(int aCvv)
  {
    boolean wasSet = false;
    cvv = aCvv;
    wasSet = true;
    return wasSet;
  }

  public boolean setExpirationDate(Date aExpirationDate)
  {
    boolean wasSet = false;
    expirationDate = aExpirationDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setIsDefault(boolean aIsDefault)
  {
    boolean wasSet = false;
    isDefault = aIsDefault;
    wasSet = true;
    return wasSet;
  }

  public boolean setId(int aId)
  {
    boolean wasSet = false;
    id = aId;
    wasSet = true;
    return wasSet;
  }

  public int getCardNumber()
  {
    return cardNumber;
  }

  public String getCardHolder()
  {
    return cardHolder;
  }

  public String getBillingAddress()
  {
    return billingAddress;
  }

  public int getCvv()
  {
    return cvv;
  }

  public Date getExpirationDate()
  {
    return expirationDate;
  }

  public boolean getIsDefault()
  {
    return isDefault;
  }

  public int getId()
  {
    return id;
  }
  
  public Customer getCustomer()
  {
    return customer;
  }
  
  public boolean setCustomer(Customer aNewCustomer)
  {
    boolean wasSet = false;
    if (aNewCustomer != null)
    {
      customer = aNewCustomer;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    customer = null;
  }


  public String toString()
  {
    return super.toString() + "["+
            "cardNumber" + ":" + getCardNumber()+ "," +
            "cardHolder" + ":" + getCardHolder()+ "," +
            "billingAddress" + ":" + getBillingAddress()+ "," +
            "cvv" + ":" + getCvv()+ "," +
            "isDefault" + ":" + getIsDefault()+ "," +
            "id" + ":" + getId()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "expirationDate" + "=" + (getExpirationDate() != null ? !getExpirationDate().equals(this)  ? getExpirationDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}