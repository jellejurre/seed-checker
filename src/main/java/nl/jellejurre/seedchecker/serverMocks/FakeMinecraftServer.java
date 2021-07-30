package nl.jellejurre.seedchecker.serverMocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.TickTimeTracker;
import net.minecraft.util.registry.RegistryTracker;
import nl.jellejurre.seedchecker.ReflectionUtils;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.datafixer.Schemas;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerMetadata;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.server.ServerTask;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.MetricsData;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class FakeMinecraftServer extends MinecraftServer {
    public FakePlayerManager playerManager;
    public static  File USER_CACHE_FILE = new File("usercache.json");
    protected  LevelStorage.Session session;
    protected  WorldSaveHandler field_24371;
    private  Snooper snooper = new Snooper("server", this, Util.getMeasuringTimeMs());
    private  List<Runnable> serverGuiTickables = Lists.newArrayList();
    private TickTimeTracker tickTimeTracker;
    private Profiler profiler;
    private  ServerNetworkIo networkIo;
    private  WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    private  ServerMetadata metadata;
    private  Random random;
    private  DataFixer dataFixer;
    private String serverIp;
    private int serverPort;
    protected  RegistryTracker.Modifiable dimensionTracker;
    private  Map<RegistryKey<World>, ServerWorld> worlds;
    private volatile boolean running;
    private boolean stopped;
    private int ticks;
    protected  Proxy proxy;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean pvpEnabled;
    private boolean flightEnabled;
    @Nullable
    private String motd;
    private int worldHeight;
    private int playerIdleTimeout;
    public  long[] lastTickLengths;
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private String userName;
    private boolean demo;
    private String resourcePackUrl;
    private String resourcePackHash;
    private volatile boolean loading;
    private long lastTimeReference;
    private boolean profilerStartQueued;
    private boolean forceGameMode;
    private  MinecraftSessionService sessionService;
    private  GameProfileRepository gameProfileRepo;
    private  UserCache userCache;
    private long lastPlayerSampleUpdate;
    private  Thread serverThread;
    private long timeReference;
    private long field_19248;
    private boolean waitingForNextTick;
    @Environment(EnvType.CLIENT)
    private boolean iconFilePresent;
    private  ResourcePackManager<ResourcePackProfile> dataPackManager;
    private  ServerScoreboard scoreboard;
    @Nullable
    private DataCommandStorage dataCommandStorage;
    private  BossBarManager bossBarManager;
    private  CommandFunctionManager commandFunctionManager;
    private  MetricsData metricsData;
    private boolean enforceWhitelist;
    private float tickTime;
    private  Executor workerExecutor;
    @Nullable
    private String serverId;
    private ServerResourceManager serverResourceManager;
    private  StructureManager structureManager;
    protected  SaveProperties saveProperties;

    public FakeMinecraftServer(Thread thread, RegistryTracker.Modifiable modifiable, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager<ResourcePackProfile> resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(thread, modifiable, session, saveProperties, resourcePackManager, proxy, dataFixer, serverResourceManager, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
        this.tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, this::getTicks);
        this.profiler = DummyProfiler.INSTANCE;
        this.metadata = new ServerMetadata();
        this.random = new Random();
        this.serverPort = -1;
        this.worlds = Maps.newLinkedHashMap();
        this.running = true;
        this.lastTickLengths = new long[100];
        this.resourcePackUrl = "";
        this.resourcePackHash = "";
        this.timeReference = Util.getMeasuringTimeMs();
        this.scoreboard = new ServerScoreboard(this);
        this.bossBarManager = new BossBarManager();
        this.metricsData = new MetricsData();
        this.dimensionTracker = modifiable;
        this.saveProperties = saveProperties;
        this.proxy = proxy;
        this.dataPackManager = resourcePackManager;
        this.serverResourceManager = serverResourceManager;
        this.sessionService = minecraftSessionService;
        this.gameProfileRepo = gameProfileRepository;
        this.userCache = userCache;
        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
        this.session = session;
        this.field_24371 = session.method_27427();
        this.dataFixer = dataFixer;
        this.commandFunctionManager = new CommandFunctionManager(this, serverResourceManager.getFunctionLoader());
        this.structureManager = new StructureManager(serverResourceManager.getResourceManager(), session, dataFixer);
        this.serverThread = thread;
        this.workerExecutor = Util.getServerWorkerExecutor();

        this.playerManager = new FakePlayerManager(this, null, 101);
    }
    
    public static FakeMinecraftServer getMinecraftServer(LevelStorage.Session levelStorageSession, SaveProperties saveProperties,
                                                         ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache){
        try {
            FakeMinecraftServer fakeMinecraftServer;
            fakeMinecraftServer = (FakeMinecraftServer) ReflectionUtils.unsafe.allocateInstance(FakeMinecraftServer.class);
            fakeMinecraftServer.initialise(null, levelStorageSession, saveProperties, resourcePackManager, Proxy.NO_PROXY, Schemas
                .getFixer(), serverResourceManager, minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new);
            return fakeMinecraftServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GameMode getDefaultGameMode() {
        return this.saveProperties.getGameMode();
    }



    @Override
    public StructureManager getStructureManager() {
        return structureManager;
    }

    @Override
    public void executeTask(ServerTask task) {
        task.run();
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    public void initialise(Thread serverThread, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager dataPackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, @Nullable MinecraftSessionService sessionService, @Nullable GameProfileRepository gameProfileRepo, @Nullable UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory){
        this.tickTimeTracker = new TickTimeTracker(Util.nanoTimeSupplier, this::getTicks);
        this.profiler = this.tickTimeTracker.getProfiler();
        this.metadata = new ServerMetadata();
        this.random = new Random();
        this.serverPort = -1;
        this.worlds = Maps.newLinkedHashMap();
        this.running = true;
        this.lastTickLengths = new long[100];
        this.resourcePackUrl = "";
        this.resourcePackHash = "";
        this.timeReference = Util.getMeasuringTimeMs();
        this.scoreboard = new ServerScoreboard(this);
        this.bossBarManager = new BossBarManager();
        this.metricsData = new MetricsData();
        this.saveProperties = saveProperties;
        this.proxy = proxy;
        this.dataPackManager = dataPackManager;
        this.serverResourceManager = serverResourceManager;
        this.sessionService = sessionService;
        this.gameProfileRepo = gameProfileRepo;
        this.userCache = userCache;
        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
        this.session = session;
        this.dataFixer = dataFixer;
        this.structureManager = new StructureManager(serverResourceManager.getResourceManager(), session, dataFixer);
        this.serverThread = serverThread;
        this.playerManager = new FakePlayerManager(this, null, 101);
    }

    @Override
    public SaveProperties getSaveProperties() {
        return saveProperties;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    protected boolean setupServer() throws IOException {
        return false;
    }

    @Override
    public int getOpPermissionLevel() {
        return 4;
    }

    @Override
    public int getFunctionPermissionLevel() {
        return 3;
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return false;
    }

    @Override
    public Optional<String> getModdedStatusMessage() {
        return Optional.empty();
    }

    @Override
    public boolean isDedicated() {
        return false;
    }

    @Override
    public boolean isUsingNativeTransport() {
        return false;
    }

    @Override
    public boolean areCommandBlocksEnabled() {
        return false;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean openToLan(GameMode gameMode, boolean cheatsAllowed, int port) {
        return false;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public LootManager getLootManager() {
        return serverResourceManager.getLootManager();
    }

    @Override
    public LootConditionManager getPredicateManager() {
        return serverResourceManager.getLootConditionManager();
    }



    @Override
    public boolean isHost(GameProfile profile) {
        return false;
    }

    @Override
    public <Source> CompletableFuture<Source> ask(
        Function<? super MessageListener<Source>, ? extends ServerTask> messageProvider) {
        return super.ask(messageProvider);
    }

    @Override
    public <Source> CompletableFuture<Source> method_27918(
        Function<? super MessageListener<Either<Source, Exception>>, ? extends ServerTask> function) {
        return super.method_27918(function);
    }


    public static class class_6414 {
         long field_33980;
         int field_33981;

        class_6414(long l, int i) {
            this.field_33980 = l;
            this.field_33981 = i;
        }

        ProfileResult method_37330(long l, int i) {
            return new ProfileResult() {
                public List<ProfilerTiming> getTimings(String parentPath) {
                    return Collections.emptyList();
                }

                @Override
                public boolean save(File file) {
                    return false;
                }

                public boolean save(Path path) {
                    return false;
                }

                public long getStartTime() {
                    return FakeMinecraftServer.class_6414.this.field_33980;
                }

                public int getStartTick() {
                    return FakeMinecraftServer.class_6414.this.field_33981;
                }

                public long getEndTime() {
                    return l;
                }

                public int getEndTick() {
                    return i;
                }

                @Override
                public long getTimeSpan() {
                    return ProfileResult.super.getTimeSpan();
                }

                @Override
                public int getTickSpan() {
                    return ProfileResult.super.getTickSpan();
                }

                public String getRootTimings() {
                    return "";
                }
            };
        }
    }
}
