package nl.enjarai.doabarrelroll.moonlightconfigs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigSpec {

    private static final Map<String, Map<ConfigType, ConfigSpec>> CONFIG_STORAGE = new HashMap<>();


    public static void addTrackedSpec(ConfigSpec spec) {
        var map = CONFIG_STORAGE.computeIfAbsent(spec.getModId(), n -> new HashMap<>());
        map.put(spec.getConfigType(), spec);
    }

    @Nullable
    public static ConfigSpec getSpec(String modId, ConfigType type) {
        var map = CONFIG_STORAGE.get(modId);
        if (map != null) {
            return map.getOrDefault(type, null);
        }
        return null;
    }


    private final String fileName;
    private final String modId;
    private final Path filePath;
    private final ConfigType type;
    private final boolean synced;
    @Nullable
    private final Runnable changeCallback;

    public ConfigSpec(Identifier name, Path configDirectory, ConfigType type) {
        this(name, configDirectory, type, false, null);
    }

    public ConfigSpec(Identifier name, Path configDirectory, ConfigType type, boolean synced, @Nullable Runnable changeCallback) {
        this.fileName = name.getNamespace() + "-" + name.getPath() + ".json";
        this.modId = name.getNamespace();
        this.filePath = configDirectory.resolve(fileName);
        this.type = type;
        this.synced = synced;
        this.changeCallback = changeCallback;
    }

    protected void onRefresh() {
        if (this.changeCallback != null) {
            this.changeCallback.run();
        }
    }

    public boolean isLoaded() {
        return true;
    }

    ;

    public abstract void loadFromFile();

    public abstract void register();

    public ConfigType getConfigType() {
        return type;
    }

    public String getModId() {
        return modId;
    }

    public String getFileName() {
        return fileName;
    }

    public Path getFullPath() {
        return filePath;
    }

    public abstract void loadFromBytes(InputStream stream);

    public abstract void save();

    @Nullable
    @Environment(EnvType.CLIENT)
    public Screen makeScreen(Screen parent) {
        return makeScreen(parent, null);
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public abstract Screen makeScreen(Screen parent, @Nullable Identifier background);

    //serverside method
    public abstract boolean hasConfigScreen();

}
