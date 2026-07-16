package com.ndjana.rate.repositories;

import com.ndjana.rate.models.Biographie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiographieRepo extends JpaRepository<Biographie,Long> {
}
