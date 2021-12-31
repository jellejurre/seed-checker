package nl.jellejurre.seedchecker.serverMocks;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.tools.Generate;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.core.LoggerContext;

public class FakeLoggerContext extends LoggerContext {
    public FakeLoggerContext() {
        super("fake");
    }

    @Override
    public Object getExternalContext() {
        return null;
    }

    @Override
    public Logger getLogger(String name) {
        return FakeLogger.createFakeLogger();
    }

    @Override
    public Logger getLogger(String name,
                                    MessageFactory messageFactory) {
        return FakeLogger.createFakeLogger();
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
}
