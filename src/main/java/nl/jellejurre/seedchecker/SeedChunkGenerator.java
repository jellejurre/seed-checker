package nl.jellejurre.seedchecker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import nl.jellejurre.seedchecker.serverMocks.FakeLevelStorage;
import nl.jellejurre.seedchecker.serverMocks.FakeLightingProvider;
import nl.jellejurre.seedchecker.serverMocks.FakeServerWorld;
import nl.jellejurre.seedchecker.serverMocks.FakeThreadedAnvilChunkStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.datafixer.Schemas;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.structure.StructureManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.NoiseChunkGenerator;
import org.apache.commons.lang3.StringUtils;

public class SeedChunkGenerator {
    private final ConcurrentHashMap<ChunkPos, ProtoChunk> chunkMap =
        new ConcurrentHashMap<>();
    private VanillaLayeredBiomeSource biomeSource;
    private SeedCheckerDimension dimension;
    private NoiseChunkGenerator chunkGenerator;
    private FakeLevelStorage levelStorage;
    private StructureManager structureManager;
    private FakeServerWorld fakeServerWorld;
    private LightingProvider fakeLightingProvider;
    private DynamicRegistryManager.Impl registryManager;
    private ResourcePackManager resourcePackManager;
    private ServerResourceManager serverResourceManager;
    private ResourceManager resourceManager;
    private FakeLevelStorage.FakeSession session;
    private long seed;
    private int targetLevel;
    private boolean createLight;

    /**
     * Constructor for SeedChecker
     * @param seed the seed.
     * @param targetLevel the target level to build chunks to.
     * @param dimension the dimension to generate.
     * @param createLight whether or not to create light.
     */
    public SeedChunkGenerator(long seed, int targetLevel, SeedCheckerDimension dimension, boolean createLight){
        this.seed = seed;
        this.dimension = dimension;
        this.targetLevel = targetLevel;
        this.createLight = createLight;
        registryManager = SeedCheckerSettings.registryManager;
        resourcePackManager = SeedCheckerSettings.resourcePackManager;
        serverResourceManager = SeedCheckerSettings.serverResourceManager;
        resourceManager = SeedCheckerSettings.resourceManager;
        biomeSource = new VanillaLayeredBiomeSource(seed, false, false,
            registryManager.get(Registry.BIOME_KEY));
        biomeSource.getTopMaterials();
        //We don't want anything storing anything
        levelStorage = FakeLevelStorage.create(Path.of(""));
        try {
            session = levelStorage.createSession();
            structureManager =
                new StructureManager(resourceManager,
                    session,
                    Schemas.getFixer());
        } catch (IOException e){
            System.out.println("Couldn't instantiate Structuremanager");
            e.printStackTrace();
        }
        switch (dimension) {
            case OVERWORLD -> initOverworld();
            case NETHER -> initNether();
            case END -> initEnd();
        }
        //If we want to create light for mob spawns, we need to get our lighting in a special way, otherwise just use our fake one
        if (createLight) {
            FakeThreadedAnvilChunkStorage
                c = new FakeThreadedAnvilChunkStorage(fakeServerWorld, session, Schemas.getFixer(),
                fakeServerWorld.getChunkManager());
            fakeLightingProvider = c.getLightingProvider();
        } else {
            fakeLightingProvider =
                new FakeLightingProvider(fakeServerWorld.getChunkManager(), false, false);
        }
    }

    private void initOverworld() {
        chunkGenerator = GeneratorOptions
            .createOverworldGenerator(registryManager.get(Registry.BIOME_KEY), registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY), seed);
        fakeServerWorld = FakeServerWorld.create(registryManager, World.OVERWORLD,
            registryManager.get(Registry.DIMENSION_TYPE_KEY)
                .getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY), seed,
            resourcePackManager, chunkGenerator, serverResourceManager, this, session);
    }

    private void initNether() {
        chunkGenerator = new NoiseChunkGenerator(MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(registryManager.get(Registry.BIOME_KEY), seed), seed, () -> registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(
            ChunkGeneratorSettings.NETHER));
        fakeServerWorld = FakeServerWorld.create(registryManager, World.NETHER,
            registryManager.get(Registry.DIMENSION_TYPE_KEY)
                .getOrThrow(DimensionType.THE_NETHER_REGISTRY_KEY), seed,
            resourcePackManager, chunkGenerator, serverResourceManager, this, session);
    }

    private void initEnd(){
        chunkGenerator = new NoiseChunkGenerator(new TheEndBiomeSource(registryManager.get(Registry.BIOME_KEY), seed), seed, () -> registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY).getOrThrow(ChunkGeneratorSettings.END));
        fakeServerWorld = FakeServerWorld.create(registryManager, World.NETHER,
            registryManager.get(Registry.DIMENSION_TYPE_KEY)
                .getOrThrow(DimensionType.THE_END_REGISTRY_KEY), seed,
            resourcePackManager, chunkGenerator, serverResourceManager, this, session);
    }


    /**
     * Get the spawn position of the current seed.
     * @return the {@link BlockPos} of the spawnposition.
     */
    public BlockPos getSpawnPos(){
        int i = 0;
        Random random = new Random(seed);
        BlockPos lv3 = biomeSource.locateBiome(0, 63, 0, 256, biome -> biome.getSpawnSettings().isPlayerSpawnFriendly(), random);
        ChunkPos chunkPos;
        if (lv3 == null) {
            chunkPos = new ChunkPos(0, 0);
        } else {
            chunkPos = new ChunkPos(lv3);
        }
        boolean bl3 = false;
        for (Block lv5 : BlockTags.VALID_SPAWN.values()) {
            if (!biomeSource.getTopMaterials().contains(lv5.getDefaultState())) continue;
            bl3 = true;
            break;
        }
        BlockPos finalSpawnPoint = chunkPos.getStartPos().add(8, i, 8);
        int j = 0;
        int k = 0;
        int l = 0;
        int m = -1;
        int n = 32;
        for (int o = 0; o < 1024; ++o) {
            BlockPos lv7;
            if (j > -16 && j <= 16 && k > -16 && k <= 16 && (lv7 =
                findServerSpawnPoint(fakeServerWorld, new ChunkPos(chunkPos.x + j, chunkPos.z + k),
                    bl3)) != null) {
                finalSpawnPoint = lv7;
                break;
            }
            if (j == k || j < 0 && j == -k || j > 0 && j == 1 - k) {
                int p = l;
                l = -m;
                m = p;
            }
            j += l;
            k += m;
        }
        return finalSpawnPoint;
    }

    //Helper method for getSpawnPos
    private BlockPos findServerSpawnPoint(World world, ChunkPos chunkPos,
                                          boolean validSpawnNeeded){
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = findOverworldSpawn(world, i, j, validSpawnNeeded);
                if (blockPos != null) {
                    return blockPos;
                }
            }
        }
        return null;
    }

    //Another helper method for getSpawnPos
    private BlockPos findOverworldSpawn(World world, int x, int z,
                                        boolean validSpawnNeeded){
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = world.getBiome(mutable);
        boolean bl = world.getDimension().hasCeiling();
        BlockState blockState = biome.getGenerationSettings().getSurfaceConfig().getTopMaterial();
        if (validSpawnNeeded && !blockState.isIn(BlockTags.VALID_SPAWN)) {
            return null;
        } else {
            Chunk worldChunk = getOrBuildChunk(ChunkSectionPos.getSectionCoord(x),
                ChunkSectionPos.getSectionCoord(z));
            int i = bl ? chunkGenerator.getSpawnHeight(world) :
                worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
            if (i < world.getBottomY()) {
                return null;
            } else {
                int j = worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15);
                if (j <= i &&
                    j > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
                    return null;
                } else {
                    for (int k = i + 1; k >= world.getBottomY(); --k) {
                        mutable.set(x, k, z);
                        BlockState blockState2 = getBlockState(mutable.getX(), mutable.getY(),
                            mutable.getZ());
                        if (!blockState2.getFluidState().isEmpty()) {
                            break;
                        }

                        if (blockState2.equals(blockState)) {
                            return mutable.up().toImmutable();
                        }
                    }
                    return null;
                }
            }
        }
    }

    /**
     * Gets the {@link Block} of at given coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordiante.
     * @return the {@link Block} at those coordinate.
     */
    public Block getBlock(int x, int y, int z){
        return getBlockState(x, y, z).getBlock();
    }

    /**
     * Gets the {@link Block} of at a given {@link BlockPos}.
     * @param pos the blockpos.
     * @return the {@link Block} at that {@link BlockPos}.
     */
    public Block getBlock(BlockPos pos){
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Gets the {@link BlockState} of at given coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordiante.
     * @return the {@link BlockState} at those coordinate.
     */
    public BlockState getBlockState(int x, int y, int z){
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        ProtoChunk chunk = chunkMap.get(pos);
        if (chunk == null) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld);
        }
        if (chunk.getStatus().getIndex() < targetLevel) {
            chunk = getOrBuildChunk(pos.x, pos.z);
        }
        chunkMap.put(pos, chunk);
        return chunk.getBlockState(
            new BlockPos(x, y, z));
    }

    /**
     * Gets the {@link BlockState} of at a given {@link BlockPos}.
     * @param pos the blockpos.
     * @return the {@link BlockState} at that {@link BlockPos}.
     */
    public BlockState getBlockState(BlockPos pos){
        return getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Gets the {@link BlockEntity} of at given coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordiante.
     * @return the {@link BlockEntity} at those coordinate.
     */
    public BlockEntity getBlockEntity(int x, int y, int z){
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        ProtoChunk chunk = chunkMap.get(pos);
        if (chunk == null) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld);
        }
        if (chunk.getStatus().getIndex() < targetLevel) {
            chunk = getOrBuildChunk(pos.x, pos.z);
        }
        chunkMap.put(pos, chunk);
        return chunk.getBlockEntity(
            new BlockPos(x, y, z));
    }

    /**
     * Gets the {@link BlockEntity} of at a given {@link BlockPos}.
     * @param pos the blockpos.
     * @return the {@link BlockEntity} at that {@link BlockPos}.
     */
    public BlockEntity getBlockEntity(BlockPos pos){
        return getBlockEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get a {@link FluidState} from coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @return the {@link FluidState} at that position.
     */
    public FluidState getFluidState(int x, int y, int z){
        ChunkPos pos = new ChunkPos(x >> 4, z >> 4);
        ProtoChunk chunk = chunkMap.get(pos);
        if (chunk == null) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld);
        }
        if (chunk.getStatus().getIndex() < targetLevel) {
            chunk = getOrBuildChunk(pos.x, pos.z);
        }
        chunkMap.put(pos, chunk);
        return chunk.getFluidState(
            new BlockPos(x, y, z));
    }

    /**
     * Get a {@link FluidState} from a given {@link BlockPos}.
     * @param pos the block pos.
     * @return the {@link FluidState} of the block at the {@link BlockPos}.
     */
    public FluidState getFluidState(BlockPos pos){
        return getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Generate chest loot for a chest at a given block pos.
     * @param x the x coordinate of the chest.
     * @param y the y coordinate of the chest.
     * @param z the z coordinate of the chest.
     * @return a list of {@link ItemStack}s from the chest, or an empty list if there is no chest at the given pos.
     */
    public List<ItemStack> generateChestLoot(int x, int y, int z){
        ChestBlockEntity chest = (ChestBlockEntity) getBlockEntity(x, y, z);
        if(chest==null){
            return new ArrayList<>();
        }
        Identifier lootTableId = (Identifier) ReflectionUtils.getValueFromField(chest, "lootTableId");
        long lootTableSeed = (long) ReflectionUtils.getValueFromField(chest, "lootTableSeed");
        LootTable lootTable = serverResourceManager.getLootManager().getTable(lootTableId);
        LootContext.Builder lootContextBuilder = new LootContext.Builder(fakeServerWorld).parameter(
            LootContextParameters.ORIGIN, Vec3d.ofCenter(chest.getPos())).random(lootTableSeed);
        return lootTable.generateLoot(lootContextBuilder.build(LootContextTypes.CHEST));
    }

    /**
     * Generate chest loot for a chest at a given {@link BlockPos}.
     * @param pos the position of the chest.
     * @return a list of {@link ItemStack}s from the chest, or an empty list if there is no chest at the given pos.
     */
    public List<ItemStack> generateChestLoot(BlockPos pos){
        return generateChestLoot(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it to the target level if it doesn't exist yet.
     * @param chunkX the x coordinate of the chunk in chunk coordinates.
     * @param chunkZ the z coordinate of the chunk in chunk coordinates.
     * @return the built chunk.
     */
    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ){
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        ProtoChunk chunk;

        if (!chunkMap.containsKey(pos)) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld);
            chunkMap.put(pos, chunk);
        } else {
            chunk = chunkMap.get(pos);
        }

        generateChunkUntil(chunk, targetLevel, true);

        return chunk;
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it to the target level if it doesn't exist yet.
     * @param pos the position of the chunk.
     * @return the built chunk.
     */
    public ProtoChunk getOrBuildChunk(ChunkPos pos){
        return getOrBuildChunk(pos.x, pos.z);
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it if it doesn't exist yet.
     * @param chunkX the x coordinate of the chunk to build in chunk coordinates.
     * @param chunkZ the z coordinate of the chunk to build in chunk coordinates.
     * @param targetLevel the targelevel of the chunk see {@link TargetState}.
     * @return a chunk built to at least the target level.
     */
    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ, int targetLevel){
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        ProtoChunk chunk;

        if (!chunkMap.containsKey(pos)) {
            chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld);
            chunkMap.put(pos, chunk);
        } else {
            chunk = chunkMap.get(pos);
        }

        generateChunkUntil(chunk, targetLevel, true);

        return chunk;
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it if it doesn't exist yet.
     * @param pos the chunk pos of the chunk.
     * @param targetLevel the targelevel of the chunk see {@link TargetState}.
     * @return a chunk built to at least the target level.
     */
    public ProtoChunk getOrBuildChunk(ChunkPos pos, int targetLevel){
        return getOrBuildChunk(pos.x, pos.z, targetLevel);
    }

    //Helper method for generating a chunk up to a certain level
    private void generateChunkUntil(ProtoChunk chunk, int target, boolean generateOthers){
        if (target >= 1)
            STRUCTURE_STARTS(chunk);
        if (target >= 2) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 2);
            STRUCTURE_REFERENCES(chunk, chunks);
        }
        if (target >= 3)
            BIOMES(chunk);
        if (target >= 4) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 4);
            NOISE(chunk, chunks);
        }
        if (target >= 5)
            SURFACE(chunk);
        if (target >= 6)
            CARVERS(chunk);
        if (target >= 7)
            LIQUID_CARVERS(chunk);
        if (target >= 8) {
            List<Chunk> chunks = getRegion(chunk.getPos(), 8, 8);
            FEATURES(chunk, chunks, generateOthers);
        }
        if(target >= 9&&dimension==SeedCheckerDimension.OVERWORLD){
            LIGHT(chunk);
        }
        if(target >= 10){
            SPAWN(chunk);
        }
    }

    //Helper method for getting a region of chunks
    private List<Chunk> getRegion(ChunkPos center, int radius, int centerTargetLevel){
        centerTargetLevel -= 1;

        List<Chunk> chunks = new ArrayList<>();
        int centerX = center.x;
        int centerZ = center.z;

        for (int z = -radius; z <= radius; z++) {
            for (int x = -radius; x <= radius; x++) {
                int distanceFromCenter = Math.max(Math.abs(z), Math.abs(x));
                int chunkTargetLevel = Math.max(0, centerTargetLevel - distanceFromCenter);

                ChunkPos pos = new ChunkPos(centerX + x, centerZ + z);
                ProtoChunk chunk;

                if (!chunkMap.containsKey(pos)) {
                    chunk = new ProtoChunk(pos, UpgradeData.NO_UPGRADE_DATA, fakeServerWorld);
                    STRUCTURE_STARTS(chunk);
                    chunkMap.put(pos, chunk);
                } else {
                    chunk = chunkMap.get(pos);
                }

                chunks.add(chunk);

                if (!pos.equals(center) && chunkTargetLevel != 0 &&
                    chunkTargetLevel > chunk.getStatus().getIndex())
                    generateChunkUntil(chunk, chunkTargetLevel, true);
            }
        }

        return chunks;
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#STRUCTURE_STARTS}.
     * @param chunk The chunk to upgrade.
     */
    private void STRUCTURE_STARTS(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.STRUCTURE_STARTS))
            return;
        chunkGenerator.setStructureStarts(fakeServerWorld.getRegistryManager(),
            fakeServerWorld.getStructureAccessor(), chunk, structureManager, seed);
        chunk.setStatus(ChunkStatus.STRUCTURE_STARTS);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#STRUCTURE_REFERENCES}.
     * @param chunk The chunk to upgrade.
     */
    private void STRUCTURE_REFERENCES(ProtoChunk chunk, List<Chunk> chunks) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.STRUCTURE_REFERENCES))
            return;
        ChunkRegion chunkRegion =
            new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.STRUCTURE_REFERENCES, -1);
        chunkGenerator.addStructureReferences(chunkRegion,
            fakeServerWorld.getStructureAccessor().forRegion(chunkRegion), chunk);
        chunk.setStatus(ChunkStatus.STRUCTURE_REFERENCES);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#BIOMES}.
     * @param chunk The chunk to upgrade.
     */
    private void BIOMES(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.BIOMES))
            return;
        chunkGenerator
            .populateBiomes(fakeServerWorld.getRegistryManager().get(Registry.BIOME_KEY), chunk);
        chunk.setStatus(ChunkStatus.BIOMES);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#NOISE}.
     * @param chunk The chunk to upgrade.
     */
    private void NOISE(ProtoChunk chunk, List<Chunk> chunks){
        if (chunk.getStatus().isAtLeast(ChunkStatus.NOISE))
            return;
        ChunkRegion chunkRegion = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.NOISE, 0);
        try {
            chunkGenerator
                .populateNoise(null, fakeServerWorld.getStructureAccessor().forRegion(chunkRegion),
                    chunk).get();
        } catch (ExecutionException |InterruptedException e){
            System.out.println("Error while generating chunk " + chunk.getPos() +" with seed "+ seed);
            e.printStackTrace();
        }
        chunk.setStatus(ChunkStatus.NOISE);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#SURFACE}.
     * @param chunk The chunk to upgrade.
     */
    private void SURFACE(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.SURFACE))
            return;
        chunkGenerator.buildSurface(
            new ChunkRegion(fakeServerWorld, ImmutableList.of(chunk), ChunkStatus.SURFACE, 0),
            chunk);
        chunk.setStatus(ChunkStatus.SURFACE);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#CARVERS}.
     * @param chunk The chunk to upgrade.
     */
    private void CARVERS(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.CARVERS))
            return;
        chunkGenerator
            .carve(seed, fakeServerWorld.getBiomeAccess(), chunk, GenerationStep.Carver.AIR);
        chunk.setStatus(ChunkStatus.CARVERS);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#LIQUID_CARVERS}.
     * @param chunk The chunk to upgrade.
     */
    private void LIQUID_CARVERS(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.LIQUID_CARVERS))
            return;
        chunkGenerator
            .carve(seed, fakeServerWorld.getBiomeAccess(), chunk, GenerationStep.Carver.LIQUID);
        chunk.setStatus(ChunkStatus.LIQUID_CARVERS);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#FEATURES}.
     * @param chunk The chunk to upgrade.
     */
    private void FEATURES(ProtoChunk chunk, List<Chunk> chunks, boolean generateOthers) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.FEATURES))
            return;


        chunk.setLightingProvider(fakeLightingProvider);
        Heightmap.populateHeightmaps(chunk, EnumSet
            .of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));
        ChunkRegion chunkRegion = new ChunkRegion(fakeServerWorld, chunks, ChunkStatus.FEATURES, 1);
        chunkGenerator.generateFeatures(chunkRegion,
            fakeServerWorld.getStructureAccessor().forRegion(chunkRegion));
        chunk.setStatus(ChunkStatus.FEATURES);

        for (LongSet chunkPosLongs : chunk.getStructureReferences().values()) {
            for (Long chunkPosLong : chunkPosLongs) {
                ChunkPos pos = new ChunkPos(chunkPosLong);
                if (pos.equals(chunk.getPos()))
                    continue;

                generateChunkUntil(chunkMap.get(pos), 8, true);
            }
        }

        if (generateOthers) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    generateChunkUntil(
                        chunkMap.get(new ChunkPos(chunk.getPos().x + x, chunk.getPos().z + z)), 8,
                        false);
                }
            }
        }
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#LIGHT}.
     * @param chunk The chunk to upgrade.
     */
    private void LIGHT(ProtoChunk chunk){
        if(!createLight){
            throw new IllegalStateException("Tried to generate light without light generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 9 or higher.");
        }
        if (chunk.getStatus().isAtLeast(ChunkStatus.LIGHT))
            return;
        boolean bl = chunk.getStatus().isAtLeast(ChunkStatus.LIGHT) && chunk.isLightOn();
        if (!chunk.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            chunk.setStatus(ChunkStatus.LIGHT);
        }
        ((ServerLightingProvider) fakeLightingProvider).light(chunk, bl).thenApply(Either::left);
        chunk.setStatus(ChunkStatus.LIGHT);
    }

    /**
     * Upgrade the given chunk to {@link ChunkStatus#SPAWN}.
     * @param chunk The chunk to upgrade.
     */
    private void SPAWN(ProtoChunk chunk) {
        if (chunk.getStatus().isAtLeast(ChunkStatus.SPAWN))
            return;
        chunkGenerator.populateEntities(new ChunkRegion(fakeServerWorld, ImmutableList.of(chunk), ChunkStatus.SPAWN, -1));
        chunk.setStatus(ChunkStatus.FULL);
    }

    /**
     * Get all entities within a box.
     *
     * NOTE: NbtCompounds arent great to work with. When getting a value from it you have to first cast it to the
     *       appropriate type (NbtLong, NbtString, etc).
     * @param name A simple filter applied to the entity name.
     * @param box The box to search within.
     * @param predicate A more advanced filter to check if the entity matches your criteria.
     * @return A list of {@link NbtCompound}s of the entities that where found.
     */
    public List<NbtCompound> getEntitiesInBox(String name, Box box, Predicate<NbtCompound> predicate) {
        if(targetLevel<10){
            throw new IllegalStateException("Tried to generate entities without entity generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 10 or higher.");
        }
        int chunkCountx = (int) box.getXLength()/16;
        int chunkCountz = (int) box.getZLength()/16;
        List<NbtCompound> list = Lists.newArrayList();
        for (int x = (int)box.minX>>4; x < ((int)box.minX>>4)+chunkCountx; x++) {
            for (int z = (int)box.minZ>>4; z < ((int)box.minZ>>4)+chunkCountz ; z++) {
                ProtoChunk chunk = getOrBuildChunk(x, z);
                for(NbtCompound nbtCompound : chunk.getEntities()){
                    if(nbtCompound.get("id").asString().toLowerCase().contains(name.toLowerCase())) {
                        NbtList s = (NbtList)nbtCompound.get("Pos");
                        double mobx = s.getDouble(0);
                        double moby = s.getDouble(1);
                        double mobz = s.getDouble(2);
                        if (box.contains(new Vec3d(mobx, moby, mobz)) && predicate.test(nbtCompound)) {
                            list.add(nbtCompound);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Get all entities within a box.
     *
     * NOTE: NbtCompounds arent great to work with. When getting a value from it you have to first cast it to the
     *       appropriate type (NbtLong, NbtString, etc).
     * @param name A simple filter applied to the entity name.
     * @param box The box to search within.
     * @return A list of {@link NbtCompound}s of the entities that where found.
     */
    public List<NbtCompound> getEntitiesInBox(String name, Box box) {
        return this.getEntitiesInBox(name, box, compound -> true);
    }

    /**
     * Get all entities within a box.
     *
     * NOTE: NbtCompounds arent great to work with. When getting a value from it you have to first cast it to the
     *       appropriate type (NbtLong, NbtString, etc).
     * @param box The box to search within.
     * @return A list of {@link NbtCompound}s of the entities that where found.
     */
    public List<NbtCompound> getEntitiesInBox(Box box) {
        return getEntitiesInBox("", box);
    }

    /**
     * Get all {@link BlockEntity}s within a box.
     * @param type The type of block entity to search for.
     * @param box The box to search within.
     * @param predicate A more advanced filter to check if the block entity matches your criteria.
     * @return A map from {@link BlockPos} to {@link BlockEntity} containing all block entities within the box.
     */
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box, Predicate<BlockEntity> predicate){
        if(targetLevel<10){
            throw new IllegalStateException("Tried to generate entities without entity generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 10 or higher.");
        }
        Map<BlockPos, BlockEntity> map = new HashMap<>();
        int chunkCountx = (int) box.getXLength()/16;
        int chunkCountz = (int) box.getZLength()/16;
        for (int x = (int)box.minX>>4; x < ((int)box.minX>>4)+chunkCountx; x++) {
            for (int z = (int)box.minZ>>4; z < ((int)box.minZ>>4)+chunkCountz ; z++) {
                ProtoChunk chunk = getOrBuildChunk(x, z);
                for(BlockEntity blockEntity :chunk.getBlockEntities().values()){
                    if(type==null||blockEntity.getType()==type) {
                        BlockPos pos = blockEntity.getPos();
                        if (box.contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ())) && predicate.test(blockEntity)) {
                            map.put(blockEntity.getPos(), blockEntity);
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * Get all {@link BlockEntity}s within a box.
     * @param type The type of block entity to search for.
     * @param box The box to search within.
     * @return A map from {@link BlockPos} to {@link BlockEntity} containing all block entities within the box.
     */
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box) {
        return getBlockEntitiesInBox(type, box, blockEntity -> true);
    }

    /**
     * Get all {@link BlockEntity}s within a box.
     * @param box The box to search within.
     * @return A map from {@link BlockPos} to {@link BlockEntity} containing all block entities within the box.
     */
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(Box box) {
        return getBlockEntitiesInBox(null, box);
    }

    /**
     * Count the amount of a certain {@link Block}s in an area.
     * @param block The block type to count.
     * @param box The area to search within.
     * @return The amount of blocks.
     */
    public int getBlockCountInBox(Block block, Box box){
        if(targetLevel<7){
            throw new IllegalStateException("Tried to generate blocks without block generation allowed.\nTo allow this, call the constructor of SeedChecker with third argument 7 or higher.");
        }
        int count = 0;
        int chunkCountx = (int) box.getXLength()/16;
        int chunkCountz = (int) box.getZLength()/16;
        for (int x = (int)box.minX>>4; x < ((int)box.minX>>4)+chunkCountx; x++) {
            for (int z = (int)box.minZ>>4; z < ((int)box.minZ>>4)+chunkCountz ; z++) {
                ProtoChunk c = getOrBuildChunk(x, z);
                for (int x2 = 0; x2 < 16; x2++) {
                    for (int z2 = 0; z2 < 16; z2++) {
                        for (int y = (int)box.minY; y < (int)box.maxY; y++) {
                            BlockPos pos = new BlockPos((x << 4) + x2, y, (z << 4) + z2);
                            if (box.contains(new Vec3d(pos.getX(), pos.getY(), pos.getZ()))) {
                                if (c.getBlockState(pos).getBlock() == block) {
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Print a box of blocks with their coordinates and block state.
     *
     * NOTE: This method is mostly for debugging
     *       It prints it in such a way that if you look from above, it should correspond with your world
     * @param box The box to print.
     */
    public void printArea(Box box) {
        Direction.Axis xa = Direction.Axis.X;
        Direction.Axis ya = Direction.Axis.Y;
        Direction.Axis za = Direction.Axis.Z;
        for (int y = (int)box.getMin(ya); y <= box.getMax(ya); y++) {
            for (int x = (int)box.getMin(xa); x <= box.getMax(xa); x++) {
                for (int z = (int)box.getMax(za); z >= box.getMin(za); z--) {
                    System.out.print("["+ StringUtils.leftPad(Integer.toString(x), 3, "0")+ ","+StringUtils.leftPad(Integer.toString(y), 3, "0")+ ","+StringUtils.leftPad(Integer.toString(z), 3, "0")+"] "+ String.format("%1$-20s", getBlockState(
                        x, y, z).getBlock().getTranslationKey().substring(16)));
                }
                System.out.println();
            }
        }
    }
}
