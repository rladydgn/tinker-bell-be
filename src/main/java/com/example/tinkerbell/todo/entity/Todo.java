package com.example.tinkerbell.todo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
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
	@Column(name = "`order`")
	private int order;
	@Column(name = "description")
	private String description;
	@ManyToMany
	@JoinTable(
		name = "todo_category",
		joinColumns = @JoinColumn(name = "todo_id"),
		inverseJoinColumns = @JoinColumn(name = "category_id")
	)
	private List<Category> categoryList = new ArrayList<>();
}
