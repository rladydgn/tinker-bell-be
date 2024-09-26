package com.example.tinkerbell.event.entity;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "event_id")
	private int eventId;
	@Column(name = "applicant_limit")
	private int applicantLimit;
	@Column(name = "applicant_count")
	private int applicantCount;
	@Column
	private LocalDateTime date;
	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}
