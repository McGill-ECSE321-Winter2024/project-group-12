package ca.mcgill.ecse321.sportcenter.model;

import jakarta.persistence.Entity;

@Entity
public class Owner extends Account
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Owner() {
    setAuthority(Authority.ROLE_OWNER);
  }
  
  public Owner(String aEmail, String aPassword, String aName, String aImageURL, String aPronouns, int aId, SportCenter aCenter)
  {
    super(aEmail, aPassword, aName, aImageURL, aPronouns, aId, aCenter);
    setAuthority(Authority.ROLE_OWNER);
  }

  
  public void delete()
  {
    super.delete();
  }

}