package nl.jellejurre.seedchecker.serverMocks;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.Nullable;

public class FakeSaveProperties implements SaveProperties {
    private FakeServerWorldProperties fakeServerWorldProperties = new FakeServerWorldProperties();
    private GeneratorOptions generatorOptions;

    public FakeSaveProperties(RegistryTracker.Modifiable registryManager, long seed) {
        generatorOptions = GeneratorType.DEFAULT.method_29077(registryManager, seed, true, false);
    }

    @Override
    public DataPackSettings method_29589() {
        return null;
    }

    @Override
    public void method_29590(DataPackSettings dataPackSettings) {

    }

    @Override
    public boolean isModded() {
        return false;
    }

    @Override
    public Set<String> getServerBrands() {
        return null;
    }

    @Override
    public void addServerBrand(String brand, boolean modded) {
    }

    @Nullable
    @Override
    public CompoundTag getCustomBossEvents() {
        return null;
    }

    @Override
    public void setCustomBossEvents(@Nullable CompoundTag tag) {

    }


    @Override
    public ServerWorldProperties getMainWorldProperties() {
        return this.fakeServerWorldProperties;
    }

    @Override
    public LevelInfo getLevelInfo() {
        return null;
    }

    @Override
    public CompoundTag cloneWorldTag(RegistryTracker registryTracker,
                                     @Nullable CompoundTag compoundTag) {
        return null;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getLevelName() {
        return null;
    }

    @Override
    public GameMode getGameMode() {
        return null;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
    }

    @Override
    public boolean areCommandsAllowed() {
        return false;
    }

    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
    }

    @Override
    public boolean isDifficultyLocked() {
        return false;
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
    }

    @Override
    public GameRules getGameRules() {
        return null;
    }

    @Override
    public CompoundTag getPlayerData() {
        return null;
    }

    @Override
    public CompoundTag method_29036() {
        return null;
    }

    @Override
    public void method_29037(CompoundTag compoundTag) {

    }

    @Override
    public GeneratorOptions getGeneratorOptions() {
        return this.generatorOptions;
    }

    @Override
    public Lifecycle method_29588() {
        return null;
    }

    ;
}
