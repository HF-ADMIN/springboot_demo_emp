package com.example.demo2.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="employee")
public class EmployeeDAO {  
    @Id 
    @Column(name="id", nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", length=20)
    private String name;

    @Column(name="employee_number")
    private Integer employee_number;

    @Column(name="sign_up_date")
    private Date sign_up_date;

    @Column(name="position", length=20)
    private String position;

    @Column(name="admin_yn", length=1)
    private String admin_yn;
}
