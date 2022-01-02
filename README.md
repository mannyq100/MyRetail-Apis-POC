# myRetail RESTful service

myRetail is a rapidly growing company with HQ in Richmond, VA and over 200 stores across the east coast. myRetail wants to make its internal data available to any number of client devices, from myRetail.com to native mobile apps.
The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API, which will aggregate product data from multiple sources and return it as JSON to the caller.
Your goal is to create a RESTful service that can retrieve product and price details by ID. The URL structure is up to you to define, but try to follow some sort of logical convention.
Build an application that performs the following actions:
•	Responds to an HTTP GET request at /products/{id} and delivers product data as JSON (where {id} will be a number.
Example product IDs: 13860428, 54456119, 13264003, 12954218)
•	Example response: {"id":13860428,"name":"The Big Lebowski (Blu-ray) (Widescreen)","current_price":{"value": 13.49,"currency_code":"USD"}}
•	Performs an HTTP GET to retrieve the product name from an external API. (For this exercise the data will come from redsky.target.com, but let’s just pretend this is an internal resource hosted by myRetail)  
•	Example:
https://redsky-uat.perf.target.com/redsky_aggregations/v1/redsky/case_study_v1?key=3yUxt7WltYG7MFKPp7uyELi1K40ad2ys&tcin=13860428
•	Reads pricing information from a NoSQL data store and combines it with the product id and name from the HTTP request into a single response.  
•	BONUS: Accepts an HTTP PUT request at the same path (/products/{id}), containing a JSON request body similar to the GET response, and updates the product’s price in the data store.  

<hr>

### Technology Stack
- Java 11
- SpringBoot and Spring Webflux
- Gradle
- Mongodb

### To Run Application
This application can be run in any Java IDE or using Gradle :
- `./gradlew clean build`
- `./gradlew bootRun`

It runs on the default port 8080. <br>
A connection to mongodb is required for the application to start. 
Connection properties is available under resources/application.yaml.

Alternatively, mongodb can be run locally using docker
- `docker run --name mongodb -d -p 27017:27017 mongo`

### On Application Start
On startup, application is configured to load 4 default products into the database for testing. ProductIds loaded are
-	13860428
-	54456119
-	13264003
-	12954218

###Api documentation:
Swagger documentation is available at http://localhost:8080/swagger-ui.html

### Security:
Spring security is leveraged to secure the api endpoints. <br>
Two users are configured internally.
-	Normal User`(username: user, password: password)`: can retrieve product
-	Admin User `(username: admin, password: password)`: can retrieve product and update product price

### Additional Features implemented:
- `Spring webflux` and `reactive-mongo` libraries leveraged for reactive/non-blocking programming.
- `Validation` for user request’s body and `error handling`.
- Up to 3 `retries` for calls to external api using backoff when server error recieved.
- `Integration tests` for api endpoints under project test folder.
- An endpoint to add product price to database.






