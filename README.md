A Spring boot application that uses JWT to do Role based CRUD operations.
1) Login service to fetch the JWT token and use in the subsequent requests
2) Users and Products are created using the flyway scripts during the start up
3) Modular based implementation which can be easily divided to Microservices based on the need
4) As this is a simple application, The DB Password and the Secret key are maintained in the Environmental file while running the application in the IDE.
