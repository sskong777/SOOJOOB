package com.D210.soojoobback.entity;

import com.D210.soojoobback.dto.user.LoginDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

// ORM - Object Relation Mapping

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id // primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="username", length=50, unique = true)
	private String username;

	private String password;

	private String email;

	private String role; //ROLE_USER, ROLE_ADMIN

	private Integer age;

	private String gender;

	private String region;

	private Integer weight;

	private Integer height;

	private boolean activated;
	// OAuth를 위해 구성한 추가 필드 2개
	private String provider;
	private String providerId;
	@CreationTimestamp
	private Timestamp createDate;

	public User(String username, String password, String email, String role, Integer age, String gender, Integer weight, Integer height
			,boolean activated, String region) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.age = null;
		this.weight = null;
		this.height = null;
		this.gender = null;
		this.region = null;
		this.activated = activated;
	}

	public User( String username, String password, String email, String role) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
	}

	public void update(String username, String password, String email) {
		this.email = email;
		this.password = password;
		this.username = username;
	}

	public LoginDetailResponseDto toBuildDetailUser() {
		return LoginDetailResponseDto.builder()
				.id(this.id)
				.email(this.email)
				.username(this.username)
//                .password(this.password)
				.role(this.role)
				.gender(this.gender)
				.age(this.age)
				.weight(this.weight)
				.height(this.height)
				.region(this.region)
				.build();
	}


}