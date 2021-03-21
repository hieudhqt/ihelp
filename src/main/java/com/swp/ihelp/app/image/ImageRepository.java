package com.swp.ihelp.app.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageEntity, String> {

    @Query("SELECT i from ImageEntity i WHERE i.authorAccount.email = :email")
    List<ImageEntity> findByAuthorEmail(String email);

    @Query("SELECT i.imageUrl FROM ImageEntity i WHERE i.authorAccount.email=:email AND i.type LIKE 'avatar'")
    String findAvatarByEmail(String email);

}
