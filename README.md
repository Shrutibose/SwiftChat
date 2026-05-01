# SwiftChat 💬

A production-grade real-time chat application built with **Spring Boot**, **WebSocket**, **STOMP**, **Redis**, and **JWT Authentication**. SwiftChat supports one-to-one messaging, group chats, user blocking, and online presence tracking - designed with a focus on performance, scalability, and clean backend architecture.

---

## Features

- **Real-time Messaging** — One-to-one chat powered by WebSocket and STOMP protocol
- **Group Chat** — Create groups, add members, and send messages to the entire group in real time
- **JWT Authentication** — Stateless, token-based security using symmetric HS256 signing
- **User Registration & Login** — Credentials stored and validated via Redis
- **Block / Unblock Users** — Prevents blocked users from sending messages
- **Online User Tracking** — Real-time visibility of currently logged-in users
- **Chat History** — Full message history retrieval for both one-to-one and group conversations
- **Logout** — Clears session state from Redis

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Framework | Spring Boot 3.x |
| Real-time Communication | WebSocket + STOMP |
| In-memory Data Store | Redis |
| Authentication | JWT (JSON Web Token) |
| Security | Spring Security |
| Language | Java 21 |
| Build Tool | Maven |

---

## Architecture

```
Client
  │
  ├── HTTP (REST)         →  AuthController, UserController, GroupController
  │                              ↓
  │                          UserService / GroupService / MessageService
  │                              ↓
  │                            Redis (stores users, messages, groups)
  │
  └── WebSocket (STOMP)   →  ChatWebSocketController
                                 ↓
                          SimpMessagingTemplate
                                 ↓
                       /topic/messages/{user}        ← 1-to-1
                       /topic/group/{groupName}      ← group chat
```

---

## Security Flow

```
POST /auth/register  →  Store credentials in Redis
POST /auth/login     →  Validate credentials → Generate JWT token
Every other request  →  JwtFilter validates token → SecurityContextHolder
```

The `JwtFilter` extends `OncePerRequestFilter` and intercepts every incoming request. It reads the `Authorization: Bearer <token>` header, validates the token using the secret key in `JwtUtil`, and sets the authenticated user in Spring Security's `SecurityContextHolder`.

---

## Project Structure

```
src/
├── controller/
│   ├── AuthController.java       # /auth/login, /auth/register
│   ├── UserController.java       # /online-users, /logout, /block, /unblock
│   └── GroupController.java      # /group/create, /add-member, /send, /messages
├── service/
│   ├── UserService.java          # Registration, login, block logic
│   ├── MessageService.java       # 1-to-1 message send/receive
│   └── GroupService.java         # Group creation, membership, messaging
├── security/
│   ├── JwtUtil.java              # Token generation, extraction, validation
│   ├── JwtFilter.java            # OncePerRequestFilter implementation
│   └── SecurityConfig.java       # Spring Security configuration
├── websocket/
│   └── ChatWebSocketController.java  # Handles /app/send and /app/group/send
├── config/
│   └── WebSocketConfig.java      # STOMP endpoint and broker configuration
├── model/
│   └── ChatMessage.java
└── dto/
    ├── ChatResponse.java
    ├── ChatListResponse.java
    └── GroupMessage.java
```

---

## API Endpoints

### Auth (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT token |

### User (Protected)
| Method | Endpoint | Description |
|---|---|---|
| GET | `/online-users` | Get all currently online users |
| POST | `/logout` | Logout user |
| POST | `/block` | Block a user |
| POST | `/unblock` | Unblock a user |

### Messages (Protected)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/send` | Send a message (REST) |
| GET | `/receive` | Get full chat history |
| GET | `/chats` | Get chat list for a user |

### Group (Protected)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/group/create` | Create a new group |
| POST | `/group/add-member` | Add a member to a group |
| GET | `/group/members` | Get all group members |
| POST | `/group/send` | Send a message to a group |
| GET | `/group/messages` | Get all messages in a group |

### WebSocket
| Destination | Description |
|---|---|
| `/app/send` | Send a 1-to-1 real-time message |
| `/app/group/send` | Send a real-time group message |
| `/topic/messages/{username}` | Subscribe to receive 1-to-1 messages |
| `/topic/group/{groupName}` | Subscribe to receive group messages |

---

## Redis Data Model

```
user:{username}              →  password (String)
login:{username}             →  "true" (String)
blocked:{username}           →  Set of blocked usernames
chat:{user1}:{user2}         →  List of JSON messages
group:{groupName}            →  Set of member usernames
groupchat:{groupName}        →  List of JSON group messages
```

---

## Getting Started

### Prerequisites
- Java 21
- Maven
- Redis (running on localhost:6379)

### Run Locally

```bash
# Clone the repository
git clone https://github.com/Shrutibose/SwiftChat.git
cd SwiftChat

# Start Redis
redis-server

# Run the application
./mvnw spring-boot:run
```

The server starts on `http://localhost:8081`

---

## Design Decisions

- **Redis over a traditional database** — Chosen for its speed and simplicity for session-based data like online presence and chat history. In a production system, persistent messages would be backed up to a relational database.
- **Symmetric JWT (HS256)** — Appropriate for a single-server architecture. For multi-server deployments, asymmetric signing (RS256) would be preferred.
- **STOMP over raw WebSocket** — Provides built-in pub/sub semantics, making it easier to route messages to specific users and groups without custom routing logic.
- **OncePerRequestFilter** — Ensures the JWT validation runs exactly once per request, avoiding duplicate processing in the Spring Security filter chain.

---

## What I Learned

Building SwiftChat gave me practical experience with:
- Designing stateless authentication systems using JWT
- Understanding Spring Security's filter chain and how to plug in custom filters
- Real-time communication patterns using WebSocket and STOMP
- Using Redis as both a session store and a message store
- Structuring a Spring Boot backend with clear separation of concerns across controllers, services, and security layers

---

*Built as part of a backend engineering learning journey — focused on writing systems that are clean, scalable, and production-aware.*
