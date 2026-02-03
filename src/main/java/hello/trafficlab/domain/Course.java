package hello.trafficlab.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int capacity; // 최대 정원

    private int currentCount; // 현재 신청 인원

    public Course(String title, int capacity) {
        this.title = title;
        this.capacity = capacity;
        this.currentCount = 0;
    }

    // 동시성 이슈를 유도할 핵심 로직
    public void increaseCount() {
        if (this.currentCount < this.capacity) {
            this.currentCount++;
        }
    }
}