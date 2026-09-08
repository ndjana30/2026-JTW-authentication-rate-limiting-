# REST API Documentation
## JTW Authentication & Rate Limiting System

**Date Generated:** 2026-09-08  
**Repository:** ndjana30/2026-JTW-authentication-rate-limiting-  
**Branch:** mlink  
**Language:** Java (96.4%) - Spring Boot Application

---

## Table of Contents
1. [Overview](#overview)
2. [Authentication Endpoints](#authentication-endpoints)
3. [Posts/Profile Endpoints](#postprofile-endpoints)
4. [Test Endpoints](#test-endpoints)
5. [Response Status Codes](#response-status-codes)
6. [Security Requirements](#security-requirements)

---

## Overview

This document provides comprehensive documentation for all REST API endpoints decorated with `@GetMapping` and `@PostMapping` annotations in the mlink branch of the authentication and rate limiting system.

**Total Endpoints:** 7
- GET Mappings: 2
- POST Mappings: 5

**Base URL:** `/api/v1/`

---

## Authentication Endpoints

### Base Path: `/api/v1/auth`

#### 1. Register User
- **Method:** `POST`
- **Endpoint:** `/api/v1/auth/register`
- **Handler:** `AuthenticationController.register(RegisterRequest request)`
- **Description:** Registers a new user in the system
- **Request Body:** 
  ```json
  {
    "firstname": "string",
    "lastname": "string",
    "email": "string",
    "password": "string",
    "phoneNumber":"String"
  }
  ```
- **Response:** `AuthenticationResponse` (200 OK)
- **Response Body:**
  ```json
  {
    "token": "JWT_TOKEN_STRING"
  }
  ```
- **Error Handling:** Returns HTTP 400 if validation fails
- **Security:** No authentication required (public endpoint)

#### 2. Authenticate User
- **Method:** `POST`
- **Endpoint:** `/api/v1/auth/authenticate`
- **Handler:** `AuthenticationController.authenticate(AuthenticationRequest request)`
- **Description:** Authenticates an existing user and returns JWT token
- **Request Body:**
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```
- **Response:** `AuthenticationResponse` (200 OK)
- **Response Body:**
  ```json
  {
    "token": "JWT_TOKEN_STRING"
  }
  ```
- **Error Handling:** Returns HTTP 401 if credentials are invalid
- **Security:** No authentication required (public endpoint)

#### 3. Verify OTP
- **Method:** `POST`
- **Endpoint:** `/api/v1/auth/verify-otp`
- **Handler:** `AuthenticationController.verifyOtp(VerifyOtpRequest request)`
- **Description:** Verifies OTP for multi-factor authentication
- **Request Body:**
  ```json
  {
    "email": "string",
    "otp": "string"
  }
  ```
- **Response:** `AuthenticationResponse` (200 OK)
- **Response Body:**
  ```json
  {
    "token": "JWT_TOKEN_STRING"
  }
  ```
- **Error Handling:** Returns HTTP 401 if OTP is invalid or expired
- **Security:** No authentication required (during OTP verification flow)
- **Notes:** Part of Twilio OTP integration for additional security

---

## Posts/Profile Endpoints

### Base Path: `/api/v1/posts`

#### 4. Create Biography
- **Method:** `POST`
- **Endpoint:** `/api/v1/posts/create-bio`
- **Handler:** `Rest.createBiography(BiographieRequest biographieRequest)`
- **Description:** Creates or updates user biography
- **Request Body:**
  ```json
  {
    "text": "string (biography text)"
  }
  ```
- **Response:** HTTP 201 CREATED with message "Bio created"
- **Response Body:** `String`
- **Error Handling:** 
  - Returns HTTP 400 "User not existing" if user not found
- **Security:** Requires JWT authentication
- **Authentication Method:** Extracts from `SecurityContextHolder`
- **Notes:** Links biography to authenticated user

#### 5. Modify Profile
- **Method:** `POST`
- **Endpoint:** `/api/v1/posts/modify-profile`
- **Handler:** `Rest.modifyProfile(...)`
- **Description:** Updates user profile with picture, cover video, and musical genres
- **Request Parameters (multipart/form-data):**
  - `profile_picture` (MultipartFile) - User profile picture
  - `cover_video` (MultipartFile) - Cover video file
  - `musical_genres` (String[]) - Array of musical genre strings
- **Response:** HTTP 200 OK with message "Profile updated"
- **Response Body:** `String`
- **Error Handling:**
  - Returns HTTP 403 "User not authenticated" if authentication fails
  - Returns HTTP 200 with error message if file processing fails
- **Security:** Requires JWT authentication
- **Authentication Method:** Extracts from `SecurityContextHolder`
- **Notes:** Stores file bytes in database, accepts array of genres

#### 6. Modify Gallery
- **Method:** `POST`
- **Endpoint:** `/api/v1/posts/gallery-modify`
- **Handler:** `Rest.modifyGallery(MultipartFile[] data)`
- **Description:** Adds media files to user's gallery
- **Request Parameters (multipart/form-data):**
  - `medias` (MultipartFile[]) - Array of media files
- **Response:** HTTP 200 OK with message "Gallery Updated"
- **Response Body:** `String`
- **Error Handling:**
  - Returns HTTP 401 "Unauthorized" if not authenticated
  - Returns HTTP 403 "User not present" if user not found
  - Returns HTTP 404 "Gallery not found for user" if gallery doesn't exist
- **Security:** Requires JWT authentication
- **Authentication Method:** Extracts from `SecurityContextHolder`
- **Transactional:** Yes (`@Transactional`)
- **Notes:** Processes multiple media files in one request

#### 7. View Gallery
- **Method:** `GET`
- **Endpoint:** `/api/v1/posts/gallery-view`
- **Handler:** `Rest.ViewGallery()`
- **Description:** Retrieves all media files from user's gallery
- **Request Parameters:** None
- **Response:** HTTP 200 OK with list of media objects
- **Response Body:** `List<String>` (Array of media data as strings)
- **Error Handling:**
  - Returns HTTP 403 "User not present" if user not found
- **Security:** Requires JWT authentication
- **Authentication Method:** Extracts from `SecurityContextHolder`
- **Notes:** Converts media byte arrays to string format for response

---

## Test Endpoints

### Base Path: `/api/v1/test`

#### 8. Create Mock User (Rate Limiting Test)
- **Method:** `GET`
- **Endpoint:** `/api/v1/test/username`
- **Handler:** `Controller.createMockUser(String apiKey)`
- **Description:** Creates a mock free user and tests rate limiting using Bucket4j
- **Request Headers:**
  - `X-api-key` (Required) - API key for rate limiting resolution
- **Response:** HTTP 200 OK with username "turner"
- **Response Body:** `String` (username)
- **Response Headers:**
  - `X-Rate-Limit-Remaining` - Number of remaining tokens in bucket
  - `X-Rate-Limit-Retry-After-Seconds` - Time to wait before retry (if rate limited)
- **Rate Limiting:**
  - If limit exceeded: HTTP 429 (Too Many Requests)
  - Includes `X-Rate-Limit-Retry-After-Seconds` header with wait time
- **Security:** API key-based rate limiting
- **Notes:** Uses Bucket4j for token bucket algorithm rate limiting

---

## Response Status Codes

| Code | Status | Usage |
|------|--------|-------|
| 200 | OK | Successful GET/POST request |
| 201 | Created | Resource successfully created |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Authentication failed or missing |
| 403 | Forbidden | Access denied or user not found |
| 404 | Not Found | Resource not found |
| 429 | Too Many Requests | Rate limit exceeded |

---

## Security Requirements

### Authentication
- **Method:** JWT Bearer Token
- **Implementation:** Spring Security
- **Token Storage:** SecurityContextHolder
- **Required For:** All `/posts` endpoints and most authentication endpoints

### Authorization
- **User-Specific:** All profile/gallery operations are user-scoped via authenticated principal
- **Role-Based:** Currently email-based user identification

### Rate Limiting
- **Technology:** Bucket4j
- **Scope:** API key-based for `/test` endpoints
- **Pricing Plans:** Different rate limit buckets per pricing plan

### API Key Headers
- `X-api-key` - Required for rate limiting resolution and test endpoints

---

## Controller Classes Summary

### 1. AuthenticationController
- **Path:** `src/main/java/com/ndjana/rate/auth/AuthenticationController.java`
- **Package:** `com.ndjana.rate.auth`
- **Base URL:** `/api/v1/auth`
- **Endpoints:** 3 (register, authenticate, verify-otp)
- **Dependencies:** AuthenticationService, UserRepository, BiographieRepo

### 2. Rest (Posts Controller)
- **Path:** `src/main/java/com/ndjana/rate/posts/Rest.java`
- **Package:** `com.ndjana.rate.posts`
- **Base URL:** `/api/v1/posts`
- **Endpoints:** 4 (create-bio, modify-profile, gallery-modify, gallery-view)
- **Dependencies:** BiographieRepo, UserRepository, ProfileRepo, GalleryRepo, MediaRepository

### 3. Controller (Test Controller)
- **Path:** `src/main/java/com/ndjana/rate/rest/Controller.java`
- **Package:** `com.ndjana.rate.rest`
- **Base URL:** `/api/v1/test`
- **Endpoints:** 1 (username)
- **Dependencies:** PricingPlanService

---

## Integration Notes

### OTP Integration
- Uses Twilio for OTP generation and delivery (feature/twilio-otp-integration branch available)
- OTP verification is part of the multi-factor authentication flow

### Rate Limiting
- Integrated with Bucket4j library
- Different rate limits based on pricing plans (Free, Premium, etc.)
- Per-API-key tracking

### Database Models
- **User** - User account with authentication credentials
- **Biographie** - User biography text
- **Profile** - User profile with picture, cover video, and musical genres
- **Gallerie** - User's media gallery
- **Media** - Individual media files in gallery
- **OTP** - One-time password for MFA

---

## Version Information
- **Java Version:** Spring Boot (implied from architecture)
- **Framework:** Spring Framework with Spring Security
- **Build Tool:** Maven (pom.xml present)
- **Docker:** Dockerfile available for containerization
- **API Version:** v1

---

*End of Documentation*
