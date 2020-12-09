package com.example.demo2.repository;

import com.example.demo2.dao.EmployeeDAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<EmployeeDAO, Long> {
    @Query(nativeQuery = true, value = "select * from employee where id = ?1")
    EmployeeDAO findById(Integer id);

    @Query(nativeQuery = true, value = "select * from employee where id = ?1")
    EmployeeDAO mergeIntoEmp(EmployeeDAO entity);
}
