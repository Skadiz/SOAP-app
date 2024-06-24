package com.example.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Employees {
    private List<Person> people;

    public Employees() {
    }

    public Employees(List<Person> people) {
        this.people = people;
    }

    @XmlElement(name = "Person")
    public List<Person> getEmployees() {
        return people;
    }

    public void setEmployees(List<Person> people) {
        this.people = people;
    }
}