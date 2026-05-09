package com.banquito.core.transactions.repository;

import com.banquito.core.transactions.model.TransaccionInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionInstitucionalRepository extends JpaRepository<TransaccionInstitucional, Long> {
}
