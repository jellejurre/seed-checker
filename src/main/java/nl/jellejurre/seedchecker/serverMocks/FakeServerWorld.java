package nl.jellejurre.seedchecker.serverMocks;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EntityList;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.EntityChunkDataAccess;
import net.minecraft.world.tick.ChunkTickScheduler;
import nl.jellejurre.seedchecker.SeedChunkGenerator;
import org.jetbrains.annotations.Nullable;

public class FakeServerWorld extends ServerWorld {
    private ServerEntityManager entityManager;
    private EntityList entityList;
    private ObjectOpenHashSet loadedMobs;
    private SeedChunkGenerator checker;
    public FakeServerWorld(MinecraftServer server,
                           Executor workerExecutor,
                           LevelStorage.Session session,
                           ServerWorldProperties properties,
                           RegistryKey<World> worldKey,
                           DimensionType dimensionType,
                           WorldGenerationProgressListener worldGenerationProgressListener,
                           ChunkGenerator chunkGenerator,
                           boolean debugWorld,
                           long seed, List<Spawner> spawners,
                           boolean shouldTickTime, SeedChunkGenerator checker) {
        super(server, workerExecutor, session, properties, worldKey, dimensionType,
            worldGenerationProgressListener, chunkGenerator, debugWorld, seed, spawners,
            shouldTickTime);
        this.checker = checker;
        this.entityList = new EntityList();
        this.loadedMobs = new ObjectOpenHashSet();
        DataFixer dataFixer = server.getDataFixer();
        ChunkDataAccess<Entity> chunkDataAccess = new EntityChunkDataAccess(this, new File(session.getWorldDirectory(worldKey).toFile(), "entities").toPath(), dataFixer, false, server);
        entityManager = new ServerEntityManager(Entity.class, new FakeServerWorld.FakeServerEntityHandler(), chunkDataAccess);

    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return checker.getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public WorldChunk getChunk(int chunkX, int chunkZ) {
        Chunk chunk = getChunk(chunkX, chunkZ, ChunkStatus.FULL);
        return new WorldChunk(this, chunk.getPos(), UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler(), new ChunkTickScheduler(), 0L, chunk.getSectionArray(), null, null);
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

    public static FakeServerWorld create(DynamicRegistryManager.Impl registryManager,
                                         RegistryKey<World> worldRegistryKey,
                                         DimensionType dimensionType, long seed,
                                         ResourcePackManager resourcePackManager,
                                         SaveProperties saveProperties,
                                         ChunkGenerator chunkGenerator,
                                         ServerResourceManager serverResourceManager, SeedChunkGenerator seedChecker, FakeLevelStorage.FakeSession session) {
        try {

            FakeMinecraftServer server =
                FakeMinecraftServer.getMinecraftServer(registryManager, session, saveProperties, resourcePackManager,
                    serverResourceManager, null, null, null);


            return new FakeServerWorld(server, Util.getMainWorkerExecutor(), session,
                saveProperties.getMainWorldProperties(), worldRegistryKey, dimensionType, null,
                chunkGenerator, false, BiomeAccess.hashSeed(seed),
                ImmutableList.of(), false, seedChecker);
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
    public void putMapState(String string, MapState mapState) {
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

    public EntityLookup<Entity> getEntityLookup() {
        return this.entityManager.getLookup();
    }

    private final class FakeServerEntityHandler implements EntityHandler<Entity> {
        FakeServerEntityHandler() {
        }

        public void create(Entity entity) {
        }

        public void destroy(Entity entity) {
           FakeServerWorld.this.getScoreboard().resetEntityScore(entity);
        }

        public void startTicking(Entity entity) {
           FakeServerWorld.this.entityList.add(entity);
        }

        public void stopTicking(Entity entity) {
           FakeServerWorld.this.entityList.remove(entity);
        }

        public void startTracking(Entity entity) {
           FakeServerWorld.this.getChunkManager().loadEntity(entity);

            if (entity instanceof MobEntity) {
                FakeServerWorld.this.loadedMobs.add((MobEntity)entity);
            }
        }

        public void stopTracking(Entity entity) {
           FakeServerWorld.this.getChunkManager().unloadEntity(entity);
            if (entity instanceof MobEntity) {
               FakeServerWorld.this.loadedMobs.remove(entity);
            }


            EntityGameEventHandler entityGameEventHandler = entity.getGameEventHandler();
            if (entityGameEventHandler != null) {
                entityGameEventHandler.onEntityRemoval(entity.world);
            }
        }
    }
}
