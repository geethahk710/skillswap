package com.skillswap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillRequestRepository
        extends JpaRepository<SkillRequest, Long> {

    List<SkillRequest> findByReceiverEmail(String receiverEmail);

    List<SkillRequest> findBySenderEmail(String senderEmail);
}