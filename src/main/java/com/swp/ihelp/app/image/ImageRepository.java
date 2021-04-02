package com.swp.ihelp.app.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ImageRepository extends JpaRepository<ImageEntity, String> {

    @Query("SELECT i from ImageEntity i WHERE i.authorAccount.email = :email")
    Set<ImageEntity> findByAuthorEmail(String email);

    @Query("SELECT i.imageUrl FROM ImageEntity i WHERE i.authorAccount.email=:email AND i.type LIKE 'avatar'")
    String findAvatarByEmail(String email);

    @Modifying
    @Query(value = "UPDATE ImageEntity i SET i.imageUrl=:avatarUrl WHERE i.authorAccount.email=:email AND i.type = 'avatar'")
    void updateAvatar(String email, String avatarUrl);

}
