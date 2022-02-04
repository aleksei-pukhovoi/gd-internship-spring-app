package gdinternshipspringapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import gdinternshipspringapp.model.entity.Pic;

@Repository
public interface PicRepository extends JpaRepository<Pic, Long> {
}
