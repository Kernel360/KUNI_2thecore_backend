package hub.application;

import hub.domain.dto.GpsLogDto;
import hub.infrastructure.CarPostionWriterImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class LastPositionUpdator {

    private final CarPostionWriterImpl carPostionWriterImpl;
    private final ScheduledExecutorService scheduledExecutorService;

    public void scheduleEverySecond(String carNumber, List<GpsLogDto.Gps> sorted) {

        final int CHUNK = 3;

        var latestPerChunk = new ArrayList<GpsLogDto.Gps>();

        for(int i = 0; i < sorted.size(); i += CHUNK){
            var to = Math.min(i + CHUNK, sorted.size());
            latestPerChunk.add(sorted.subList(i, to).get(to - i - 1));
        }

        var idx = new java.util.concurrent.atomic.AtomicInteger(0);
        var ref = new java.util.concurrent.atomic.AtomicReference<java.util.concurrent.ScheduledFuture<?>>();

        Runnable task = () -> {
            int i = idx.getAndIncrement();
            if (i >= latestPerChunk.size()) {
                var f = ref.get();
                if (f != null) f.cancel(false);
                log.info("car {} pacing done ({} chunks)", carNumber, latestPerChunk.size());
                return;
            }
            var g = latestPerChunk.get(i);
            // 트랜잭션은 CarPositionWriter가 보장
            carPostionWriterImpl.updateOnce(carNumber, g.getLatitude(), g.getLongitude());
        };

        var f = scheduledExecutorService.scheduleAtFixedRate(task, 0, 3, java.util.concurrent.TimeUnit.SECONDS);

        ref.set(f);

        log.info("car {} scheduled {} chunked updates (every 5s)", carNumber, latestPerChunk.size());
    }
}

