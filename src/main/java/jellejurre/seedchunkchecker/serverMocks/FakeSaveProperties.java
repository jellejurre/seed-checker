package jellejurre.seedchunkchecker.serverMocks;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.registry.DynamicRegistryManager;
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

    public FakeSaveProperties(DynamicRegistryManager.Impl registryManager, long seed) {
        generatorOptions = GeneratorType.DEFAULT.createDefaultOptions(registryManager, seed, true, false);
    }

    @Override
    public DataPackSettings getDataPackSettings() {
        return null;
    }

    @Override
    public void updateLevelInfo(DataPackSettings dataPackSettings) {
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
    public NbtCompound getCustomBossEvents() {
        return null;
    }

    @Override
    public void setCustomBossEvents(@Nullable NbtCompound nbt) {
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
    public NbtCompound cloneWorldNbt(DynamicRegistryManager registryManager,
                                     @Nullable NbtCompound playerNbt) {
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
    public NbtCompound getPlayerData() {
        return null;
    }

    @Override
    public NbtCompound getDragonFight() {
        return null;
    }

    @Override
    public void setDragonFight(NbtCompound nbt) {
    }

    @Override
    public GeneratorOptions getGeneratorOptions() {
        return this.generatorOptions;
    }

    @Override
    public Lifecycle getLifecycle() {
        return null;
    }
}
