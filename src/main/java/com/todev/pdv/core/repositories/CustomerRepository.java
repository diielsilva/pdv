package com.todev.pdv.core.repositories;

import com.todev.pdv.core.models.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

}
