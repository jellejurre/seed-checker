package nl.jellejurre.seedchecker;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

public class Main {
    public static void main(String[] args) {
        SeedChecker checker = new SeedChecker(101);
        System.out.println(checker.getSpawnPos());
        List<ItemStack> lootlist = new ArrayList<>();
        lootlist.addAll(checker.generateChestLoot(166, 70, -96));
        System.out.println(lootlist);
        System.exit(0);
    }
}