package com.ndjana.rate.repositories;

import com.ndjana.rate.models.ProfessionalGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfGenreRepo extends JpaRepository<ProfessionalGenre,Long> {
}
