package nl.jellejurre.seedchecker.serverMocks;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import nl.jellejurre.seedchecker.LazyDataFixerBuilder;
import nl.jellejurre.seedchecker.ReflectionUtils;

public class FakeLevelStorage extends LevelStorage {
    public Path savesDirectory;

    public FakeLevelStorage(Path savesDirectory, Path backupsDirectory,
                            DataFixer dataFixer) {
        super(savesDirectory, backupsDirectory, dataFixer);
    }
    private void init() {
        this.savesDirectory = Path.of("seedCheckerLib");
    }

    @Override
    public Path getSavesDirectory() {
        return Path.of("seedCheckerLib");
    }

    public static FakeLevelStorage create(Path path) {
        try {
            FakeLevelStorage fls = (FakeLevelStorage) ReflectionUtils.unsafe.allocateInstance(FakeLevelStorage.class);
            fls.init();
            return fls;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public FakeSession createSession() throws IOException {
        try {
            return (FakeSession) ReflectionUtils.unsafe.allocateInstance(FakeSession.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public class FakeSession extends LevelStorage.Session {
        public FakeSession(String directoryName) throws IOException {
            super(directoryName);
        }


        @Override
        public WorldSaveHandler createSaveHandler() {
            return null;
        }

        @Override
        public Path getDirectory(WorldSavePath savePath) {
            return Path.of("seedCheckerLib");
        }

        @Override
        public Path getWorldDirectory(RegistryKey<World> key) {
            return Path.of("seedCheckerLib");
        }

    }
}
