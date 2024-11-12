package com.project.template.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isEnabled;
    private boolean accountNoExpired;
    private boolean accountNoLocked;
    private boolean credentialNoExpired;

    @OneToOne
    private UserEntity user;
}
