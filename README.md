e-commerce service
===

A Spring Boot app that implements a basic e-commerce service

## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi)

## Running the application locally

There are two ways to run this project. Both are executed from the project's root folder

The first is to build and run the JAR file

```shell
# to build the jar file run 
mvn clean package

# to run the jar file run
java -jar target/e-commerce-service-0.0.1-SNAPSHOT.jar
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
        "price": 19.99
    }

### Response

    Status: 201 Created
    {"id":6,"name":"Book","price":19.99}

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

`DELETE /products/:id`

Delete a product with the given ID. Currently, delete only works for products that are not associated with a basket or order

### Response

    Status: 200 OK

# Baskets

## Create Basket For Product

### Request

`POST /baskets/`

Create a new basket with given product. The amount of the product to be added is specified in productCount

Body

    json
    {
        "productId": 1,
        "productCount": 5
    }

### Response

    Status: 201 Created
    {
        "id": 3,
        "goodIdToBasketItemMap": {
            "1": {
                "id": 6,
                "productId": 1,
                "product": null,
                "productCount": 5,
                "basketId": 3
            }
        }
    }

## Get Basket For Id

### Request

`GET /baskets/:id`

Get a basket for the given ID

### Response

    Status: 200 OK
    {
        "id": 1,
        "goodIdToBasketItemMap": {
            "1": {
                "id": 1,
                "productId": 1,
                "product": {
                    "id": 1,
                    "name": "Video Game",
                    "price": 45.50
                },
                "productCount": 3,
                "basketId": 1
            },
            "2": {
                "id": 2,
                "productId": 2,
                "product": {
                    "id": 2,
                    "name": "Mug",
                    "price": 14.99
                },
                "productCount": 2,
                "basketId": 1
            },
            "3": {
                "id": 3,
                "productId": 3,
                "product": {
                    "id": 3,
                    "name": "T-Shirt",
                    "price": 30.00
                },
                "productCount": 1,
                "basketId": 1
            }
        }
    }

## Get Total Price For Basket

### Request

`GET /baskets/:id/total`

Get price for all the products in the basket

### Response

    Status: 200 OK
    196.48

## Add Number Of Products To Basket

### Request

`PATCH /products/:id/add`

Add the given number of the specified product to the basket. If the product is not already in the basket it will be added.

Body

    json
    {
        "productId": 1,
        "productCount": 5
    }

### Response

    Status: 200 OK
    {
        "id": 1,
        "goodIdToBasketItemMap": {
            "1": {
                "id": 1,
                "productId": 1,
                "product": {
                    "id": 1,
                    "name": "Video Game",
                    "price": 45.50
                },
                "productCount": 8,
                "basketId": 1
            },
            "2": {
                "id": 2,
                "productId": 2,
                "product": {
                    "id": 2,
                    "name": "Mug",
                    "price": 14.99
                },
                "productCount": 2,
                "basketId": 1
            },
            "3": {
                "id": 3,
                "productId": 3,
                "product": {
                    "id": 3,
                    "name": "T-Shirt",
                    "price": 30.00
                },
                "productCount": 1,
                "basketId": 1
            }
        }
    }

## Reduce Number Of Products In Basket

### Request

`PATCH /products/:id/add`

Remove the given number of the specified product from the basket. If number of products is greater than or equal to the number in the basket that product will be removed.

Body

    json
    {
        "productId": 1,
        "productCount": 5
    }

### Response

    Status: 200 OK
    {
        "id": 1,
        "goodIdToBasketItemMap": {
            "1": {
                "id": 1,
                "productId": 1,
                "product": {
                    "id": 1,
                    "name": "Video Game",
                    "price": 45.50
                },
                "productCount": 8,
                "basketId": 1
            },
            "2": {
                "id": 2,
                "productId": 2,
                "product": {
                    "id": 2,
                    "name": "Mug",
                    "price": 14.99
                },
                "productCount": 2,
                "basketId": 1
            },
            "3": {
                "id": 3,
                "productId": 3,
                "product": {
                    "id": 3,
                    "name": "T-Shirt",
                    "price": 30.00
                },
                "productCount": 1,
                "basketId": 1
            }
        }
    }

# Orders

## Create Order From Basket

### Request

`POST /orders/`

Create a new order from the given basket and delete the basket.

Body

    json
    {
        "basketId": 1,
        "name": "New name",
        "address": "new address"
    }

### Response

    Status: 201 Created
    {
        "id": 3,
        "orderItems": [
            {
                "id": 6,
                "productId": 1,
                "product": {
                    "id": 1,
                    "name": "Video Game",
                    "price": 45.50
                },
                "productCount": 8,
                "orderId": 3
            },
            {
                "id": 7,
                "productId": 3,
                "product": {
                    "id": 3,
                    "name": "T-Shirt",
                    "price": 30.00
                },
                "productCount": 1,
                "orderId": 3
            },
            {
                "id": 8,
                "productId": 2,
                "product": {
                    "id": 2,
                    "name": "Mug",
                    "price": 14.99
                },
                "productCount": 2,
                "orderId": 3
            }
        ],
        "status": "PREPARING",
        "name": "New name",
        "address": "new address"
    }

## Get Order For Id

### Request

`GET /orders/1`

Get Order for the given ID

### Response

    Status: 200 OK
    {
        "id": 1,
        "orderItems": [
            {
                "id": 1,
                "productId": 1,
                "product": {
                    "id": 1,
                    "name": "Video Game",
                    "price": 45.50
                },
                "productCount": 3,
                "orderId": 1
            },
            {
                "id": 3,
                "productId": 3,
                "product": {
                    "id": 3,
                    "name": "T-Shirt",
                    "price": 30.00
                },
                "productCount": 1,
                "orderId": 1
            },
            {
                "id": 2,
                "productId": 2,
                "product": {
                    "id": 2,
                    "name": "Mug",
                    "price": 14.99
                },
                "productCount": 2,
                "orderId": 1
            }
        ],
        "status": "SHIPPED",
        "name": "Luke Waldron",
        "address": "12 Nice Street, The Big City, Smallland, 1027B2"
    }

## Get Total Price For Order Id

### Request

`GET /orders/1/total`

Get the price of all the products in the Order

### Response

    Status: 200 OK
    196.48