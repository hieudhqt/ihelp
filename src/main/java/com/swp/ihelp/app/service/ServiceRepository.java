package com.swp.ihelp.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {
    @Query("SELECT s from ServiceEntity s order by s.status.id")
    Page<ServiceEntity> findAll(Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.title like %:title%")
    Page<ServiceEntity> findByTitle(String title, Pageable pageable);

    @Query(value = "SELECT t1.* from ihelp.service t1 " +
            "INNER JOIN service_category_has_service t2 " +
            "ON t1.id = t2.service_id AND t2.service_category_id = :categoryId", nativeQuery = true)
    Page<ServiceEntity> findByCategoryId(int categoryId, Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.status.id = :id")
    Page<ServiceEntity> findByServiceStatusId(int id, Pageable pageable);

    @Query("SELECT s from ServiceEntity s where s.authorAccount.email = :email")
    Page<ServiceEntity> findByAuthorEmail(String email, Pageable pageable);

    @Modifying
    @Query(value = "update ServiceEntity s " +
            "Set s.title = :#{#service.title}, " +
            "s.description = :#{#service.description}, " +
            "s.startDate = :#{#service.startDate}, " +
            "s.endDate = :#{#service.startDate}, " +
            "s.point = :#{#service.point}, " +
            "s.quota = :#{#service.quota}, " +
            "s.location = :#{#service.location} " +
            "Where s.id = :#{#service.id} ")
    void update(@Param("service") ServiceEntity service);

    @Query("SELECT count(s.service.authorAccount) from ServiceHasAccountEntity s where s.service.id = :serviceId")
    int getUsedSpot(String serviceId);

    @Query("SELECT s.quota from ServiceEntity s where s.id = :serviceId")
    int getQuota(String serviceId);

    @Modifying
    @Query(value = "UPDATE ihelp.service s SET s.status_id = :statusId WHERE s.id = :serviceId",
            nativeQuery = true)
    void updateStatus(String serviceId, int statusId);
}
