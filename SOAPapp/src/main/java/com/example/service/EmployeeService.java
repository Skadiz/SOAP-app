package com.example.service;

import com.example.service.exceptions.DatabaseException;
import com.example.service.exceptions.EmployeeAlreadyExists;
import com.example.service.exceptions.EmployeeNotFoundException;
import com.example.service.exceptions.TypeDoesNotExistException;
import com.example.service.model.Employees;
import com.example.service.model.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.Endpoint;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService
public class EmployeeService {

    private Map<String, Person> employeesData;
    private Map<String, Person> externalData = new HashMap<>();
    private Map<String, Person> internalData = new HashMap<>();


    public EmployeeService() {
        employeesData = new HashMap<>();
        loadEmployeesFromXML();
        mergeMaps(employeesData, externalData, "ext_");
        mergeMaps(employeesData, internalData, "int_");
        for (Map.Entry<String, Person> entry : employeesData.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/employee";
        Endpoint.publish(url, new EmployeeService());
        System.out.println("SOAP Service is published at " + url);
    }

    @WebMethod
    public Employees findEmployee(@WebParam(name = "personId") String personId,
                                  @WebParam(name = "firstName") String firstName,
                                  @WebParam(name = "lastName") String lastName,
                                  @WebParam(name = "mobile") String mobile,
                                  @WebParam(name = "email") String email,
                                  @WebParam(name = "pesel") String pesel,
                                  @WebParam(name = "type") String type) throws EmployeeNotFoundException {
        List<Person> people = new ArrayList<>();
        Employees employees = new Employees();
        String prefix = "";
        if (type.equals("External"))
            prefix = "ext_";
        else if (type.equals("Internal"))
            prefix = "int_";
        for (Map.Entry<String, Person> entry : employeesData.entrySet()) {
            if ((personId != null && (personId.equals(entry.getValue().getPersonId()))) ||
                    ((type.equals("External") || type.equals("Internal")) && (entry.getKey().startsWith(prefix))) ||
                    (firstName != null && firstName.equals(entry.getValue().getFirstName())) ||
                    (lastName != null && lastName.equals(entry.getValue().getLastName())) ||
                    (mobile != null && mobile.equals(entry.getValue().getMobile())) ||
                    (email != null && email.equals(entry.getValue().getEmail())) ||
                    (pesel != null && pesel.equals(entry.getValue().getPesel()))) {
                people.add(entry.getValue());
            }
            employees.setEmployees(people);
        }
        if (people.isEmpty())
            throw new EmployeeNotFoundException();
        else
            return employees;
    }

    @WebMethod
    public String createEmployee(@WebParam(name = "person") Person person, @WebParam(name = "type") String type) throws EmployeeAlreadyExists, ValidationException, DatabaseException, TypeDoesNotExistException {
        if (type == null) throw new TypeDoesNotExistException();
        String prefix = " ";
        if (type.equals("External"))
            prefix = "ext_";
        else if (type.equals("Internal"))
            prefix = "int_";
        if (!employeesData.containsKey(prefix + person.getPersonId())) {
            if (person.getPersonId() == null || person.getPersonId().isEmpty()) {
                throw new ValidationException("Person ID cannot be null or empty.");
            }
            if (type.equals("External")) {
                externalData.put(person.getPersonId(), person);
                saveEmployeesToXML(externalData, type);
            } else if (type.equals("Internal")) {
                internalData.put(person.getPersonId(), person);
                saveEmployeesToXML(internalData, type);
            }
            return ("Employee with ID " + person.getPersonId() + " created successfully.");
        } else {
            throw new EmployeeAlreadyExists(person.getPersonId());
        }
    }


    @WebMethod
    public String modifyEmployee(Person person) throws EmployeeAlreadyExists, DatabaseException {
        if (person != null && person.getPersonId() != null) {
            String personId = person.getPersonId();
            if (employeesData.containsKey("ext_" + personId)) {
                externalData.put(personId, person);
                saveEmployeesToXML(externalData, "External");
            } else if (employeesData.containsKey("int_" + personId)) {
                internalData.put(personId, person);
                saveEmployeesToXML(internalData, "Internal");
            }
            return "Employee with ID " + personId + " updated successfully.";
        } else {
            throw new NullPointerException();
        }

    }

    @WebMethod
    public String removeEmployee(@WebParam(name = "personId") String personId) throws EmployeeNotFoundException, DatabaseException {
        Person person;
        System.out.println("ext_" + personId);
        if (employeesData.containsKey("ext_" + personId)) {
            person = employeesData.get("ext_" + personId);
            System.out.println(externalData.containsKey(personId));
            externalData.remove(personId);
            saveEmployeesToXML(externalData, "External");
            if (employeesData.remove("ext_" + personId) != null)
                return "Employee with id: " + person.getPersonId() + "removed successfully";
            else
                throw new EmployeeNotFoundException();
        } else if (employeesData.containsKey("int_" + personId)) {
            person = employeesData.get("int_" + personId);
            internalData.remove(personId);
            saveEmployeesToXML(internalData, "Internal");
            if (employeesData.remove("int_" + personId) != null)
                return "Employee with id: " + person.getPersonId() + "removed successfully";
            else
                throw new EmployeeNotFoundException();
        } else
            throw new EmployeeNotFoundException(personId);

    }


    @WebMethod
    public Employees listEmployees() {
        List<Person> people = new ArrayList<>(employeesData.values());
        Employees employees = new Employees();
        employees.setEmployees(people);
        return employees;
    }

    private void saveEmployeesToXML(Map<String, Person> employees, String type) throws DatabaseException {
        try {
            File file;
            boolean isExternal = false;
            if (type.equals("External")) {
                file = new File("src/main/resources/employees/external/employee1.xml");
                isExternal = true;
            } else file = new File("src/main/resources/employees/internal/employee2.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Employees.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Employees employeeList = new Employees();
            employeeList.setEmployees(new ArrayList<>(employees.values()));

            marshaller.marshal(employeeList, file);
            if (isExternal)
                mergeMaps(employeesData, employees, "ext_");
            else mergeMaps(employeesData, employees, "int_");
            for (Map.Entry<String, Person> entry : employeesData.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new DatabaseException("An error occurred while saving data to the database: " + e.getMessage());
        }
    }

    private void loadEmployeesFromXML() {
        File externalDir = new File("src/main/resources/employees/external");
        File internalDir = new File("src/main/resources/employees/internal");

        loadEmployeesFromDirectory(externalDir);
        loadEmployeesFromDirectory(internalDir);
    }

    private void loadEmployeesFromDirectory(File directory) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".xml"));
        if (files != null) {
            for (File file : files) {
                if (directory.getName().contains("external")) {
                    parseEmployeeXML(file, externalData);
                } else parseEmployeeXML(file, internalData);
            }
        }
    }

    private void parseEmployeeXML(File file, Map<String, Person> dataMap) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            Element rootElement = doc.getDocumentElement();
            NodeList nodeList = rootElement.getElementsByTagName("Person");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Person employee = new Person();

                    employee.setPersonId(element.getElementsByTagName("personId").item(0).getTextContent());
                    employee.setFirstName(element.getElementsByTagName("firstName").item(0).getTextContent());
                    employee.setLastName(element.getElementsByTagName("lastName").item(0).getTextContent());
                    employee.setMobile(element.getElementsByTagName("mobile").item(0).getTextContent());
                    employee.setEmail(element.getElementsByTagName("email").item(0).getTextContent());
                    employee.setPesel(element.getElementsByTagName("pesel").item(0).getTextContent());
                    dataMap.put(employee.getPersonId(), employee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeMaps(Map<String, Person> target, Map<String, Person> source, String prefix) {
        for (Map.Entry<String, Person> entry : source.entrySet()) {
            String newKey = prefix + entry.getKey();
            target.put(newKey, entry.getValue());
        }
    }


}

