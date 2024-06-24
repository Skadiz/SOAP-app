# Project "EmployeeService"
## Description
The "EmployeeService" project is a SOAP-based web service for managing employee data. This service allows operations such as creating, reading, updating, and deleting employee information, as well as listing all employees based on various criteria.

## Project Structure
The project consists of several key components:

- EmployeeService: The main class implementing the web service logic.
- Person: A model class representing employee data.
- Exceptions: Exception classes for handling errors in the service.
- XML Handling: Functionality for saving and loading employee data to/from XML files.
## Usage
To use the "EmployeeService" web service, follow these steps:

1. Run the Service:

Start the application containing the EmployeeService class. By default, the service will be available at http://localhost:8080/employee.

2. Invoke Service Methods:

Use SOAP requests to invoke methods such as createEmployee, findEmployee, modifyEmployee, removeEmployee, listEmployees.
## Examples of SOAP Requests
Below are examples of XML requests to invoke methods on the "EmployeeService" web service:

### Create Employee (createEmployee):
`<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:createEmployee>
         <!--Optional:-->
         <person>
            <email>jane.doe@example.com</email>
            <firstName>Jane</firstName>
            <lastName>Doe</lastName>
            <mobile>1234567890</mobile>
            <personId>ext_123456</personId>
            <pesel>12345678901</pesel>
         </person>
         <type>External</type>
      </ser:createEmployee>
   </soapenv:Body>
</soapenv:Envelope>
`

### Find Employee (findEmployee):
` <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:findEmployee>
         <!--Optional:-->
         <personId>ext_123456</personId>
         <!--Optional:-->
         <firstName/>
         <!--Optional:-->
         <lastName/>
         <!--Optional:-->
         <mobile/>
         <!--Optional:-->
         <email/>
         <!--Optional:-->
         <pesel/>
         <type>External</type>
      </ser:findEmployee>
   </soapenv:Body>
</soapenv:Envelope>
`

### Update Employee (modifyEmployee):
`<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:modifyEmployee>
         <!--Optional:-->
         <person>
            <email>jane.updated@example.com</email>
            <firstName>Jane Updated</firstName>
            <lastName>Doe</lastName>
            <mobile>1234567890</mobile>
            <personId>ext_123456</personId>
            <pesel>12345678901</pesel>
         </person>
      </ser:modifyEmployee>
   </soapenv:Body>
</soapenv:Envelope>
`
### Remove Employee (removeEmployee):
`<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:removeEmployee>
         <!--Optional:-->
         <personId>ext_123456</personId>
      </ser:removeEmployee>
   </soapenv:Body>
</soapenv:Envelope>
`

### List Employees (listEmployees):
`<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://service.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:listEmployees/>
   </soapenv:Body>
</soapenv:Envelope>
`

## Conclusion
The "EmployeeService" project provides a straightforward and efficient way to manage employee data through a web service. Use the provided examples to integrate with the service and adapt it to your needs.
