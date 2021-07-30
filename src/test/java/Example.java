import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.server.Main;
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

        //Create a second box because no animals spawn within the first
        Box cowBox = new Box(200, 60, 200, 300, 80, 100);

        //Get the amount of wool blocks in the box
        int carvedPumpkins = checker.getBlockCountInBox(Blocks.CARVED_PUMPKIN, box);

        //Get the amount of cactus blocks in the box
        int planks = checker.getBlockCountInBox(Blocks.DARK_OAK_PLANKS, box);

        //Get the amount of iron golems in the box
        long cows = checker.getEntitiesInBox("cow",cowBox).stream().count();

        //Get all blockentities in the box which are a BlockEntityType.CHEST
        Map<BlockPos, BlockEntity> blockEntities = checker.getBlockEntitiesInBox(BlockEntityType.CHEST, box);

        //Generate all chest loot
        List<ItemStack> list = new ArrayList<>();
        for(BlockPos pos : blockEntities.keySet()){
            list.addAll(checker.generateChestLoot(pos));
        }

        //Check our results
        assertEquals(new BlockPos(71, 64, 72), spawnpoint);
        assertEquals(2, carvedPumpkins);
        assertEquals(347, planks);
        assertEquals(4, cows);
        assertEquals("[3 potato, 4 carrot, 4 carrot, 3 dark_oak_log, 3 dark_oak_log, 3 dark_oak_log, 4 string, 1 experience_bottle, 3 tripwire_hook]", list.toString());
    }

}