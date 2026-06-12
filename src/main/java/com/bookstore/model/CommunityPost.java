package com.bookstore.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "community_posts")
public class CommunityPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;
    private String username;

    @Column(length = 5000)
    private String content;

    private String title;
    private LocalDateTime createdAt;

    public CommunityPost() {
        this.createdAt = LocalDateTime.now();
    }
}
