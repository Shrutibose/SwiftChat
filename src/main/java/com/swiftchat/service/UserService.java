package com.swiftchat.service;

import com.swiftchat.security.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final JwtUtil jwtUtil;

    private final StringRedisTemplate redis;

    public UserService(JwtUtil jwtUtil, StringRedisTemplate redis) {
        this.jwtUtil = jwtUtil;
        this.redis = redis;
    }

    // REGISTER
    public String register(String username, String password) {

        String key = "user:" + username;

        if (redis.hasKey(key)) {
            return "User already exists";
        }

        redis.opsForValue().set(key, password);
        return "User registered successfully";
    }

    // LOGIN
    public String login(String username, String password) {

        String key = "user:" + username;

        String storedPassword = redis.opsForValue().get(key);

        if (storedPassword == null) {
            return "User not found. Please register.";
        }

        if (!storedPassword.equals(password)) {
            return "Wrong password, try again.";
        }

        // mark user as logged in
        redis.opsForValue().set("login:" + username, "true");

        // generate JWT token
        String token = jwtUtil.generateToken(username);

        return token;
    }

    //gives online users as list
    public List<String> getOnlineUsers() {

        Set<String> keys = redis.keys("login:*");

        List<String> users = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                String username = key.replace("login:", "");
                users.add(username);
            }
        }

        return users;
    }

    //logged out users
    public String logout(String username) {

        String key = "login:" + username;

        if (!redis.hasKey(key)) {
            return "User already logged out";
        }

        redis.delete(key);

        return "Logout successful";
    }
}

/* When a user tries to register, the request goes to the /register API in the UserController, where it accepts the username and password as parameters.
The controller then passes this data to the register() method in UserService.
Inside the service, a unique key is created in the format user:username.
It checks if this key already exists in Redis. If it does, it returns "User already exists".
If not, it stores the username and password in Redis using that key, and then returns "User registered successfully"
 */

/* When a user is trying to login, the request hits the /login API in the controller with username and password.
The controller extracts those values and calls the service class, passing the data to the login() method.
The system then generates a key in the format user:username and tries to fetch the stored password from Redis.
Now there are 3 cases:
Case I – Login successful: the user exists and the password matches, so the system stores login:username = true in Redis then generated a JWWT token and returns that.
Case II – Wrong password: the user exists but the password does not match, so it returns "Wrong password".
Case III – User not found: no data is found in Redis for that key, so it returns "User not found".
 */

/*When the system wants to see the users who are online, the request first goes to the /online-users API in the controller.
The controller then calls the getOnlineUsers() method in the service.
The service fetches all the logged-in user keys from Redis using the pattern login:* and creates an empty list.
It then iterates over each key, removes the login: prefix to extract the username, and adds those usernames to the list.
Finally, it returns the list of online users to the controller, which sends it back to the frontend. */

/* When a user tries to log out from the system, the request hits the /logout API in the controller.
The controller extracts the username and calls the service method, passing that data.
The service method generates a key in the format login:username and checks if the key is present in Redis.
If the key exists, the system deletes it and returns "Logout successful".
If the key does not exist, it returns "User already logged out".
 */