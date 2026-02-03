package hello.trafficlab;

import hello.trafficlab.domain.Course;
import hello.trafficlab.repository.CourseRepository;
import hello.trafficlab.service.CourseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class TrafficLabApplicationTests {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("동시에 100명이 수강신청을 하면 정합성 문제가 발생한다")
    void concurrencyTest() throws InterruptedException {
        // 1. 초기 데이터 준비 (정원 30명)
        Course course = courseRepository.save(new Course("알고리즘", 30));
        int threadCount = 100;

        // 멀티스레드 설정을 위한 자바 표준 도구
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 2. 100명이 동시에 신청 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    courseService.register(course.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 3. 결과 확인
        Course updatedCourse = courseRepository.findById(course.getId()).orElseThrow();
        System.out.println("최종 신청 인원: " + updatedCourse.getCurrentCount());

        // 정원은 30명이지만, 동시성 제어가 없으면 30명보다 많이 신청되거나
        // 레이스 컨디션으로 인해 30명보다 적게 기록되는 등 엉망이 됩니다.
    }
}
