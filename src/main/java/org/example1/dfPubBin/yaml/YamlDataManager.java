package org.example1.dfPubBin.yaml;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.example1.dfPubBin.DfPubBinPlugin;
import org.example1.dfPubBin.data.GarbageContainer;
import org.example1.dfPubBin.data.GarbageManager;
import org.example1.dfPubBin.data.GarbagePage;
import org.example1.dfPubBin.data.GarbageType;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class YamlDataManager {

    private final DfPubBinPlugin plugin;
    private FileConfiguration dataConfig;
    private java.io.File dataFile;

    public YamlDataManager(DfPubBinPlugin plugin) {
        this.plugin = plugin;
        saveDefaultDataConfig();
    }

    public void saveDefaultDataConfig() {
        if (dataFile == null) {
            dataFile = new java.io.File(plugin.getDataFolder(), "garbage_data.yml");
        }
        // 如果文件不存在，则创建一个空的配置文件
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                // 忽略创建文件的错误
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public FileConfiguration getDataConfig() {
        if (dataConfig == null) {
            saveDefaultDataConfig();
        }
        return dataConfig;
    }

    public void saveDataConfig() {
        if (dataConfig == null || dataFile == null) {
            return;
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            // 忽略保存错误
        }
    }

    /**
     * 保存所有垃圾桶数据到YAML文件
     */
    public void saveAllData() {
        // 清空现有数据
        getDataConfig().set("garbage_containers", null);

        // 保存公共垃圾桶数据
        GarbageContainer publicContainer = GarbageManager.getPublicContainer();
        saveContainerToYaml(publicContainer, GarbageType.PUBLIC, null);

        // 保存所有私人垃圾桶数据
        for (Map.Entry<UUID, GarbageContainer> entry : GarbageManager.getPrivateContainers().entrySet()) {
            saveContainerToYaml(entry.getValue(), GarbageType.PRIVATE, entry.getKey());
        }

        saveDataConfig();
    }

    /**
     * 保存单个垃圾桶容器到YAML
     */
    private void saveContainerToYaml(GarbageContainer container, GarbageType type, UUID playerUUID) {
        String containerKey = "garbage_containers." + type.name().toLowerCase();
        if (type == GarbageType.PRIVATE && playerUUID != null) {
            containerKey += "." + playerUUID.toString();
        }

        for (Map.Entry<Integer, GarbagePage> pageEntry : container.getPages().entrySet()) {
            int pageIndex = pageEntry.getKey();
            GarbagePage page = pageEntry.getValue();

            String pageKey = containerKey + ".pages." + pageIndex;

            for (int slotIndex = 0; slotIndex < page.getSize(); slotIndex++) {
                ItemStack item = page.getItem(slotIndex);
                if (item != null && item.getType() != Material.AIR) {
                    String itemKey = pageKey + "." + slotIndex;
                    getDataConfig().set(itemKey + ".item_data", serializeItemStack(item));

                    // 保存物品所有者
                    UUID itemOwner = page.getItemOwner(slotIndex);
                    if (itemOwner != null) {
                        getDataConfig().set(itemKey + ".item_owner", itemOwner.toString());
                    }

                    // 保存丢弃时间
                    Long dropTime = page.getItemDropTime(slotIndex);
                    if (dropTime != null) {
                        getDataConfig().set(itemKey + ".drop_time", dropTime);
                    }
                }
            }
        }
    }

    /**
     * 从YAML文件加载所有垃圾桶数据
     */
    public void loadAllData() {
        // 检查文件是否存在且不为空
        if (!dataFile.exists() || dataFile.length() == 0) {
            // 如果文件不存在或为空，清空所有垃圾桶数据
            GarbageManager.clearAllContainers();
            return;
        }
        
        // 文件存在，清空现有垃圾桶数据
        GarbageManager.clearAllContainers();

        // 加载公共垃圾桶
        loadContainerFromYaml(GarbageType.PUBLIC, null);

        // 加载私人垃圾桶
        if (getDataConfig().contains("garbage_containers." + GarbageType.PRIVATE.name().toLowerCase())) {
            for (String playerUUIDStr : getDataConfig().getConfigurationSection("garbage_containers." + GarbageType.PRIVATE.name().toLowerCase()).getKeys(false)) {
                UUID playerUUID = UUID.fromString(playerUUIDStr);
                loadContainerFromYaml(GarbageType.PRIVATE, playerUUID);
            }
        }
    }

    /**
     * 从YAML加载单个垃圾桶容器
     */
    private void loadContainerFromYaml(GarbageType type, UUID playerUUID) {
        String containerKey = "garbage_containers." + type.name().toLowerCase();
        if (type == GarbageType.PRIVATE && playerUUID != null) {
            containerKey += "." + playerUUID.toString();
        }

        if (!getDataConfig().contains(containerKey)) {
            return;
        }

        GarbageContainer container;
        if (type == GarbageType.PUBLIC) {
            container = GarbageManager.getPublicContainer();
        } else {
            container = GarbageManager.getPrivateContainer(playerUUID);
        }

        if (getDataConfig().contains(containerKey + ".pages")) {
            for (String pageIndexStr : getDataConfig().getConfigurationSection(containerKey + ".pages").getKeys(false)) {
                int pageIndex = Integer.parseInt(pageIndexStr);
                GarbagePage page = container.getOrCreatePage(pageIndex, 45);

                for (String slotIndexStr : getDataConfig().getConfigurationSection(containerKey + ".pages." + pageIndexStr).getKeys(false)) {
                    int slotIndex = Integer.parseInt(slotIndexStr);
                    String itemKey = containerKey + ".pages." + pageIndexStr + "." + slotIndexStr;

                    String itemData = getDataConfig().getString(itemKey + ".item_data");
                    if (itemData != null) {
                        ItemStack item = deserializeItemStack(itemData);
                        if (item != null) {
                            // 设置物品
                            page.setItem(slotIndex, item);

                            // 设置物品所有者
                            String itemOwnerStr = getDataConfig().getString(itemKey + ".item_owner");
                            if (itemOwnerStr != null) {
                                try {
                                    UUID itemOwner = UUID.fromString(itemOwnerStr);
                                    page.setItemOwner(slotIndex, itemOwner);
                                } catch (IllegalArgumentException e) {
                                    // 如果UUID格式不正确，跳过
                                }
                            }

                            // 设置丢弃时间
                            Long dropTime = getDataConfig().getLong(itemKey + ".drop_time");
                            if (dropTime != 0) {
                                page.setItemDropTime(slotIndex, dropTime);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 序列化物品栈为字符串
     */
    public static String serializeItemStack(ItemStack itemStack) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(itemStack);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            // 忽略序列化错误
            return null;
        }
    }

    /**
     * 从字符串反序列化物品栈
     */
    public static ItemStack deserializeItemStack(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack itemStack = (ItemStack) dataInput.readObject();

            dataInput.close();
            return itemStack;
        } catch (IOException | ClassNotFoundException e) {
            // 忽略反序列化错误
            return null;
        }
    }
}