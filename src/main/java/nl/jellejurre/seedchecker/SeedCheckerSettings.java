package nl.jellejurre.seedchecker;

import java.io.PrintStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.level.storage.LevelStorage;
import nl.jellejurre.seedchecker.serverMocks.FakeLogger;
import nl.jellejurre.seedchecker.serverMocks.FakeServerResourceManager;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;

public class SeedCheckerSettings {
    public static RegistryTracker.Modifiable registryManager;
    public static ServerResourceManager serverResourceManager;
    public static ResourcePackManager resourcePackManager;
    public static ResourceManager resourceManager;
    private static boolean init = false;

    static{
        initialise();
    }

    public SeedCheckerSettings() {

    }

    //We use an initialise method instead of doing this on import to save time for when people import this class but don't use it,
    //since this method takes literal seconds
    public static void initialise(){
        if(init==false) {
            //Remove the loggers, we don't want log files
            LogManager.setFactory(new LoggerContextFactory() {
                @Override
                public LoggerContext getContext(String fqcn, ClassLoader loader,
                                                Object externalContext, boolean currentContext) {
                    return new LoggerContext() {
                        @Override
                        public Object getExternalContext() {
                            return null;
                        }

                        @Override
                        public ExtendedLogger getLogger(String name) {
                            return new FakeLogger();
                        }

                        @Override
                        public ExtendedLogger getLogger(String name,
                                                        MessageFactory messageFactory) {
                            return new FakeLogger();
                        }

                        @Override
                        public boolean hasLogger(String name) {
                            return true;
                        }

                        @Override
                        public boolean hasLogger(String name, MessageFactory messageFactory) {
                            return true;
                        }

                        @Override
                        public boolean hasLogger(String name,
                                                 Class<? extends MessageFactory> messageFactoryClass) {
                            return true;
                        }
                    };
                }

                @Override
                public LoggerContext getContext(String fqcn, ClassLoader loader,
                                                Object externalContext, boolean currentContext,
                                                URI configLocation, String name) {
                    return null;
                }

                @Override
                public void removeContext(LoggerContext context) {

                }
            });
            //Temporarily set the System.out to not display, since the initialisation prints lines, which we don't like.
            PrintStream outStream = System.out;
            System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) {}
            }) {
                @Override public void flush() {}
                @Override public void close() {}
                @Override public void write(int b) {}
                @Override public void write(byte[] b) {}
                @Override public void write(byte[] buf, int off, int len) {}
                @Override public void print(boolean b) {}
                @Override public void print(char c) {}
                @Override public void print(int i) {}
                @Override public void print(long l) {}
                @Override public void print(float f) {}
                @Override public void print(double d) {}
                @Override public void print(char[] s) {}
                @Override public void print(String s) {}
                @Override public void print(Object obj) {}
                @Override public void println() {}
                @Override public void println(boolean x) {}
                @Override public void println(char x) {}
                @Override public void println(int x) {}
                @Override public void println(long x) {}
                @Override public void println(float x) {}
                @Override public void println(double x) {}
                @Override public void println(char[] x) {}
                @Override public void println(String x) {}
                @Override public void println(Object x) {}
                @Override public java.io.PrintStream printf(String format, Object... args) { return this; }
                @Override public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) { return this; }
                @Override public java.io.PrintStream format(String format, Object... args) { return this; }
                @Override public java.io.PrintStream format(java.util.Locale l, String format, Object... args) { return this; }
                @Override public java.io.PrintStream append(CharSequence csq) { return this; }
                @Override public java.io.PrintStream append(CharSequence csq, int start, int end) { return this; }
                @Override public java.io.PrintStream append(char c) { return this; }
            });
            //Setup our ResourceManagers and constants
            SharedConstants.getGameVersion();
            ReflectionUtils.setValueOfStaticField(Bootstrap.class, "initialized", true);
            registryManager = RegistryTracker.create();
            try {
                resourcePackManager = new ResourcePackManager(ResourcePackProfile::new,
                    new VanillaDataPackProvider());
                MinecraftServer.loadDataPacks(resourcePackManager, DataPackSettings.SAFE_MODE, true);
//                public ResourcePackManager(ResourcePackProfile.Factory profileFactory,
//                    ResourcePackProvider ... providers) {
//                    this.profileFactory = profileFactory;
//                    this.providers = ImmutableSet.copyOf(providers);
//                }
//
//                public ResourcePackManager(ResourceType arg, ResourcePackProvider ... args) {
//                                this((String string, Text arg2, boolean bl, Supplier<ResourcePack> supplier, PackResourceMetadata arg3, ResourcePackProfile.InsertionPosition arg4, ResourcePackSource arg5)
//                                    -> new ResourcePackProfile(string, arg2, bl, supplier, arg3, arg, arg4, arg5), args);
//                }

                serverResourceManager = (FakeServerResourceManager) FakeServerResourceManager
                    .reload(resourcePackManager.createResourcePacks(), RegistryTracker.create(), null, 3,
                        Util.getServerWorkerExecutor(),
                        Util.getServerWorkerExecutor()
                    ).get();
            } catch (ExecutionException | InterruptedException ex) {
                System.out.println("Couldn't reload FakeServerResourceManager");
                ex.printStackTrace();
            }
            serverResourceManager.loadRegistryTags();
            resourceManager = serverResourceManager.getResourceManager();
            //Don't run this function again
            init=true;
            //Set the system.out back to the normal state
            System.setOut(outStream);
        }
    }
}
