package org.example1.dfPubBin.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GarbageManager {

    private static GarbageContainer publicContainer;
    private static final Map<UUID, GarbageContainer> privateContainers = new HashMap<>();

    public static void init() {
        publicContainer = new GarbageContainer(GarbageType.PUBLIC, null);
    }

    public static GarbageContainer getPublicContainer() {
        return publicContainer;
    }

    public static GarbageContainer getPrivateContainer(UUID uuid) {
        return privateContainers.computeIfAbsent(
                uuid,
                u -> new GarbageContainer(GarbageType.PRIVATE, u)
        );
    }
    
    /**
     * 获取所有私人垃圾桶容器的映射
     */
    public static Map<UUID, GarbageContainer> getPrivateContainers() {
        return privateContainers;
    }
    
    /**
     * 清空所有垃圾桶数据
     */
    public static void clearAllContainers() {
        if (publicContainer != null) {
            publicContainer.getPages().clear();
        }
        privateContainers.clear();
    }
}
