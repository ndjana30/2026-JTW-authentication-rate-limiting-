package com.ndjana.rate.repositories;

import com.ndjana.rate.models.ArtistGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistGenreRepo extends JpaRepository<ArtistGenre,Long> {
}
