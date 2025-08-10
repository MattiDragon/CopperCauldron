package dev.mattidragon.coppercauldron.client.render.content;

import dev.mattidragon.coppercauldron.behaviour.CauldronContent;
import dev.mattidragon.coppercauldron.behaviour.CauldronContentType;

import java.util.HashMap;
import java.util.Map;

public class CauldronContentRenderers {
    private static final Map<CauldronContentType<?>, CauldronContentRenderer<?>> RENDERERS = new HashMap<>();

    private CauldronContentRenderers() {
    }

    public static <T extends CauldronContent> void register(CauldronContentType<T> type, CauldronContentRenderer<T> renderer) {
        RENDERERS.put(type, renderer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CauldronContent> CauldronContentRenderer<T> getRenderer(T content) {
        return (CauldronContentRenderer<T>) RENDERERS.get(content.type());
    }

    public static void registerDefaultRenderers() {
        register(CauldronContentType.EMPTY, new EmptyCauldronContentRenderer<>());
        register(CauldronContentType.FLUID, new FluidCauldronContentRenderer());
    }
}
