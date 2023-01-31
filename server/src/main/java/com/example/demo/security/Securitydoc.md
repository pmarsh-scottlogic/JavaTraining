# 🔑 Peter's Spring Security Doc  🔐
This documentation summarizes my understanding of Spring security, and how all the components in my system fit together with the stuff provided by the framework.

## Authentication journey from start to finish 🚌
Names marked with an asterisk (\*) are things provided by Spring security, and not made by me.
### Initialisation
At the start of the program, `ApplicationSecurity` gets going, configuring the `AuthenticationManager`\* to be able to access the database based on a username. We also configure `HttpSecurity`\*; we secure all endpoints apart from `auth/login`, disable a few protections that mess with the app, determine how to handle authentication exceptions and add our custom `JwtTokenFilter` to Spring's filter chain. Next we expose our `PasswordEncoder` and `AuthenticationManager`\* `@Beans`.

Our H2-database is initialized with tables determined by the `AppUser` and `Role` `@Entity` classes. `RoleRepo` and `UserRepo` expose methods for querying the database.

The `@Bean` `CommandLineRunner`runs once, populating our database with hardcoded users and roles.

### User logs in

The client posts a JSON object containing a user's username and password to `auth/login`.

Upon the port receiving the request, we bypass `HttpSecurity`\* (having configured it to not secure this endpoint), and the request hits the endpoint.

To check the user's credentials, we look to `AuthenticationManager`\* (which we configured at the start). We call its `authenticate()`\* method, passing it a `new UsernameAndPasswordAuthenticationToken`\*, with the username and password taken from the request. It does some black box Spring magic ✨ to determine if this is a real user. If this fails, we return an *unauthorised* status. If it succeeds, we call on our `jwtTokenUtil` class to generate us a JWT and send it back to the client.

### Authorised User Accesses API 

The client makes any http request to the server, but this time it includes the JWT as part of the request. Specifically, in the token headers we have the field called `Authorization`, which contains the text "Bearer [token string]".

Upon the port receiving the request, it gets pulled into the filter chain by `HttpSecurity`\* where it hits our `JwtTokenFilter` and the method `doFIlterInternal` is invoked, performing the following:

- Ensure that request has the Authorization field in its header. If not, we continue along the chain without authorizing the request.
- Ensure that the token is valid (correct format, and that the signature is correct given the header, payload and secrets). If not, we continue along the chain without authorizing the request. If valid, we call `setAuthenticationContext()` which, through some black box Spring magic✨ means that the request can get through to the endpoint.

At the endpoint, relevant logic is executed and a sensible response is sent  to the client.


## Misc Concepts

### [Java Web Token (JWT)](https://jwt.io/introduction)
Upon authenticating with the server, the server returns a JWT to the client. This token is then included in any further http requests via the authorization header.

The java web token has three parts:
- Header | typically contains the token type (jwt) and the hashing algorithm.
- Payload | contains a list of claims e.g. issuer, expiration time, subject, audience.
- Signature | calculated by encoding the above two sections, then hashing them along with a secret key.

The token is formed by stringing together the encoded header, the encoded payload and the signature.

An example token could look like this:


> eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
> .eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
> .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c




## My classes 📁

*authInfo
├── AuthRequest
├── AuthResponse
Configuration
├──ApplicationSecurity
Controller
├──AuthController
filter
├──JwtTokenFilter
repo
├──RoleRepo
├──UserRepo
service
├──MyUserService
token
├──JwtTokenUtil
userInfo
├──AppUser
├──Role*


## AuthRequest
A POJO that stores a username and password. It is the type of the `/auth/login` post request body. It has validation on these properties so that when the post request comes in we can check that the credentials are valid.

## AuthResponse
A POJO that carries a username and accessToken. When the `/auth/login` endpoint returns, the body is of this type. The accessToken is then used to authenticate further api calls.

## AppUser
Tagged with `@Entity` which tells JPA that this POJO represents data that can be persisted to a database. An entity class represents a table, and each instance of the class is a new row in the table.

POJO that holds information of a particular user, in particular a list of roles, which we tag with `@ManyToMany` to tell JPA that we have a many-to-many relationship between users and roles.

We implement `UserDetails` for reasons to be determined...

## Role
Tagged with `@Entity` which tells JPA that this POJO represents data that can be persisted to a database. An entity class represents a table, and each instance of the class is a new row in the table.

## UserRepo
A `@repo` interface that extends `JpaRepository<AppUser, Long>`.  AppUser is the type that is being stored in the repo. We then selectively expose [certain methods](https://www.baeldung.com/spring-data-derived-queries) by simply declaring methods of the right signature.

In this case we expose `findByUsername()`, which we call in `MyUserService`'s `getUser()` method.

## RoleRepo
A `@repo` interface that extends `JpaRepository<Role, Long>`.  AppUser is the type that is being stored in the repo. We then selectively expose [certain methods](https://www.baeldung.com/spring-data-derived-queries) by simply declaring methods of the right signature.

In this case we expose `findByName()`, which we call in `MyUserService`'s `getUser()` method.


## AuthController 🎮

 **Dependencies:**
- [AuthenticationManager](https://www.baeldung.com/spring-security-authenticationmanagerresolver#:~:text=What%20Is%20the%20AuthenticationManager?,authenticated%20flag%20set%20to%20true.)
- `JwtTokenUtil`

### Job:
Provides the endpoint for `/auth/login`. We receive a post request with a username and password (body of type `AuthRequest`).

Credentials are checked by `authenticationManager`, via its `authenticate` method.

If username and password are accepted, the we return an `AuthResponse` with the username and JWT

## ApplicationSecurity 📋
This is the big boy that sets up all the configuration.

**Dependencies:**
- `UserRepo`
- `jwtTokenFilter`

### Job:

Configures Spring security to do its job via the `configure` methods.

One `configure` method sets up the `AuthenticationManagerBuilder`'s `userDetailsService` with a custom method to retrieve a user by username from storage.

The other `configure` method sets up the `HttpSecurity` object. In particular we decide which endpoints should be secured, and add our custom filter, `JwtTokenFilter`, into the Spring filter chain.

We also define `@Bean`s that provide 1) a `PasswordEncoder` to Spring (for use in `UserService` so that we can encode passwords before adding the to our database.) and 2) the `AuthenticationManager` that we configured earlier.

## JwtTokenFilter 🧪
**Dependencies:**
- JwtTokenUtil

### Job:
Is entered into the Spring filter chain to check JWTs.

Overrides OncePerRequestFilter. This means that the `doFilterInternal` method runs exactly once per incoming http request.

If there is no token supplied, or the token is invalid (bad format, or the signature is incorrect), then we continue along the filter chain. If the token is supplied and valid, then we `setAuthenticationContext` and continue down the chain.

## MyUserService
Implements `UserService` (my own interface) `UserDetailsService`.

**Dependencies:**
- `UserRepo`
- `RoleRepo`
- `PasswordEncoder`

### Job:
This service is for interacting with the database. It does this via the `UserRepo` and `RoleRepo` interfaces, along with Spring JPA which handles the actual database stuff. It has methods for adding and retrieving users and roles to the database.

## JwtTokenUtil
Has constants for token expiry duration, the signing secret key and the hashing algorithm.

### Job:
Generate a JWT for a given user, via the JWT class's builder pattern.

Validate JWTs (check correct format and that signature is correct). We do this via `JWTVerifier.BaseVerification` and its builder pattern. We throw various errors depending on the outcome.

Get the subject from a JWT.

# Weirdo Things Provided by Spring
## [AuthenticationManager](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AuthenticationManager.html)

This, to me, is an enigma. But what it does have, is the handy authenticate() method. You pass it a `UsernamePasswordAuthenticationToken`, and it works its Spring magic behind the scenes to access the repo and check credentials.

## [HttpSecurity](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)

Head honcho of the whole security system. Configure this to set which endpoints are secured, which filters are in the filter chain, how to deal with authentication exceptions and more!
