# Chat Server using Kafka and MySQL #

This application demos use of Kafka to create a scalable chat server.

## Requirements ##
* Java 8
* Spring Boot
* Maven
* MySQL
* Kafka

### Run Application ###
* Alter mysql connection properties in _application.properties_
* Create the database as mentioned in connection properties
* Start Kafka. In this projects default port(9092) is configured on localhost
* Start the application either from ChatApplication class file in IDE or by building and executing the jar.

Postman collection is present in the root folder with basic APIs. You can import it in your postman.

###Usage###
1. Create Users ` Use _Add User_ Api from Postman Collection` 
2. Login with a user(sender) and note the Auth Token
3. Login with receiver users and note their auth token
4. For testing receivers you can use `https://www.websocket.org/echo.html`. Connect on the following web socket address `ws://localhost:8080/chat?Authorization={authToken}`
5. Using Sender's Auth token in header, use `Send Chat Message API` to send Message. Set receiver's userId in `toUserId` field.
6. The sent message will be received by the relevant user.


###Enhancements###
1. Different message types to be supported
2. Save message in database and add API to be used upon login to fetch all messages.
