package gdinternshipspringapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import gdinternshipspringapp.model.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
}
