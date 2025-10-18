package br.com.rastrodeliberdade.rider_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "riders")
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String bikerNickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime registerDate;

}