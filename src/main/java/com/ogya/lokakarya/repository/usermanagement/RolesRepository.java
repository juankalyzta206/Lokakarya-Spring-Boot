package com.ogya.lokakarya.repository.usermanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ogya.lokakarya.entity.usermanagement.Roles;

public interface RolesRepository extends JpaRepository<Roles, Long> {
	@Query(value = "SELECT COUNT(*) FROM ROLES r WHERE r.NAMA = :nama", nativeQuery = true)
	Long isExistRoleName(@Param("nama") String nama);

	@Query(value = "SELECT COUNT(*) FROM HAK_AKSES ha WHERE ha.ROLE_ID = :roleId", nativeQuery = true)
	Long isExistHakAkses(@Param("roleId") Long roleId);

	@Query(value = "SELECT COUNT(*) FROM ROLE_MENU rm WHERE rm.ROLE_ID = :roleId", nativeQuery = true)
	Long isExistRoleMenu(@Param("roleId") Long roleId);

}