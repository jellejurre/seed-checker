package jellejurre.seedchunkchecker.serverMocks;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import jellejurre.seedchunkchecker.ReflectionUtils;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.storage.LevelStorage;

public class FakeLevelStorage extends LevelStorage {
    public Path savesDirectory;

    public FakeLevelStorage(Path savesDirectory, Path backupsDirectory,
                            DataFixer dataFixer) {
        super(savesDirectory, backupsDirectory, dataFixer);
    }
    private void init() {
        this.savesDirectory = Path.of("this.does.not.exist");
    }

    @Override
    public Path getSavesDirectory() {
        return Path.of("this.does.not.exist");
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
        public File getWorldDirectory(RegistryKey<World> key) {
            return null;
        }

        @Override
        public WorldSaveHandler createSaveHandler() {
            return null;
        }

        @Override
        public Path getDirectory(WorldSavePath savePath) {
            return Path.of("this.does.not.exist");
        }
    }
}
