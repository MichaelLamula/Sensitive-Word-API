package repository;

import entity.SensitiveWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensitiveWordRepository extends JpaRepository<SensitiveWord,Long> {
    boolean existsByWordIgnoreCase(String word);
}
