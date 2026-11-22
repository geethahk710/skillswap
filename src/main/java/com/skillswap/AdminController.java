package com.skillswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Value("${skillswap.admin.username}")
    private String adminUsername;

    @Value("${skillswap.admin.password}")
    private String adminPassword;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRequestRepository requestRepository;


    // ADMIN LOGIN
    @PostMapping("/login")
    public String adminLogin(
            @RequestBody Map<String, String> details,
            HttpSession session) {

        String username = details.get("username");
        String password = details.get("password");

        if (Objects.equals(adminUsername, username)
                && Objects.equals(adminPassword, password)) {

            session.setAttribute("ADMIN_LOGGED_IN", true);

            return "Admin login successful!";
        }

        throw new RuntimeException(
                "Invalid admin username or password!");
    }


    // CHECK ADMIN SESSION
    private boolean isAdmin(HttpSession session) {

        return Boolean.TRUE.equals(
                session.getAttribute("ADMIN_LOGGED_IN"));
    }


    // CHECK ADMIN AUTHENTICATION
    @GetMapping("/check")
    public Map<String, Object> checkAdmin(
            HttpSession session) {

        return Map.of(
                "authenticated",
                isAdmin(session)
        );
    }


    // VIEW ALL REGISTERED USERS
    @GetMapping("/users")
    public Object getAllUsers(HttpSession session) {

        if (!isAdmin(session)) {
            return Map.of("error", "Unauthorized access");
        }

        List<Map<String, Object>> users =
                userRepository.findAll().stream().map(user -> {

                    Map<String, Object> details =
                            new LinkedHashMap<>();

                    details.put("id", user.getId());
                    details.put("name", user.getName());
                    details.put("email", user.getEmail());
                    details.put("skillToTeach",
                            user.getSkillToTeach());
                    details.put("skillToLearn",
                            user.getSkillToLearn());

                    return details;

                }).toList();

        return users;
    }


    // VIEW ALL SKILL EXCHANGE REQUESTS
    @GetMapping("/requests")
    public Object getAllRequests(HttpSession session) {

        if (!isAdmin(session)) {
            return Map.of("error", "Unauthorized access");
        }

        return requestRepository.findAll();
    }


    // DELETE A USER
    @DeleteMapping("/users/{id}")
    public Object deleteUser(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdmin(session)) {
            return Map.of("error", "Unauthorized access");
        }

        if (!userRepository.existsById(id)) {
            return Map.of("error", "User not found");
        }

        userRepository.deleteById(id);

        return Map.of(
                "message",
                "User deleted successfully"
        );
    }


    // ADMIN UPDATE REQUEST
    @PutMapping("/requests/{id}")
    public Object updateRequest(
            @PathVariable Long id,
            @RequestBody Map<String, String> details,
            HttpSession session) {

        if (!isAdmin(session)) {
            return Map.of("error", "Unauthorized access");
        }

        var request =
                requestRepository.findById(id).orElse(null);

        if (request == null) {
            return Map.of("error", "Request not found");
        }

        String status = details.get("status");

        if (!"ACCEPTED".equals(status)
                && !"REJECTED".equals(status)
                && !"PENDING".equals(status)) {

            return Map.of("error", "Invalid status");
        }

        request.setStatus(status);
        requestRepository.save(request);

        return Map.of(
                "message",
                "Request updated successfully"
        );
    }


    // ADMIN LOGOUT
    @PostMapping("/logout")
    public String adminLogout(HttpSession session) {

        session.invalidate();

        return "Admin logged out successfully!";
    }

}
