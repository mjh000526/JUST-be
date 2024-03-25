package com.example.just.Repository;

import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import com.example.just.Document.HashTagDocument;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    List<Notification> findAllByReceiver(Member member);


}
