package com.ndjana.rate.repositories;

import com.ndjana.rate.models.Gallerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepo extends JpaRepository<Gallerie,Long> {
}
