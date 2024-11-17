package com.example.tinkerbell.todo.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.tinkerbell.oAuth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Todo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "is_completed")
	private boolean isCompleted;
	@Column
	private String title;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	@CreatedDate
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	@Column
	private LocalDateTime date;
	@Column
	private int order;
}
