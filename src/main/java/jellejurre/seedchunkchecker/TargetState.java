package jellejurre.seedchunkchecker;


//Target state of a chunk to generate to, this correlates exactly to minecraft ChunkStatus.getId(). If your option isn't in here, you have to give a number.
public enum TargetState {
    NO_STRUCTURES(7),
    STRUCTURES(8),
    WITH_MOBS(11),
    FULL(12);

    private final int level;

    TargetState(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
