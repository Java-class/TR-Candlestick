
# Candlestick Computer

The Spring Boot based Application for Compute and manage Canldestick based on WebSocket Partnet Service.


## Table of Content
- Introduction
- System Design
- Redis Structure
- Compute Algorithm
- Tech Stack
- API Reference
- Authors
- Social Media Links
- Future Development
## Introduction
The Candlestick System is a powerful tool designed to compute instrument data and quote prices in real-time using WebSocket technology, implemented in Java with Spring Boot framework and integrated with a Redis database.

At its core, the Candlestick System leverages the WebSocket protocol to establish a bi-directional communication channel between the server and the client. This enables efficient and instantaneous transmission of instrument and quote data, ensuring that users receive up-to-date information without any significant delays.

The system is developed using Java, a robust and widely-used programming language known for its scalability and versatility. Java's object-oriented nature allows for the creation of modular and maintainable code, making it an ideal choice for building complex systems like the Candlestick System.

Spring Boot, a popular Java framework, is utilized to provide a streamlined development experience. It offers a wide range of features, such as dependency injection, auto-configuration, and integrated testing capabilities. With Spring Boot, developers can rapidly build and deploy WebSocket-based applications, reducing development time and effort.

To store and retrieve instrument and quote data efficiently, the Candlestick System employs Redis, an in-memory data structure store. Redis provides high-performance data storage and retrieval capabilities, making it ideal for real-time applications. By leveraging Redis, the system can handle large volumes of data and deliver real-time updates to clients with minimal latency.

The Candlestick System computes candlestick charts, a popular visual representation of financial instrument price movements over time. It analyzes incoming quote data, aggregates it into fixed time intervals (such as minutes or hours), and generates candlestick data points. These points contain information such as the opening price, closing price, highest price, and lowest price within the specified time interval.

The system continuously updates the candlestick charts as new quote data arrives, ensuring that users have access to the most recent and accurate representation of price movements. Users can subscribe to specific instruments or customize their data feeds based on their preferences. The Candlestick System allows for seamless integration with other financial applications, enabling traders, analysts, and investors to make informed decisions based on real-time market data.

In summary, the Candlestick System is a robust and scalable solution for computing instrument and quote prices over WebSocket using Java, Spring Boot, and Redis. It provides real-time updates, allowing users to analyze price movements and make informed decisions. With its efficient architecture and powerful features, the Candlestick System is a valuable tool for anyone involved in financial markets.
## System Design

![img.png](img.png)


## Redis Structure
All Incoming Data including both Instrument and Quote are stored in Redis DB.
- InstrumentHash 
  - is an entity for store Instrument which has an Identifier based on ISIN
  - Also, the description stored in Instrument Hash.
- QuoteHash
  - is an entity to store incoming Quote data and has an UUID Random Identifier
  - Indexed with Instrument ISIN
  - store price
  - timestamp received
  - time chunk
- Candlestick Hash
  - is an entity to store computed candlestick data based on instrument and stored quote data
  - UUID Random Identifier
  - ISIN
  - time chunk
  - open price timestamp (first incoming quote in time chunk)
  - open price
  - high price
  - low price
  - close price timestamp (last incoming quote in time chunk)
  - compute timestamp (time of compute candlestick)

## Compute Algorithm
## Tech Stack
- Java 17
- Sprin Boot 3.1.0
- Maven
- Redis DB
- Test Container

## API Reference

#### Get Candlestick history

```http
  GET /candlesticks
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `isin` | `string` | **Required**. Instrument Identifier |



## Authors

- [@Mostafa Anbarmoo](https://www.github.com/java-class)


## 🔗 Social Media Links
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/mostafa-anbarmoo)


## Future Development