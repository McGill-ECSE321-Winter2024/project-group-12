package ca.mcgill.ecse321.sportcenter.repository;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.sportcenter.model.Account;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    Account findAccountById(int id);
}
