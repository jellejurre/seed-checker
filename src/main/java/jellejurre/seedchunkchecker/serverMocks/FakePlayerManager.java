package jellejurre.seedchunkchecker.serverMocks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.WorldSaveHandler;

public class FakePlayerManager extends PlayerManager {
    public FakePlayerManager(MinecraftServer server,
                             DynamicRegistryManager.Impl registryManager,
                             WorldSaveHandler saveHandler, int maxPlayers) {
        super(server, registryManager, saveHandler, maxPlayers);
    }

    @Override
    public int getViewDistance() {
        return 0;
    }
}
