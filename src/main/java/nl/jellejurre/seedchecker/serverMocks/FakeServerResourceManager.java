package nl.jellejurre.seedchecker.serverMocks;

import static nl.jellejurre.seedchecker.SeedCheckerSettings.registryManager;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.registry.RegistryTracker;
import nl.jellejurre.seedchecker.ReflectionUtils;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.util.Unit;


public class FakeServerResourceManager extends ServerResourceManager{
    private static final CompletableFuture<Unit> COMPLETED_UNIT =  CompletableFuture.completedFuture(Unit.INSTANCE);
    private ReloadableResourceManager resourceManager;
    private RecipeManager recipeManager;
    private RegistryTagManager registryTagManager;
    private LootConditionManager lootConditionManager;
    private LootManager lootManager;
    @Override
    public LootManager getLootManager() {
        return lootManager;
    }

    public FakeServerResourceManager(int functionPermissionLevel) {
        super(RegistrationEnvironment.DEDICATED, functionPermissionLevel);
    }

    public void initialise(){
        this.resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
        this.recipeManager = new RecipeManager();
        this.registryTagManager = new RegistryTagManager();
        this.lootConditionManager = new LootConditionManager();
        this.lootManager = new LootManager(this.lootConditionManager);
        this.resourceManager.registerListener(this.registryTagManager);
        this.resourceManager.registerListener(this.lootConditionManager);
        this.resourceManager.registerListener(this.recipeManager);
        this.resourceManager.registerListener(this.lootManager);
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    @Override
    public LootConditionManager getLootConditionManager() {
        return lootConditionManager;
    }

    public static CompletableFuture<ServerResourceManager> reload(List<ResourcePack> packs, RegistryTracker.Modifiable registryManager, RegistrationEnvironment commandEnvironment, int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor) {
        try {
            FakeServerResourceManager serverResourceManager;
            serverResourceManager = (FakeServerResourceManager) ReflectionUtils.unsafe.allocateInstance(FakeServerResourceManager.class);
            serverResourceManager.initialise();
            CompletableFuture<Unit> completableFuture = serverResourceManager.resourceManager.beginReload(prepareExecutor, applyExecutor, packs, COMPLETED_UNIT);
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
        this.registryTagManager.apply();
    }
}

