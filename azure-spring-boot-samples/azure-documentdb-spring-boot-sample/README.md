## Overview
This sample project demonstrates how to use Azure DocumentDB via Spring Boot Starter `azure-documentdb-spring-boot-starter` to store data in and retrieve data from your Azure Cosmos DB by using the DocumentDB API.

## Prerequisites

* An Azure subscription; if you don't already have an Azure subscription, you can activate your [MSDN subscriber benefits](https://azure.microsoft.com/en-us/pricing/member-offers/msdn-benefits-details/) or sign up for a [free Azure account](https://azure.microsoft.com/en-us/free/).

* A [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/), version 1.8.

* [Apache Maven](http://maven.apache.org/), version 3.0 or later.

## Quick Start

### Create an Azure Cosmos DB on Azure

1. Go to [Azure portal](https://portal.azure.com/) and click +New .
2. Click Databases, and then click Azure Cosmos DB to create your database. 
3. Navigate to the database you have created, and click Access keys and copy your URI and access keys for your database.
                                                                                                                                  
### Config the sample

1. Navigate to `src/main/resources` and open `application.properties`.
2. Replace the `DNS URI`, `Access Key` and `Name` with information of your database 

### Run the sample

1. Change directory to folder `azure-documentdb-spring-boot-starter-sample`.
2. Run below commands. 

```
mvn package
java -jar target/azure-ducumentdb-spring-boot-starter-sample-0.0.1-SNAPSHOT.jar
```
### Known issue

Directly running the sample app from IDE IntelliJ or Eclipse has below security exception if using the *released* starter. The root cause
 is that the release `spring-data-azure-documentdb` jar is code-signed by us. We're working actively to resolve this issue. 

```
Caused by: java.lang.SecurityException: class "com.microsoft.azure.sample.User_Accessor_yhb3bq"'s signer information does not match signer information of other classes in the same package
	at java.lang.ClassLoader.checkCerts(ClassLoader.java:898) ~[na:1.8.0_131]
	at java.lang.ClassLoader.preDefineClass(ClassLoader.java:668) ~[na:1.8.0_131]
	at java.lang.ClassLoader.defineClass(ClassLoader.java:761) ~[na:1.8.0_131]
```

### More details

Please refer to [this article](https://docs.microsoft.com/en-us/azure/cosmos-db/documentdb-java-spring-boot-starter-with-cosmos-db) for the toturial about how to use the Spring Boot Starter with Azure Cosmos DB DocumentDB API.

