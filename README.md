This is Java Application to implement a simple ticket service that facilitates the discovery, temporary hold, and final reservation of seats within a high-demand performance
venue.

Conventions:
====================
This is stand-alone Java application with command line interface that allows the user select option to 
1.Find number of available seats 
2.Find and hold Seats
3.Reserve Seats
4.Exit

Assumptions:
====================
1. Venue will have holdTime , this hold time is applicable to all seats at venue.

2. Available seats will have no Seat Hold and Seat Hold for which hold time has expired will have status Available.

2.The program takes three arguments from user as command line input 
1.Numbers of seats in a row 
2.Number of seats in a columns 
3.Hold Time for the Seat at Venue in seconds

3.Program will run with default values set in POM.xml if the user does not provide command line arguments
If the user provides invalid command line argument, the program will initialize with default values which are
1.Numbers of seats in a row =6
2.Number of seats in a columns =4 
3.Hold Time for the Seat at Venue =600 seconds

4.As per the requirement (Implementation mechanisms such as disk-based storage, a REST API, and a front-end GUI are not required), no Rest API , or disk based storage or front-end GUI are implemented 

INSTALLATION
================

System Requirements
--------------------
* Maven 2.0.9 or higher
* Java 1.8

Building and installing
====================
Copy the Code in a directory and navigate to testService directory
To Run Test use command mvn clean install

To Run Application with default values set in POM.xml use command - 
mvn clean install exec:java

To Run Application with arguments use command where 
Numbers of seats in a row=5,Numbers of seats in a columns=6, Hold Time for the Seat at Venue in seconds=10
mvn clean install exec:java  -Dexec.args="5 6 10"

Design Approach
====================
Please Refer to Class Diagram which explains the Model design.
