# Data Station Collector

## About The Project

The Data Station Collector is a project where a distributed system with a REST-based API, a RabbitMQ message queue and a JavaFX UI was implemented.

The workflow:
* Customer can input a customer id into the UI and click “Generate Invoice”
* A HTTP Request calls the REST-based API
* The application starts a new data gathering job
* When the data is gathered, it gets send to the PDF generator
* The PDF generator generates the invoice and saves it on the file system
* The UI checks every couple seconds if the invoice is available

It is a docker-compose project that sets up five databases and a queue. One database stores user information, one database stores the access information of the other three databases, which itself store the charging information (every charging station has its own database). 

### The System Overview

A graph of the system:

![image](https://github.com/altink7/Station_DISYS_Project/assets/84073745/990028b7-0399-4cd3-a27e-a200808423de)

## The Components

### Station JavaFX Application

The PDF Generator is a JavaFX Application wher the customer can enter the customer_id. Afterwards a list with all corresponding invoices is displayed. The customer can select the invoice which should be generated as PDF.

![image](https://github.com/altink7/Station_DISYS_Project/assets/84073745/687bb4d8-d5bb-4a5a-8536-792ec13068cd)

### Spring Boot Application

The Spring Boot Application starts the process by sending a start message to the Data Collection Dispatcher. It contains the Data Collection Controller which is responsible for the REST API and starts the data gathering job as well as returns the invoice PDF. The Data Collection Service within this component contains the business logic for the data collection Spring Boot Application. Further more the Name and the Service Queue are part of the Spring Boot Application.

### Data Collection Dispatcher

The Data Collection Dispatcher starts the data gahtering job, has knowledge about the available stations, sends a message for every charging station to the Station Data Collector and sends a message to the Data Collection Receiver, that a new job started. It is responsible for dispatching the data collection to the correct data collector. Furthermor it includes the class Database Connector which is responsible for the connection to the database POSTGRES and the Station Class which represents a station central database. In addition the Name and the Service Queue are included.

## User Guide

This guide will help you set up and run the project using IntelliJ and Docker.

### Requirements

- IntelliJ IDEA (Community or Ultimate edition) installed on your system
- Docker installed on your system

### Installation

1. Clone or download the project from the [GitHub repository](https://github.com/altink7/Station_DISYS_Project.git).
2. Open the project in IntelliJ IDEA.

### Starting Docker

1. Navigate to the project directory.
2. Run the `start_docker.bat` file by double-clicking on it.
   - This script will start the required Docker images using Docker Compose.

### Running the Application

1. Open the project in IntelliJ IDEA.
2. In the IntelliJ toolbar, select the desired run configuration from the dropdown menu.
3. Click the Run button or press Shift+F10 to start the application.
   - This will start all the necessary services and applications defined in the run configuration.

### Generating PDF

1. Once the applications have started, open the GUI for the desired application.
2. Enter the number of customers for whom you want to generate an invoice.
3. Wait for the PDF to be generated. The progress will be displayed in the application.
4. After the PDF is generated, click the "View" button to open the PDF file.
   - The PDF file will be opened using the default PDF viewer on your system.

That's it! You can now use the application to generate invoices and view them in PDF format.

Please note that this guide assumes basic familiarity with IntelliJ IDEA and Docker. If you encounter any issues or have specific questions about the project, refer to the project documentation or consult the project's support resources.

For more information and the latest updates, visit the [GitHub repository](https://github.com/altink7/Station_DISYS_Project.git).
