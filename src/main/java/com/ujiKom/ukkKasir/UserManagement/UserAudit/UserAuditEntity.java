package com.ujiKom.ukkKasir.UserManagement.UserAudit;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_audits")
public class UserAuditEntity {
    @Id
    @SequenceGenerator(name = "user_audit_sequence", sequenceName = "user_audit_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_audit_sequence")
    private long id;
    private String captureDate;
    private Long timestamp;
    private String timeTaken;
    private Integer status;
    private String method;
    private String uri;
    private String host;
    private String userAgent;
    private String remoteAddress;
    private String reqContentType;
    private String respContentType;

    private String username;

    public UserAuditEntity(String captureDate, Long timestamp) {
        this.captureDate = captureDate;
        this.timestamp = timestamp;
    }
}
