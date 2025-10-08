package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.DispatchLog;
import com.app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
	List<Notification> findByDispatchLog(DispatchLog dispatchLog);
	
	// Optimized query for parent notifications
	List<Notification> findByDispatchLog_Student_StudentIdOrderByCreatedDateDesc(Integer studentId);

}
