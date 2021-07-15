import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Items;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import nl.jellejurre.seedchecker.SeedChecker;
import nl.jellejurre.seedchecker.SeedCheckerDimension;
import nl.jellejurre.seedchecker.TargetState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.junit.jupiter.api.Test;

public class Example {

    @Test
    public void Example() {
        //Set some variables as examples
        long seed = 8040347553L;

        //Instantiate our seedChecker
        //We want the target state to be full, because this ensures entity spawns
        //We're going to be testing on the overworld dimension
        SeedChecker checker = new SeedChecker(seed, TargetState.FULL, SeedCheckerDimension.OVERWORLD);

        //Get spawn point
        BlockPos spawnpoint = checker.getSpawnPos();

        //Create a box around our spawn point (can be done like this, can also be given two BlockPos')
        Box box = new Box(spawnpoint.getX() - 64, spawnpoint.getY() - 10, spawnpoint.getZ() - 64,
            spawnpoint.getX() + 64, spawnpoint.getY() + 50, spawnpoint.getZ() + 64);

        //Get the amount of wool blocks in the box
        int wool = checker.getBlockCountInBox(Blocks.WHITE_WOOL, box);

        //Get the amount of cactus blocks in the box
        int cactus = checker.getBlockCountInBox(Blocks.CACTUS, box);

        //Get the amount of iron golems in the box
        long iron_golem = checker.getEntitiesInBox("iron_golem",box).stream().count();

        //Get all blockentities in the box which are a BlockEntityType.CHEST
        Map<BlockPos, BlockEntity> blockEntities = checker.getBlockEntitiesInBox(BlockEntityType.CHEST, box);

        //Generate all chest loot
        List<ItemStack> list = new ArrayList<ItemStack>();
        for(BlockPos pos : blockEntities.keySet()){
            list.addAll(checker.generateChestLoot(pos));
        }

        //Check carrot count in chests
        int carrot = 0;
        for(ItemStack i : list){
            if(i.getItem()== Items.CARROT){
                carrot+=i.getCount();
            }
        }

        //Check our results
        assertEquals(2, iron_golem);
        assertEquals(44, cactus);
        assertEquals(38, wool);
        assertEquals(8, carrot);
        assertEquals("[3 potato, 4 carrot, 4 carrot, 3 dark_oak_log, 3 dark_oak_log, 3 dark_oak_log, 4 string, 1 experience_bottle, 3 tripwire_hook]", list.toString());
    }

    @Test
    public void ChunkExample(){
        //NOTE: This should only be used if there is no other method. Interacting with a chunk might throw errors or cause problems.
        //These chunks are extremely powerful, but difficult to work with. Only use them if you know what you're doing.

        //Set some variables as examples
        long seed = 8040347553L;

        //Instantiate our seedChecker
        SeedChecker checker = new SeedChecker(seed, TargetState.FULL, SeedCheckerDimension.OVERWORLD);

        //Get a chunk with a full target level.
        //Note that you can't get access to functionality of a target level higher than the one you call in the constructor, only lower.
        Chunk chunk = checker.getChunk(1, 2, TargetState.STRUCTURES.getLevel());

        //Get the heightmap from this chunk.
        Heightmap map = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE);

        //Check our results.
        assertEquals(69, map.get(1, 2));
    }

    @Test
    public void mapExample(){
        //Same checker, but for a test involving drawing a map, which crashed
        SeedChecker checker = new SeedChecker(-6876956224386768041L);
        Box box = new Box(-44, 48, 164, 140, 113, 348);
        Map<BlockPos, BlockEntity> blockEntities = checker.getBlockEntitiesInBox(BlockEntityType.CHEST, box);
        List<ItemStack> lootList = new ArrayList<>();
        for(BlockPos pos : blockEntities.keySet()){
            lootList.addAll(checker.generateChestLoot(pos));
        }
        assertEquals(new BlockPos(48, 63, 247), checker.getSpawnPos());
        assertEquals("[2 iron_ingot, 2 emerald, 3 iron_ingot, 1 lapis_lazuli, 8 iron_nugget, 4 iron_nugget, 7 iron_nugget, 8 iron_nugget, 1 filled_map, 1 paper, 4 feather, 10 paper, 3 coal, 1 rotten_flesh, 3 coal, 1 leather_chestplate, 5 paper, 1 moss_block, 10 paper, 2 wheat, 2 wheat, 3 wheat, 2 wheat, 1 stone_axe, 1 golden_helmet]", lootList.toString());
    }

}