package com.skillswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    // REGISTER USER
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if (user.getEmail() == null || user.getPassword() == null
                || user.getEmail().isBlank()
                || user.getPassword().isBlank()) {
            throw new RuntimeException(
                    "Email and password are required!");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException(
                    "Email already registered!");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);

        return "Registration successful!";
    }

    // LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody Map<String, String> details,
            HttpServletRequest request) {

        String email = details.get("email");
        String password = details.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password!");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password!");
        }

        String storedPassword = user.getPassword();

        boolean passwordMatches;

        if (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {

            passwordMatches =
                    encoder.matches(password, storedPassword);

        } else {

            passwordMatches = password.equals(storedPassword);

            if (passwordMatches) {
                user.setPassword(encoder.encode(password));
                userRepository.save(user);
            }
        }

        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password!");
        }

        HttpSession oldSession = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = request.getSession(true);

        session.setAttribute("USER_EMAIL", user.getEmail());

        return ResponseEntity.ok("Login successful!");
    }

    // GET USER PROFILE
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {

        String email =
                (String) session.getAttribute("USER_EMAIL");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login first"));
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        Map<String, Object> profile = new LinkedHashMap<>();

        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("skillToTeach", user.getSkillToTeach());
        profile.put("skillToLearn", user.getSkillToLearn());

        return ResponseEntity.ok(profile);
    }

    // CHECK USER LOGIN
    @GetMapping("/check")
    public Map<String, Boolean> checkLogin(HttpSession session) {

        boolean loggedIn =
                session.getAttribute("USER_EMAIL") != null;

        return Map.of("authenticated", loggedIn);
    }

    // LOGOUT USER
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "Logged out successfully!";
    }

    // UPDATE USER PROFILE
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> details,
            HttpSession session) {

        String email =
                (String) session.getAttribute("USER_EMAIL");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login first"));
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        String name = details.get("name");
        String skillToTeach = details.get("skillToTeach");
        String skillToLearn = details.get("skillToLearn");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Name is required"));
        }

        user.setName(name);
        user.setSkillToTeach(skillToTeach);
        user.setSkillToLearn(skillToLearn);

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message",
                        "Profile updated successfully!")
        );
    }

    // SEARCH USERS BY SKILL
    @GetMapping("/search")
    public List<Map<String, Object>> searchUsers(
            @RequestParam String skill) {

        List<User> users =
                userRepository.findBySkillToTeachContainingIgnoreCase(
                        skill);

        List<Map<String, Object>> results = new ArrayList<>();

        for (User user : users) {

            Map<String, Object> details =
                    new LinkedHashMap<>();

            details.put("id", user.getId());
            details.put("name", user.getName());
            details.put("email", user.getEmail());
            details.put("skillToTeach",
                    user.getSkillToTeach());
            details.put("skillToLearn",
                    user.getSkillToLearn());

            results.add(details);
        }

        return results;
    }
}
