package nl.jellejurre.seedchecker.serverMocks;

import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.timer.Timer;
import org.jetbrains.annotations.Nullable;

public class FakeServerWorldProperties implements ServerWorldProperties {
    @Override
    public String getLevelName() {
        return null;
    }

    @Override
    public void setThundering(boolean thundering) {
    }

    @Override
    public int getRainTime() {
        return 0;
    }

    @Override
    public void setRainTime(int rainTime) {

    }

    @Override
    public void setThunderTime(int thunderTime) {
    }

    @Override
    public int getThunderTime() {
        return 0;
    }

    @Override
    public int getClearWeatherTime() {
        return 0;
    }

    @Override
    public void setClearWeatherTime(int clearWeatherTime) {
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
    }

    @Nullable
    @Override
    public UUID getWanderingTraderId() {
        return null;
    }

    @Override
    public void setWanderingTraderId(UUID uuid) {
    }

    @Override
    public GameMode getGameMode() {
        return null;
    }

    @Override
    public void setWorldBorder(WorldBorder.Properties properties) {
    }

    @Override
    public WorldBorder.Properties getWorldBorder() {
        return null;
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void setInitialized(boolean initialized) {
    }

    @Override
    public boolean areCommandsAllowed() {
        return false;
    }

    @Override
    public void setGameMode(GameMode gameMode) {
    }

    @Override
    public Timer<MinecraftServer> getScheduledEvents() {
        return null;
    }

    @Override
    public void setTime(long time) {
    }

    @Override
    public void setTimeOfDay(long timeOfDay) {
    }

    @Override
    public void setSpawnX(int spawnX) {
    }

    @Override
    public void setSpawnY(int spawnY) {
    }

    @Override
    public void setSpawnZ(int spawnZ) {
    }

    @Override
    public void setSpawnAngle(float angle) {
    }

    @Override
    public int getSpawnX() {
        return 0;
    }

    @Override
    public int getSpawnY() {
        return 0;
    }

    @Override
    public int getSpawnZ() {
        return 0;
    }

    @Override
    public float getSpawnAngle() {
        return 0;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public long getTimeOfDay() {
        return 0;
    }

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public void setRaining(boolean raining) {
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public GameRules getGameRules() {
        return null;
    }

    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @Override
    public boolean isDifficultyLocked() {
        return false;
    }
}
