package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Document(collection = "users")
public class User {
	@Id
	private String id;

	@NotBlank(message = "Name field is required!! ")
	private String name;

	@NotBlank(message = "Email field is required!! ")
	@Email(message = "Enter a valid email!! ")
	private String email;

	@NotBlank(message = "Password field is required!! ")
	@Size(min = 4, message = "Length should be greater than 4")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).*$", message = "Password must contain at least one letter, one number, and one special character")
	private String password;

	private String about;

	@DBRef
	private List<Contact> contacts = new ArrayList<>();

	private String role;
	private Boolean enabled;
	private String imageUrl;

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", about=" + about
				+ ", role=" + role + ", enabled=" + enabled + ", imageUrl=" + imageUrl + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
