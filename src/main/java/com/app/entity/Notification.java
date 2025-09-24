package com.app.entity;

import java.time.LocalDateTime;
import com.app.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_log_id")
	private Integer notificationLogId;

	@ManyToOne
	@JoinColumn(name = "dispatch_log_id", nullable = false)
	private DispatchLog dispatchLog;

	// Notification Type ENUM
	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type", nullable = false, length = 20)
	private NotificationType notificationType;

	@Column(name = "is_sent", nullable = false)
	private Boolean isSent = false;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	@Column(name = "error_msg", length = 255)
	private String errorMsg;

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_by", length = 50)
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = LocalDateTime.now();
	}
}
