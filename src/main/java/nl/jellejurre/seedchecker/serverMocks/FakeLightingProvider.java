package nl.jellejurre.seedchecker.serverMocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public class FakeLightingProvider extends LightingProvider {
    public FakeLightingProvider(ChunkProvider chunkProvider,
                                boolean hasBlockLight, boolean hasSkyLight) {
        super(chunkProvider, hasBlockLight, hasSkyLight);
    }

    @Override
    public void checkBlock(BlockPos pos) {
    }

    @Override
    public void addLightSource(BlockPos pos, int level) {
    }

    @Override
    public boolean hasUpdates() {
        return false;
    }

    @Override
    public int doLightUpdates(int i, boolean bl, boolean bl2) {
        return 0;
    }

    @Override
    public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
    }

    @Override
    public void setColumnEnabled(ChunkPos chunkPos, boolean bl) {
    }

    @Override
    public ChunkLightingView get(LightType lightType) {
        return null;
    }

    @Override
    public String displaySectionLevel(LightType lightType, ChunkSectionPos chunkSectionPos) {
        return null;
    }

    @Override
    public void enqueueSectionData(LightType lightType, ChunkSectionPos pos,
                                   @Nullable ChunkNibbleArray nibbles, boolean bl) {
    }

    @Override
    public void setRetainData(ChunkPos pos, boolean retainData) {
    }

    @Override
    public int getLight(BlockPos pos, int ambientDarkness) {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getBottomY() {
        return 0;
    }

    @Override
    public int getTopY() {
        return 0;
    }

    @Override
    public void setSectionStatus(BlockPos pos, boolean notReady) {
    }
}
