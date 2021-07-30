package nl.jellejurre.seedchecker.serverMocks;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.WorldSaveHandler;

public class FakePlayerManager extends PlayerManager {
    public FakePlayerManager(MinecraftServer server,
                             WorldSaveHandler saveHandler, int maxPlayers) {
        super(server, RegistryTracker.create(), saveHandler, maxPlayers);
    }

    @Override
    public int getViewDistance() {
        return 0;
    }
}
