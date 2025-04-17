package com.ujiKom.ukkKasir.Members;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_members")
public class Members {
    @Id
    @SequenceGenerator(name = "members_seq", sequenceName = "members_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "members_seq")
    private Integer id;
    private String name;
    private String phoneNumber;
    private Integer point;
    private LocalDateTime createAt = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));
}
