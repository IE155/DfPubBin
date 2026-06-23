package org.example1.dfPubBin.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.text.SimpleDateFormat;
import java.util.*;

public class GarbagePage {

    private final ItemStack[] contents;
    private final Map<Integer, UUID> itemOwners = new HashMap<>();
    private final Map<Integer, Long> itemDropTimes = new HashMap<>();

    public GarbagePage(int size) {
        this.contents = new ItemStack[size];
    }



    public int getSize() {
        return contents.length;
    }

    public ItemStack getItem(int index) {
        if (index < 0 || index >= contents.length) return null;
        return contents[index];
    }

    public void setItem(int index, ItemStack item) {
        if (index < 0 || index >= contents.length) return;
        contents[index] = item;
        itemOwners.remove(index);
        itemDropTimes.remove(index);
    }

    public void setItem(int index, ItemStack item, Player player) {
        if (index < 0 || index >= contents.length) return;

        if (item == null || item.getType() == Material.AIR) {
            setItem(index, null);
            return;
        }

        contents[index] = item.clone();
        UUID ownerUUID = (player != null) ? player.getUniqueId() : null;
        itemOwners.put(index, ownerUUID);
        itemDropTimes.put(index, System.currentTimeMillis());
    }
    
    /**
     * 设置物品，允许所有者为null（表示自然掉落）
     */
    public void setItemWithOwner(int index, ItemStack item, UUID owner) {
        if (index < 0 || index >= contents.length) return;

        if (item == null || item.getType() == Material.AIR) {
            setItem(index, null);
            return;
        }

        contents[index] = item.clone();
        itemOwners.put(index, owner); // 可以为null
        itemDropTimes.put(index, System.currentTimeMillis());
    }

    public int findNextEmptySlot() {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null || contents[i].getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }



    public ItemStack[] getContentsForPlayer(Player player, GarbageType type) {
        ItemStack[] result = new ItemStack[contents.length];

        for (int i = 0; i < contents.length; i++) {
            ItemStack raw = contents[i];
            if (raw == null || raw.getType() == Material.AIR) continue;

            if (type == GarbageType.PRIVATE) {
                UUID owner = itemOwners.get(i);
                if (owner == null || !owner.equals(player.getUniqueId())) continue;
            }


            ItemStack display = raw.clone();
            ItemMeta meta = display.getItemMeta();

            if (meta != null) {
                List<String> lore = new ArrayList<>();
                UUID owner = itemOwners.get(i);
                Long time = itemDropTimes.get(i);

                if (time != null) {
                    lore.add("§2-------§7[*详情*]§2---------");
                    lore.add(" ");
                    if (owner != null) {
                        lore.add("-§b丢弃者: " +
                                Bukkit.getOfflinePlayer(owner).getName());
                    } else {
                        lore.add("-§b丢弃者: 自然");
                    }
                    lore.add("-§e时间: " +
                            new SimpleDateFormat("YYYY-MM-dd HH:mm")
                                    .format(new Date(time)));
                    lore.add(" ");
                    lore.add("§2--------------------------");
                    lore.add("§f左键或者右键取出，请勿点击shift");
                }

                meta.setLore(lore);
                display.setItemMeta(meta);
            }

            result[i] = display;
        }

        return result;
    }
    
    /**
     * 整理页面，将稀有物品和非空物品移到前面，填满空隙，稀有物品优先
     */
    public void compact() {
        // 创建临时数组存储所有非空物品及其对应的玩家ID和时间
        List<ItemStack> nonEmptyItems = new ArrayList<>();
        List<UUID> owners = new ArrayList<>();
        List<Long> dropTimes = new ArrayList<>();
        
        // 收集所有非空物品
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].getType() != Material.AIR) {
                nonEmptyItems.add(contents[i]);
                // 只有当对应槽位有数据时才添加，避免null值
                owners.add(itemOwners.get(i));
                dropTimes.add(itemDropTimes.get(i));
            }
        }
        
        // 创建带时间戳的物品条目，用于排序
        List<ItemEntry> allItems = new ArrayList<>();
        for (int i = 0; i < nonEmptyItems.size(); i++) {
            ItemEntry entry = new ItemEntry(nonEmptyItems.get(i), owners.get(i), dropTimes.get(i));
            allItems.add(entry);
        }
        
        // 按照要求的顺序排序：稀有物品 → 最新物品 → 其他物品
        allItems.sort((a, b) -> {
            boolean aIsRare = org.example1.dfPubBin.rare.RareItemManager.isRare(a.item);
            boolean bIsRare = org.example1.dfPubBin.rare.RareItemManager.isRare(b.item);
            
            // 稀有物品优先
            if (aIsRare && !bIsRare) {
                return -1;
            }
            if (!aIsRare && bIsRare) {
                return 1;
            }
            
            // 如果都是稀有物品或都不是稀有物品，则按时间倒序（最新的在前）
            return Long.compare(b.dropTime, a.dropTime);
        });
        
        // 清空当前数组和映射
        for (int i = 0; i < contents.length; i++) {
            contents[i] = null;
        }
        itemOwners.clear();
        itemDropTimes.clear();
        
        // 按照排序后的顺序放置物品
        for (int i = 0; i < allItems.size() && i < contents.length; i++) {
            ItemEntry entry = allItems.get(i);
            contents[i] = entry.item;
            if (entry.owner != null) {
                itemOwners.put(i, entry.owner);
            }
            itemDropTimes.put(i, entry.dropTime);
        }
    }
    
    /**
     * 获取指定槽位的物品所有者
     */
    public UUID getItemOwner(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= contents.length) return null;
        return itemOwners.get(slotIndex);
    }
    
    /**
     * 获取指定槽位的物品丢弃时间
     */
    public Long getItemDropTime(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= contents.length) return null;
        return itemDropTimes.get(slotIndex);
    }
    
    /**
     * 手动设置指定槽位的物品丢弃时间（用于从数据库加载数据时）
     */
    public void setItemDropTime(int slotIndex, long dropTime) {
        if (slotIndex < 0 || slotIndex >= contents.length) return;
        itemDropTimes.put(slotIndex, dropTime);
    }
    
    /**
     * 手动设置指定槽位的物品所有者（用于从数据库加载数据时）
     */
    public void setItemOwner(int slotIndex, UUID ownerUUID) {
        if (slotIndex < 0 || slotIndex >= contents.length) return;
        itemOwners.put(slotIndex, ownerUUID);
    }
    
    /**
     * 检查页面是否包含物品
     */
    public boolean hasItems() {
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 用于排序的物品条目
     */
    private static class ItemEntry {
        final ItemStack item;
        final UUID owner;
        final Long dropTime;
        
        ItemEntry(ItemStack item, UUID owner, Long dropTime) {
            this.item = item;
            this.owner = owner;
            this.dropTime = dropTime;
        }
    }
}
