import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Items;
import net.minecraft.world.Heightmap;
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
        long seed = 101L;

        //Instantiate our seedChecker
        //We want the target state to be full, because this ensures entity spawns
        //We're going to be testing on the overworld dimension
        SeedChecker checker =
            new SeedChecker(seed, TargetState.FULL, SeedCheckerDimension.OVERWORLD);

        //Get spawn point
        BlockPos spawnpoint = checker.getSpawnPos();

        //Create a box around our spawn point (can be done like this, can also be given two BlockPos')
        Box box = new Box(spawnpoint.getX() - 64, spawnpoint.getY() - 10, spawnpoint.getZ() - 64,
            spawnpoint.getX() + 64, spawnpoint.getY() + 50, spawnpoint.getZ() + 64);

        //Get the amount of log  blocks in the box
        int logs = checker.getBlockCountInBox(Blocks.OAK_LOG, box);

        //Get the amount of cactus blocks in the box
        int dandelions = checker.getBlockCountInBox(Blocks.DANDELION, box);

        //Get the amount of iron golems in the box
        long pigs = checker.getEntitiesInBox("pig", box).stream().count();

        //Get all blockentities in the box which are a BlockEntityType.CHEST
        Map<BlockPos, BlockEntity> blockEntities =
            checker.getBlockEntitiesInBox(BlockEntityType.CHEST, box);

        //Generate all chest loot
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (BlockPos pos : blockEntities.keySet()) {
            list.addAll(checker.generateChestLoot(pos));
        }

        //Check wheat count in chests
        int wheat = 0;
        for (ItemStack i : list) {
            if (i.getItem() == Items.WHEAT) {
                wheat += i.getCount();
            }
        }

        //Check our results
        assertEquals(13, pigs);
        assertEquals(167, dandelions);
        assertEquals(55, logs);
        assertEquals(18, wheat);
        assertEquals(
            "[3 potato, 18 wheat, 1 suspicious_stew, 3 poisonous_potato, 3 coal, 1 leather_helmet, 1 leather_chestplate, 2 potato, 6 poisonous_potato]",
            list.toString());
    }

    @Test
    public void ChunkExample() {
        //NOTE: This should only be used if there is no other method. Interacting with a chunk might throw errors or cause problems.
        //These chunks are extremely powerful, but difficult to work with. Only use them if you know what you're doing.

        //Set some variables as examples
        long seed = 8040347553L;

        //Instantiate our seedChecker
        SeedChecker checker =
            new SeedChecker(seed, TargetState.FULL, SeedCheckerDimension.OVERWORLD);

        //Get a chunk with a full target level.
        //Note that you can't get access to functionality of a target level higher than the one you call in the constructor, only lower.
        Chunk chunk = checker.getChunk(6, 9, TargetState.STRUCTURES.getLevel());

        //Get the heightmap from this chunk.
        Heightmap map = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE);

        //Check our results.
        assertEquals(69, map.get(0, 4));
    }

    @Test
    public void mapExample() {
        //Same checker, but for a test involving drawing a map, which crashed
        SeedChecker checker = new SeedChecker(101);
        List<ItemStack> lootlist = new ArrayList<>();
        lootlist.addAll(checker.generateChestLoot(166, 70, -96));
        assertEquals(new BlockPos(-272, 72, -112), checker.getSpawnPos());
        assertEquals("[1 filled_map, 4 feather, 1 feather, 4 paper]", lootlist.toString());
    }
}

