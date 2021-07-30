package nl.jellejurre.seedchecker.serverMocks;

import static nl.jellejurre.seedchecker.SeedCheckerSettings.registryManager;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.item.map.MapState;
import nl.jellejurre.seedchecker.SeedChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;

public class FakeServerWorld extends ServerWorld {
    private ObjectOpenHashSet loadedMobs;
    private SeedChunkGenerator checker;
    public FakeServerWorld(MinecraftServer server,
                           Executor workerExecutor,
                           LevelStorage.Session session,
                           ServerWorldProperties properties,
                           RegistryKey<World> worldKey,
                           RegistryKey<DimensionType> dimensionKey,
                           DimensionType dimensionType,
                           WorldGenerationProgressListener worldGenerationProgressListener,
                           ChunkGenerator chunkGenerator,
                           boolean debugWorld,
                           long seed, List<Spawner> spawners,
                           boolean shouldTickTime, SeedChunkGenerator checker) {
        super(server, workerExecutor, session, properties, worldKey, dimensionKey,dimensionType,
            worldGenerationProgressListener, chunkGenerator, debugWorld, seed, spawners,
            shouldTickTime);
        this.checker = checker;
        this.loadedMobs = new ObjectOpenHashSet();
        DataFixer dataFixer = server.getDataFixer();

    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return checker.getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        Chunk chunk = checker.getOrBuildChunk(chunkX, chunkZ, leastStatus.getIndex());
        return chunk;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return checker.getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    public static FakeServerWorld create(RegistryKey<World> worldRegistryKey,
                                         RegistryKey<DimensionType> dimensionKey,
                                         DimensionType dimensionType, long seed,
                                         ResourcePackManager resourcePackManager,
                                         ChunkGenerator chunkGenerator,
                                         ServerResourceManager serverResourceManager, SeedChunkGenerator seedChunkGenerator, FakeLevelStorage.FakeSession session) {
        try {

            FakeSaveProperties saveProperties = new FakeSaveProperties(registryManager, seed);

            FakeMinecraftServer server =
                FakeMinecraftServer.getMinecraftServer(session, saveProperties, resourcePackManager,
                    serverResourceManager, null, null, null);


            return new FakeServerWorld(server, Util.getServerWorkerExecutor(), session,
                saveProperties.getMainWorldProperties(), worldRegistryKey, dimensionKey, dimensionType, null,
                chunkGenerator, false, BiomeAccess.hashSeed(seed),
                ImmutableList.of(), false, seedChunkGenerator);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public int getNextMapId(){
        return 0;
    }

    @Override
    public void putMapState(MapState mapState) {
    }

    @Nullable
    @Override
    public MapState getMapState(String string) {
        return null;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.HARD;
    }

    @Override
    public long getTimeOfDay() {
        return 0;
    }

    @Override
    public float getMoonSize() {
        return 0;
    }
}
