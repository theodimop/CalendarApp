CS5031 S1 2016/17
Shared calendar

************************************
Table of Contents:

I   build
II  Run
III Logging in
IV  Adding events
V   Adding users and locations
VI  Other gradle commands

************************************

I Build
    To build the file, run the gradle command 'build'.
    This will create two .jar files: 'BackEnd.jar' and 'FrontEnd.jar'.
    'BackEnd.jar' is the server for the calendar and interacts with a MySQL database for persistence.
    'FrontEnd.jar' is the gui for the calendar for use by the user.

II Run
    To run the application first run the 'BackEnd.jar'.
    Then run the 'FrontEnd.jar'.
    Note: The server is hosted at 'http://localhost:8080/server/' with the MySQL database located at
    'jdbc:mysql://irs6.host.cs.st-andrews.ac.uk:3306/irs6_cal'.

III Logging in
    To log in use the admin login email: 'admin@admin.com'.
    To log in as a normal user use this email for example: 'user1@something.com'.
    The admin will have the ability to add other users and locations.

IV Adding events
    These are the valid fields for adding an event:

    The valid locations: location1
                         location2
                         location3

    The valid users: user1@something.com
                     user2@something.com
                     user3@something.com

V Adding users and locations
    The admin can add more locations and users.
    It is advised that if a new user is added that they a use a valid email.
    That way they can receive updates via email about events they are invited to.

VI Other Gradle commands
    jacocoTestReport: creates a report for code coverage
    pitest: runs mutation testing and creates report
    checkstyleMain/checkstyleTest: creates a report for the style testing using the school's style checker
    see build.gradle for more information.