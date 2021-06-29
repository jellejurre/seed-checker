package jellejurre.seedchunkchecker.serverMocks;

import com.mojang.datafixers.DataFixer;
import java.util.function.IntSupplier;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.level.storage.LevelStorage;

public class FakeThreadedAnvilChunkStorage extends ThreadedAnvilChunkStorage {
    public FakeThreadedAnvilChunkStorage(ServerWorld world,
                                         LevelStorage.Session session,
                                         DataFixer dataFixer,
                                         ChunkProvider chunkProvider){
        super(world, session, dataFixer, null, Util.getMainWorkerExecutor(), new FakeThreadExecutor("ThreadedAnvilChunkStorageMain") ,
            chunkProvider,
            null, null, null,
            null, 10, false);
    }
    public ServerLightingProvider getLightingProvider(){
        return super.getLightingProvider();
    }

    @Override
    protected void releaseLightTicket(ChunkPos pos) {

    }

    @Override
    protected IntSupplier getCompletedLevelSupplier(long pos) {
        return new IntSupplier() {
            @Override
            public int getAsInt() {
                return 12;
            }
        };
    }
}
