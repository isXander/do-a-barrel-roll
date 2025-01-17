package nl.enjarai.doabarrelroll.moonlightconfigs.fabric;

import nl.enjarai.doabarrelroll.moonlightconfigs.ConfigBuilder;
import nl.enjarai.doabarrelroll.moonlightconfigs.ConfigType;
import nl.enjarai.doabarrelroll.moonlightconfigs.fabric.values.*;
import nl.enjarai.doabarrelroll.util.CombinedValue;
import nl.enjarai.doabarrelroll.util.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import net.minecraft.util.Identifier;

/**
 * Author: MehVhadJukaar
 */
public class ConfigBuilderImpl extends ConfigBuilder {

    public static ConfigBuilder create(Identifier name, ConfigType type) {
        return new ConfigBuilderImpl(name, type);
    }

    private final ConfigSubCategory mainCategory = new ConfigSubCategory(this.getName().getNamespace());

    private final Stack<ConfigSubCategory> categoryStack = new Stack<>();

    public ConfigBuilderImpl(Identifier name, ConfigType type) {
        super(name, type);
        categoryStack.push(mainCategory);
    }

    @NotNull
    public FabricConfigSpec build() {
        assert categoryStack.size() == 1;
        FabricConfigSpec spec = new FabricConfigSpec(this.getName(),
                mainCategory, this.type, this.synced, this.changeCallback);
        spec.loadFromFile();
        spec.save();
        return spec;
    }

    @Override
    protected String currentCategory() {
        return categoryStack.peek().getName();
    }

    @Override
    public ConfigBuilderImpl push(String name) {
        var cat = new ConfigSubCategory(name);
        cat.setTranslationKey(categoryTranslationKey(name));
        categoryStack.peek().addEntry(cat);
        categoryStack.push(cat);
        return this;
    }

    @Override
    public ConfigBuilderImpl pop() {
        assert categoryStack.size() != 1;
        categoryStack.pop();
        return this;
    }

    private void doAddConfig(String name, ConfigValue<?> config) {
        config.setTranslationKey(this.translationKey(name));
        maybeAddTranslationString(name);
        if (currentlyDescription) {
            var tooltipKey = this.tooltipKey(name);
            config.setDescriptionKey(tooltipKey);
            currentlyDescription = false;
        }

        this.categoryStack.peek().addEntry(config);
    }


    @Override
    public Value<Boolean> define(String name, boolean defaultValue) {
        var config = new BoolConfigValue(name, defaultValue);
        doAddConfig(name, config);
        return config;
    }


    @Override
    public Value<Double> define(String name, double defaultValue, double min, double max) {
        var config = new DoubleConfigValue(name, defaultValue, min, max);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public Value<Integer> define(String name, int defaultValue, int min, int max) {
        var config = new IntConfigValue(name, defaultValue, min, max);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public Value<Integer> defineColor(String name, int defaultValue) {
        var config = new ColorConfigValue(name, defaultValue);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public Value<String> define(String name, String defaultValue, Predicate<Object> validator) {
        var config = new StringConfigValue(name, defaultValue, validator);
        doAddConfig(name, config);
        return config;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends String> Value<List<String>> define(String name, List<? extends T> defaultValue, Predicate<Object> predicate) {
        var config = new ListStringConfigValue<>(name, (List<String>) defaultValue, predicate);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public <V extends Enum<V>> Value<V> define(String name, V defaultValue) {
        var config = new EnumConfigValue<>(name, defaultValue);
        doAddConfig(name, config);
        return config;
    }

    @Override
    public <T> Value<List<? extends T>> defineForgeList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return new CombinedValue<>(value -> {}, () -> defaultValue);
    }

    @Override
    protected void maybeAddTranslationString(String name) {
        comments.put(this.translationKey(name), getReadableName(name));
        super.maybeAddTranslationString(name);
    }

}
