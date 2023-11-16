package com.shopme.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 128, nullable = false, unique = true)
	private String name;
	
	@Column(length = 64, nullable = false, unique = true)
	private String alias;
	
	@Column(length = 128)
	private String image;
	
	private boolean enabled;
	
	public Category() {

	}
	
	public Category(Integer id) {
		this.id = id;
	}

	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}
	
	public Category(Integer id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Transient
	public String getImagePath() {
		if (this.id == null || this.image == null) return "/images/image-thumbnail.png";
		
		return "/category-images/" + this.id + "/" + this.image;
	}
	
}