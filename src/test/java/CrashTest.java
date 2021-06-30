import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import java.util.List;
import nl.jellejurre.seedchecker.SeedChecker;
import nl.jellejurre.seedchecker.SeedCheckerSettings;
import org.junit.jupiter.api.Test;

public class CrashTest {
    public static ConcurrentHashMap<Long, Long> count = new ConcurrentHashMap<>();

    @Test
    public void singleThreadedTest(){
        try {
            SeedCheckerSettings.initialise();
            for (int i = 0; i < 10; i++) {
                long worldseed = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                SeedChecker checker = new SeedChecker(worldseed);
                BlockPos spawn = checker.getSpawnPos();
                Box box = new Box(spawn.getX() - 48, 40, spawn.getZ() - 48, spawn.getX() + 48, 80,
                    spawn.getZ() + 48);
                int bedrock = checker.getBlockCountInBox(Blocks.BEDROCK, box);
                Map<BlockPos, BlockEntity> list = checker.getBlockEntitiesInBox(box);
            }
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void multiThreadedTest(){
        SeedCheckerSettings.initialise();
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((int)Math.ceil(Runtime.getRuntime().availableProcessors()*1/4d));
        for (int i = 0; i < 10; i++) {
            pool.execute(new TestTask());
        }
        while(count.size()<10){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public class TestTask implements Runnable{
        @Override
        public void run() {
            long worldseed = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
            SeedChecker checker = new SeedChecker(worldseed);
            BlockPos spawn = checker.getSpawnPos();
            Box box = new Box(spawn.getX()-48, 40, spawn.getZ()-48, spawn.getX()+48, 80, spawn.getZ()+48);
            Map<BlockPos, BlockEntity> list = checker.getBlockEntitiesInBox(box);
            count.put(worldseed, worldseed);
        }
    }
}

