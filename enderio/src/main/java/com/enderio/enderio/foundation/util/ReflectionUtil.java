package com.enderio.enderio.foundation.util;

import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class ReflectionUtil {

    private static final Field GRAPH_OBJECTS_FIELD = getGraphObjectsField();

    private static Field getGraphObjectsField() {
        try {
            var f = Graph.class.getDeclaredField("objects");
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<GraphObject<Mergeable.Dummy>, ?> getRawMap(Graph<Mergeable.Dummy> graph) {
        if(GRAPH_OBJECTS_FIELD == null) {
            return null;
        }
        try {
            return (Map<GraphObject<Mergeable.Dummy>, ?>) GRAPH_OBJECTS_FIELD.get(graph);
        } catch (Exception ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Collection<GraphObject<Mergeable.Dummy>> getRawObjects(Graph<Mergeable.Dummy> graph) {
        try {
            Map<GraphObject<Mergeable.Dummy>, ?> map = (Map<GraphObject<Mergeable.Dummy>, ?>) GRAPH_OBJECTS_FIELD.get(graph);
            return map.keySet();
        } catch (Exception ignored) {}
        return graph.getObjects(); // fallback
    }

}
