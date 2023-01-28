/*
* SubMenu.java
*	This class is sub menu entity/table, getter and setter for each column
*
* Version 1.0
*
* Copyright : Irzan Maulana, Backend Team OGYA
*/
package com.ogya.lokakarya.entity.usermanagement.alamat;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "DESA")
public class Desa {
	private Long desaId;
	private String nama;
	private Kecamatan kecamatan;
	private String programName;
	private Date createdDate;
	private String createdBy;
	private Date updatedDate;
	private String updatedBy;

	@Id
	@GeneratedValue(generator = "DESA_GEN", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "DESA_GEN", sequenceName = "DESA_SEQ", initialValue = 1, allocationSize = 1)
	public Long getDesaId() {
		return desaId;
	}

	public void setDesanId(Long desaId) {
		this.desaId = desaId;
	}

	// --------------------------------------------------------------------------------------------------------
	@Column(name = "NAMA")
	public String getNama() {
		return nama;
	}

	public void setNama(String nama) {
		this.nama = nama;
	}

	// --------------------------------------------------------------------------------------------------------
	@Column(name = "PROGRAM_NAME")
	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	// --------------------------------------------------------------------------------------------------------
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	// --------------------------------------------------------------------------------------------------------
	@Column(name = "CREATED_BY")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	// --------------------------------------------------------------------------------------------------------
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	// --------------------------------------------------------------------------------------------------------
	@Column(name = "UPDATED_BY")
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@ManyToOne
	@JoinColumn(name = "KECAMATAN_ID")
	@JsonIgnore
	public Kecamatan getKecamatan() {
		return kecamatan;
	}

	public void setKecamatan(Kecamatan kecamatan) {
		this.kecamatan = kecamatan;
	}

	@PrePersist
	private void onCreate() {
		createdDate = new Date();
	}

}