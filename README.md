# Login-Registration

A backend API for user login and registration with JWT authentication and WebSocket functionality.

## Description

This project provides a backend API for user authentication and registration using JSON Web Tokens (JWT). It also includes basic WebSocket functionality for real-time communication.

## Features

- User registration
- User login/logout
- Update user data
- Get registered users information
- JWT authentication
- WebSocket support for real-time messaging

## Usage

To register a new user:

```bash
curl -X POST http://127.0.0.1:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName": "example", "email": "example@gmail.com", "password": "password", "avatar": "image_base_64"}'
