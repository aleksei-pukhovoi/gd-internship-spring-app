package gdinternshipspringapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import gdinternshipspringapp.model.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
