package com.vit.club_manager.repository;

import com.vit.club_manager.model.ChatMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {
    List<ChatMessageDocument> findTop50ByChannelIdOrderByTimestampDesc(Long channelId);
    // In ChatMessageRepository.java
List<ChatMessageDocument> findTop50ByChannelIdAndTimestampLessThanOrderByTimestampDesc(Long channelId, Instant beforeTimestamp);
}