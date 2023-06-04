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
