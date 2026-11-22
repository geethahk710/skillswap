package com.skillswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class SkillRequestController {

    @Autowired
    private SkillRequestRepository requestRepository;

    // SEND SKILL EXCHANGE REQUEST
    @PostMapping("/send")
    public ResponseEntity<?> sendRequest(
            @RequestBody SkillRequest request,
            HttpSession session) {

        String senderEmail =
                (String) session.getAttribute("USER_EMAIL");

        if (senderEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login first"));
        }

        if (request.getReceiverEmail() == null
                || request.getReceiverEmail().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Receiver email is required"));
        }

        if (senderEmail.equalsIgnoreCase(request.getReceiverEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "You cannot send a request to yourself"));
        }

        request.setSenderEmail(senderEmail);
        request.setStatus("PENDING");

        requestRepository.save(request);

        return ResponseEntity.ok(
                Map.of("message", "Skill exchange request sent successfully!")
        );
    }


    // VIEW RECEIVED REQUESTS
    @GetMapping("/received")
    public ResponseEntity<?> receivedRequests(HttpSession session) {

        String email =
                (String) session.getAttribute("USER_EMAIL");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login first"));
        }

        List<SkillRequest> requests =
                requestRepository.findByReceiverEmail(email);

        return ResponseEntity.ok(requests);
    }


    // VIEW SENT REQUESTS
    @GetMapping("/sent")
    public ResponseEntity<?> sentRequests(HttpSession session) {

        String email =
                (String) session.getAttribute("USER_EMAIL");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login first"));
        }

        List<SkillRequest> requests =
                requestRepository.findBySenderEmail(email);

        return ResponseEntity.ok(requests);
    }


    // ACCEPT OR REJECT REQUEST
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRequest(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession session) {

        String email =
                (String) session.getAttribute("USER_EMAIL");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please login first"));
        }

        SkillRequest request =
                requestRepository.findById(id).orElse(null);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Request not found"));
        }

        // Only the receiver can update the request
        if (!email.equalsIgnoreCase(request.getReceiverEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You cannot update this request"));
        }

        if (!"ACCEPTED".equalsIgnoreCase(status)
                && !"REJECTED".equalsIgnoreCase(status)) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request status"));
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "This request has already been processed"));
        }

        request.setStatus(status.toUpperCase());
        requestRepository.save(request);

        return ResponseEntity.ok(
                Map.of("message", "Request updated successfully!")
        );
    }
}