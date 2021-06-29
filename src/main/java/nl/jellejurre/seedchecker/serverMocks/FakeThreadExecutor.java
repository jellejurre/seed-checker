package nl.jellejurre.seedchecker.serverMocks;

import net.minecraft.util.thread.ThreadExecutor;

public class FakeThreadExecutor extends ThreadExecutor<Runnable> {
    protected FakeThreadExecutor(String name) {
        super(name);
    }

    @Override
    protected Runnable createTask(Runnable runnable) {
        return null;
    }

    @Override
    protected boolean canExecute(Runnable task) {
        return false;
    }

    @Override
    protected Thread getThread() {
        return null;
    }
}
