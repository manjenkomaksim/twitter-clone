# 🐦 Twitter Clone API

A simple Twitter-like REST API built with Spring Boot, MongoDB, and WebSocket support.

## 🚀 Features

- User authentication with JWT (register, login)
- Create, update, delete posts
- Like/unlike posts
- Comment on posts
- Follow/unfollow users
- Real-time feed updates via WebSocket
- Full integration test suite
- Postman collection included

## 📦 Stack

- Groovy
- Spring Boot
- Spring Security (JWT)
- MongoDB
- WebSockets (STOMP)
- Spock for testing

## ⚙️ Getting Started

```bash
git clone
```
-------
! Create .env file with JWT secret and add it to App Run configuration

-------
```bash
cd twitter-clone

./gradlew build

docker-compose up -d

./gradlew bootRun
```

# 📪 Postman API Collection
In the root of the project:
[twitter-clone-api.postman_collection.json](https://github.com/manjenkomaksim/twitter-clone/blob/master/twitter-clone-api.postman_collection.json)

! No environments or variables needed — just paste the JWT token manually into the Authorization header.

# 📬 WebSocket Feed
Use HTML file in the root of the project to test WS: 
[websocket-test.html](https://github.com/manjenkomaksim/twitter-clone/blob/master/websocket-test.html)


