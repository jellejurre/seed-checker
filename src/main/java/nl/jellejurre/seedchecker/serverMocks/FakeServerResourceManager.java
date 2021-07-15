package nl.jellejurre.seedchecker.serverMocks;

import static nl.jellejurre.seedchecker.SeedCheckerSettings.registryManager;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.tag.TagManagerLoader;
import net.minecraft.util.Unit;
import net.minecraft.util.registry.DynamicRegistryManager;
import nl.jellejurre.seedchecker.ReflectionUtils;


public class FakeServerResourceManager extends ServerResourceManager{
    private static final CompletableFuture<Unit> COMPLETED_UNIT =  CompletableFuture.completedFuture(Unit.INSTANCE);
    private ReloadableResourceManager resourceManager;
    private TagManagerLoader registryTagManager;
    private LootConditionManager lootConditionManager;
    private LootManager lootManager;
    private LootFunctionManager lootFunctionManager;

    @Override
    public LootManager getLootManager() {
        return lootManager;
    }

    public FakeServerResourceManager(DynamicRegistryManager registryManager, RegistrationEnvironment commandEnvironment, int functionPermissionLevel) {
        super(registryManager, commandEnvironment, functionPermissionLevel);
    }

    public void initialise(){
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
        this.lootConditionManager = new LootConditionManager();
        this.lootManager = new LootManager(this.lootConditionManager);
        this.lootFunctionManager = new LootFunctionManager(this.lootConditionManager, this.lootManager);
        this.registryTagManager = new TagManagerLoader(registryManager);
        this.resourceManager.registerReloader(this.registryTagManager);
        this.resourceManager.registerReloader(this.lootConditionManager);
        this.resourceManager.registerReloader(this.lootManager);
        this.resourceManager.registerReloader(this.lootFunctionManager);
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    @Override
    public LootConditionManager getLootConditionManager() {
        return lootConditionManager;
    }

    public static CompletableFuture<ServerResourceManager> reload(List<ResourcePack> packs, DynamicRegistryManager registryManager, RegistrationEnvironment commandEnvironment, int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor) {
        try {
            FakeServerResourceManager serverResourceManager;
            serverResourceManager = (FakeServerResourceManager) ReflectionUtils.unsafe.allocateInstance(FakeServerResourceManager.class);
            serverResourceManager.initialise();
            CompletableFuture<Unit> completableFuture = serverResourceManager.resourceManager.reload(prepareExecutor, applyExecutor, packs, COMPLETED_UNIT);
            return completableFuture.whenComplete((unit, throwable) -> {
                if (throwable != null) {
                    serverResourceManager.close();
                }

            }).thenApply((unit) -> serverResourceManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        this.resourceManager.close();
    }

    public void loadRegistryTags() {
        this.registryTagManager.getTagManager().apply();
    }
}

