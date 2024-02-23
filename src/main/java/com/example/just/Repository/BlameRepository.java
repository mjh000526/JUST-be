package com.example.just.Repository;

import com.example.just.Dao.Blame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlameRepository extends JpaRepository<Blame, Long> {

}

/*

 */