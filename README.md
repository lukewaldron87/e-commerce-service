e-commerce service
===

A Spring Boot app that implements a basic e-commerce service

## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi)

## Running the application locally

There are two ways to run this project.

The first is to run the JAR file

```shell
java -jar target/
```

Alternatively you can run it with maven

```shell
mvn spring-boot:run
```

## API Endpoints
The REST API is described below

# Products

## Create Product

### Request

`POST /products/`

Create a new product

Body

    json
    {
        "name": "Book",
        "price": 19.9912345
    }

### Response

    Status: 201 Created
    {"id":5,"name":"REST Book 1","price":19.9912345}

## Update Product For Id

### Request

`PUT /products/:id`

Update an existing product with the specified ID. 

Body

    json
    {
        "name": "Updated Product",
        "price": 99.99
    }

### Response

    Status: 200 OK
    {"id":1,"name":"Updated Product","price":99.99}

## Get All Products

### Request

`GET /products/`

Get a list of all existing products

### Response

    Status: 200 OK
    [
        {
            "id": 1,
            "name": "Updated Product",
            "price": 99.99
        },
        {
            "id": 2,
            "name": "Mug",
            "price": 14.99
        },
        {
            "id": 3,
            "name": "T-Shirt",
            "price": 30.00
        }
    ]

## Delete Product For ID

### Request

`GET /products/:id`

Delete a product with the given ID

### Response

    Status: 200 OK

