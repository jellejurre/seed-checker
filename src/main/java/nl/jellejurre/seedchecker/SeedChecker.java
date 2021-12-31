package nl.jellejurre.seedchecker;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.ProtoChunk;

public class SeedChecker {
    private SeedChunkGenerator seedChunkGenerator;
    private long seed;
    private int targetLevel;
    private boolean createLight;

    /**
     * Constructor for SeedChecker
     * @param seed the seed.
     * @param targetLevel the target level to build chunks to.
     * @param dimension the dimension to generate.
     */
    public SeedChecker(long seed, int targetLevel, SeedCheckerDimension dimension) {
        //This is here to avoid OutOfMemoryErrors
        if(Runtime.getRuntime().freeMemory()<50000){
            try {
                Thread.sleep(5000);
            }catch (Exception e){

            }
        }
        SeedCheckerSettings.initialise();
        this.seed = seed;
        this.targetLevel = targetLevel;
        this.createLight = targetLevel >= 9;
        this.seedChunkGenerator = new SeedChunkGenerator(seed, targetLevel, dimension, createLight);
    }

    /**
     * Constructor for SeedChecker.
     * @param seed the seed.
     * @param state the target state to build the chunk to.
     * @param dimension the dimension.
     */
    public SeedChecker(long seed, TargetState state, SeedCheckerDimension dimension) {
        this(seed, state.getLevel(), dimension);
    }

    /**
     * Constructor for SeedChecker.
     * @param seed the seed.
     * @param state the target state to build the chunk to.
     */
    public SeedChecker(long seed, int state){
        this(seed, state, SeedCheckerDimension.OVERWORLD);
    }

    /**
     * Constructor for SeedChecker.
     * @param seed the seed.
     * @param state the target state to build the chunk to.
     */
    public SeedChecker(long seed, TargetState state){
        this(seed, state.getLevel());
    }

    /**
     * Constructor for SeedChecker.
     * NOTES: target level will be set to a full chunk.
     * @param seed the seed.
     * @param dimension the dimension.
     */
    public SeedChecker(long seed, SeedCheckerDimension dimension) {
        this(seed, 12, dimension);
    }

    /**
     * Constructor for SeedChecker.
     * NOTES: target level will be set to a full chunk, and dimension will be set to Overworld.
     * @param seed the seed.
     */
    public SeedChecker(long seed) {
        this(seed, 12, SeedCheckerDimension.OVERWORLD);
    }

    /**
     * Get the spawn position of the current seed.
     * @return the {@link BlockPos} of the spawnposition.
     */
    public BlockPos getSpawnPos(){
        return this.seedChunkGenerator.getSpawnPos();
    }

    /**
     * Gets the {@link Block} of at given coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordiante.
     * @return the {@link Block} at those coordinate.
     */
    public Block getBlock(int x, int y, int z){
        return seedChunkGenerator.getBlock(x, y, z);
    }

    /**
     * Gets the {@link Block} of at a given {@link BlockPos}.
     * @param pos the blockpos.
     * @return the {@link Block} at that {@link BlockPos}.
     */
    public Block getBlock(BlockPos pos){
        return seedChunkGenerator.getBlock(pos);
    }

    //A method to return a BlockState from coordinates
    public BlockState getBlockState(int x, int y, int z){
        return seedChunkGenerator.getBlockState(x, y, z);
    }

    /**
     * Gets the {@link BlockState} of at a given {@link BlockPos}.
     * @param pos the blockpos.
     * @return the {@link BlockState} at that {@link BlockPos}.
     */
    public BlockState getBlockState(BlockPos pos){
        return seedChunkGenerator.getBlockState(pos);
    }

    /**
     * Gets the {@link BlockEntity} of at given coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordiante.
     * @return the {@link BlockEntity} at those coordinate.
     */
    public BlockEntity getBlockEntity(int x, int y, int z){
        return seedChunkGenerator.getBlockEntity(x, y, z);
    }

    /**
     * Gets the {@link BlockEntity} of at a given {@link BlockPos}.
     * @param pos the blockpos.
     * @return the {@link BlockEntity} at that {@link BlockPos}.
     */
    public BlockEntity getBlockEntity(BlockPos pos){
        return seedChunkGenerator.getBlockEntity(pos);
    }

    /**
     * Get a {@link FluidState} from coordinates.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @return the {@link FluidState} at that position.
     */
    public FluidState getFluidState(int x, int y, int z){
        return seedChunkGenerator.getFluidState(x, y, z);
    }

    /**
     * Get a {@link FluidState} from a given {@link BlockPos}.
     * @param pos the block pos.
     * @return the {@link FluidState} of the block at the {@link BlockPos}.
     */
    public FluidState getFluidState(BlockPos pos){
        return seedChunkGenerator.getFluidState(pos);
    }

    /**
     * Generate chest loot for a chest at a given block pos.
     * @param x the x coordinate of the chest.
     * @param y the y coordinate of the chest.
     * @param z the z coordinate of the chest.
     * @return a list of {@link ItemStack}s from the chest, or an empty list if there is no chest at the given pos.
     */
    public List<ItemStack> generateChestLoot(int x, int y, int z){
        return seedChunkGenerator.generateChestLoot(x, y, z);
    }

    /**
     * Generate chest loot for a chest at a given {@link BlockPos}.
     * @param pos the position of the chest.
     * @return a list of {@link ItemStack}s from the chest, or an empty list if there is no chest at the given pos.
     */
    public List<ItemStack> generateChestLoot(BlockPos pos){
        return seedChunkGenerator.generateChestLoot(pos);
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it to the target level if it doesn't exist yet.
     * @param chunkX the x coordinate of the chunk in chunk coordinates.
     * @param chunkZ the z coordinate of the chunk in chunk coordinates.
     * @return the built chunk.
     */
    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ);
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it to the target level if it doesn't exist yet.
     * @param pos the position of the chunk.
     * @return the built chunk.
     */
    public ProtoChunk getOrBuildChunk(ChunkPos pos){
        return seedChunkGenerator.getOrBuildChunk(pos.x, pos.z);
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it if it doesn't exist yet.
     * @param chunkX the x coordinate of the chunk to build in chunk coordinates.
     * @param chunkZ the z coordinate of the chunk to build in chunk coordinates.
     * @param targetLevel the targelevel of the chunk see {@link TargetState}.
     * @return a chunk built to at least the target level.
     */
    public ProtoChunk getOrBuildChunk(int chunkX, int chunkZ, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ, targetLevel);
    }

    /**
     * Get a {@link ProtoChunk} chunk from the hashmap or build it if it doesn't exist yet.
     * @param pos the chunk pos of the chunk.
     * @param targetLevel the targelevel of the chunk see {@link TargetState}.
     * @return a chunk built to at least the target level.
     */
    public ProtoChunk getOrBuildChunk(ChunkPos pos, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(pos, targetLevel);
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
        return seedChunkGenerator.getEntitiesInBox(name, box ,predicate);
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
        return seedChunkGenerator.getEntitiesInBox(name, box);
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
        return seedChunkGenerator.getEntitiesInBox(box);
    }

    /**
     * Get all {@link BlockEntity}s within a box.
     * @param box The box to search within.
     * @return A map from {@link BlockPos} to {@link BlockEntity} containing all block entities within the box.
     */
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(Box box) {
        return seedChunkGenerator.getBlockEntitiesInBox(box);
    }


    /**
     * Get all {@link BlockEntity}s within a box.
     * @param type The type of block entity to search for.
     * @param box The box to search within.
     * @return A map from {@link BlockPos} to {@link BlockEntity} containing all block entities within the box.
     */
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box){
        return seedChunkGenerator.getBlockEntitiesInBox(type, box);
    }
    /**
     * Get all {@link BlockEntity}s within a box.
     * @param type The type of block entity to search for.
     * @param box The box to search within.
     * @param predicate A more advanced filter to check if the block entity matches your criteria.
     * @return A map from {@link BlockPos} to {@link BlockEntity} containing all block entities within the box.
     */
    public Map<BlockPos, BlockEntity> getBlockEntitiesInBox(BlockEntityType type, Box box, Predicate<BlockEntity> predicate) {
        return seedChunkGenerator.getBlockEntitiesInBox(type, box, predicate);
    }

    /**
     * Count the amount of a certain {@link Block}s in an area.
     * @param block The block type to count.
     * @param box The area to search within.
     * @return The amount of blocks.
     */
    public int getBlockCountInBox(Block block, Box box){
        return seedChunkGenerator.getBlockCountInBox(block, box);
    }

    /**
     * Print a box of blocks with their coordinates and block state.
     *
     * NOTE: This method is mostly for debugging
     *       It prints it in such a way that if you look from above, it should correspond with your world
     * @param box The box to print.
     */
    public void printArea(Box box) {
        seedChunkGenerator.printArea(box);
    }

    /**
     * Get a {@link ProtoChunk} from chunk coordinates.
     *
     * NOTE: This should only be used if there is no other method. Interacting with a chunk might throw errors or cause problems.
     * These chunks are extremely powerful, but difficult to work with. Only use them if you know what you're doing.
     *
     * @param chunkX the x coordinate of the chunk.
     * @param chunkZ the z coordinate of the chunk.
     * @return the {@link ProtoChunk} at that location.
     */
    public ProtoChunk getChunk(int chunkX, int chunkZ){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ);
    }

    /**
     * Get a {@link ProtoChunk} from a {@link ChunkPos}.
     *
     * NOTE: This should only be used if there is no other method. Interacting with a chunk might throw errors or cause problems.
     * These chunks are extremely powerful, but difficult to work with. Only use them if you know what you're doing.
     *
     * @param pos the position of the chunk.
     * @return the {@link ProtoChunk} at that location.
     */
    public ProtoChunk getChunk(ChunkPos pos){
        return seedChunkGenerator.getOrBuildChunk(pos);
    }

    /**
     * Get a {@link ProtoChunk} from chunk coordinates and a target level.
     *
     * NOTE: This should only be used if there is no other method. Interacting with a chunk might throw errors or cause problems.
     * These chunks are extremely powerful, but difficult to work with. Only use them if you know what you're doing.
     *
     * @param chunkX the x coordinate of the chunk.
     * @param chunkZ the z coordinate of the chunk.
     * @param targetLevel the target level of the chunk.
     * @return the {@link ProtoChunk} at that location.
     */
    public ProtoChunk getChunk(int chunkX, int chunkZ, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(chunkX, chunkZ, targetLevel);
    }

    /**
     * Get a {@link ProtoChunk} from chunk coordinates.
     *
     * NOTE: This should only be used if there is no other method. Interacting with a chunk might throw errors or cause problems.
     * These chunks are extremely powerful, but difficult to work with. Only use them if you know what you're doing.
     *
     * @param pos the position of the chunk.
     * @param targetLevel the target level of the chunk.
     * @return the {@link ProtoChunk} at that location.
     */
    public ProtoChunk getChunk(ChunkPos pos, int targetLevel){
        return seedChunkGenerator.getOrBuildChunk(pos, targetLevel);
    }

    public ProtoChunk getChunk(BlockPos pos) {
        return getChunk(pos.getX() % 16, pos.getZ() % 16);
    }

    /**
     * Clear all the chunk data.
     */
    public void clearMemory(){
        seedChunkGenerator.clearMemory();
    }

    /**
     * Gets seed.
     * @return seed.
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Gets targetLevel.
     * @return targetLevel.
     */
    public long getTargetLevel() {
        return targetLevel;
    }
}
