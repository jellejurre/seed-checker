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
import net.minecraft.util.SystemDetails;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.GameMode;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import nl.jellejurre.seedchecker.ReflectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class FakeMinecraftServer extends MinecraftServer {
    public FakePlayerManager playerManager;

    static  Logger LOGGER = LogManager.getLogger();
    public static  float field_33212 = 0.8F;
    public static  int field_33213 = 100;
    public static  int field_33206 = 50;
    public static  int field_33214 = 6000;
    public static  int field_33215 = 2000;
    public static  int field_33216 = 15000;
    public static  String LEVEL_PROTOCOL_NAME = "level";
    public static  String LEVEL_PROTOCOL = "level://";
    public static  long PLAYER_SAMPLE_UPDATE_INTERVAL = 5000000000L;
    public static  int field_33218 = 12;
    public static  String RESOURCES_ZIP_FILE_NAME = "resources.zip";
    public static  File USER_CACHE_FILE = new File("usercache.json");
    public static  int START_TICKET_CHUNK_RADIUS = 11;
    public static  int START_TICKET_CHUNKS = 441;
    public static  int field_33220 = 6000;
    public static  int field_33221 = 3;
    public static  int MAX_WORLD_BORDER_RADIUS = 29999984;
    public static  LevelInfo DEMO_LEVEL_INFO;
    public static  long MILLISECONDS_PER_TICK = 50L;
    protected  LevelStorage.Session session;
    protected  WorldSaveHandler saveHandler;
    public  Snooper snooper = new Snooper("server", this, Util.getMeasuringTimeMs());
    public  List<Runnable> serverGuiTickables = Lists.newArrayList();
    public Recorder tickTimeTracker;
    public Profiler profiler;
    public Consumer<ProfileResult> field_33975;
    public Consumer<Path> field_33976;
    public boolean field_33977;
    @Nullable
    public FakeMinecraftServer.class_6414 field_33978;
    public boolean profilerEnabled;
    public  ServerNetworkIo networkIo;
    public  WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
    public  ServerMetadata metadata;
    public  Random random;
    public  DataFixer dataFixer;
    public String serverIp;
    public int serverPort;
    protected  DynamicRegistryManager.Impl registryManager;
    public  Map<RegistryKey<World>, ServerWorld> worlds;
    public volatile boolean running;
    public boolean stopped;
    public int ticks;
    protected  Proxy proxy;
    public boolean onlineMode;
    public boolean preventProxyConnections;
    public boolean pvpEnabled;
    public boolean flightEnabled;
    @Nullable
    public String motd;
    public int playerIdleTimeout;
    public  long[] lastTickLengths;
    @Nullable
    public KeyPair keyPair;
    @Nullable
    public String userName;
    public boolean demo;
    public String resourcePackUrl;
    public String resourcePackHash;
    public volatile boolean loading;
    public long lastTimeReference;
    public  MinecraftSessionService sessionService;
    @Nullable
    public  GameProfileRepository gameProfileRepo;
    @Nullable
    public  UserCache userCache;
    public long lastPlayerSampleUpdate;
    public  Thread serverThread;
    public long timeReference;
    public long nextTickTimestamp;
    public boolean waitingForNextTick;
    public boolean iconFilePresent;
    public  ResourcePackManager dataPackManager;
    public  ServerScoreboard scoreboard;
    @Nullable
    public DataCommandStorage dataCommandStorage;
    public  BossBarManager bossBarManager;
    public  CommandFunctionManager commandFunctionManager;
    public  MetricsData metricsData;
    public boolean enforceWhitelist;
    public float tickTime;
    public  Executor workerExecutor;
    @Nullable
    public String serverId;
    public ServerResourceManager serverResourceManager;
    public  StructureManager structureManager;
    public  SaveProperties saveProperties;

    public FakeMinecraftServer(DynamicRegistryManager.Impl registryManager, LevelStorage.Session levelStorageSession, SaveProperties saveProperties,
                               ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache) {
        super(null, registryManager, levelStorageSession, saveProperties, resourcePackManager, Proxy.NO_PROXY, Schemas
            .getFixer(), serverResourceManager, minecraftSessionService, gameProfileRepository, userCache, WorldGenerationProgressLogger::new);

        this.playerManager = new FakePlayerManager(this, null, null, 101);
    }
    
    public static FakeMinecraftServer getMinecraftServer(DynamicRegistryManager.Impl registryManager, LevelStorage.Session levelStorageSession, SaveProperties saveProperties,
                                                         ResourcePackManager resourcePackManager, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache){
        try {
            FakeMinecraftServer fakeMinecraftServer;
            fakeMinecraftServer = (FakeMinecraftServer) ReflectionUtils.unsafe.allocateInstance(FakeMinecraftServer.class);
            fakeMinecraftServer.initialise(null, registryManager, levelStorageSession, saveProperties, resourcePackManager, Proxy.NO_PROXY, Schemas
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
    public SystemDetails addExtraSystemDetails(SystemDetails details) {
        return null;
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return registryManager;
    }

    public void initialise(Thread serverThread, DynamicRegistryManager.Impl registryManager, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager dataPackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, @Nullable MinecraftSessionService sessionService, @Nullable GameProfileRepository gameProfileRepo, @Nullable UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory){
        this.tickTimeTracker = DummyRecorder.INSTANCE;
        this.profiler = this.tickTimeTracker.getProfiler();
        this.field_33975 = (profileResult) -> {
            this.resetRecorder();
        };
        this.field_33976 = (path) -> {
        };
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
        this.registryManager = registryManager;
        this.saveProperties = saveProperties;
        this.proxy = proxy;
        this.dataPackManager = dataPackManager;
        this.serverResourceManager = serverResourceManager;
        this.sessionService = sessionService;
        this.gameProfileRepo = gameProfileRepo;
        this.userCache = userCache;
        if (userCache != null) {
            userCache.setExecutor(this);
        }

        this.networkIo = new ServerNetworkIo(this);
        this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
        this.session = session;
        this.saveHandler = session.createSaveHandler();
        this.dataFixer = dataFixer;
        this.structureManager = new StructureManager(serverResourceManager.getResourceManager(), session, dataFixer);
        this.serverThread = serverThread;
        this.workerExecutor = Util.getMainWorkerExecutor();
        this.playerManager = new FakePlayerManager(this, null, null, 101);
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
    public int getRateLimit() {
        return 0;
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
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public boolean cannotBeSilenced() {
        return super.cannotBeSilenced();
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
    public <Source> CompletableFuture<Source> askFallible(
        Function<? super MessageListener<Either<Source, Exception>>, ? extends ServerTask> messageProvider) {
        return super.askFallible(messageProvider);
    }


    public static class class_6414 {
        final long field_33980;
        final int field_33981;

        class_6414(long l, int i) {
            this.field_33980 = l;
            this.field_33981 = i;
        }

        ProfileResult method_37330(long l, int i) {
            return new ProfileResult() {
                public List<ProfilerTiming> getTimings(String parentPath) {
                    return Collections.emptyList();
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

                public String getRootTimings() {
                    return "";
                }
            };
        }
    }
}
