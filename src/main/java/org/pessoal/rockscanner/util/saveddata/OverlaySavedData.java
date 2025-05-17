package org.pessoal.rockscanner.util.saveddata;


import journeymap.client.api.display.MarkerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Marker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.pessoal.rockscanner.RockScannerMod;
import org.pessoal.rockscanner.journey.MarkerOverlayFactory;

import java.util.*;

public class OverlaySavedData extends SavedData {

    private final Map<UUID, List<MarkerOverlay>> overlayList = new HashMap<>();
    private static ServerLevel level = null;

    public OverlaySavedData(){

    }

    public static ServerLevel getServerLevel(){
        return level;
    }

    public OverlaySavedData(CompoundTag tag, ServerLevel l){
        overlayList.putAll(load(tag, level).overlayList);
        level = l;
    }

    public Map<UUID, List<MarkerOverlay>> getOverlayList(){
        return overlayList;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        RockScannerMod.LOGGER.info("SAVE");
        ListTag listTag = new ListTag();
        overlayList.forEach((uuid, markerOverlayList) -> {
            for(MarkerOverlay markerOverlay : markerOverlayList) {
                CompoundTag overlayTag = new CompoundTag();
                overlayTag.put("marker", MarkerOverlayFactory.MarkerToTag(markerOverlay));
                RockScannerMod.LOGGER.info("SAVE -> {}, X:{}, Z:{}, Y:{}", markerOverlay.getId(), markerOverlay.getPoint().getX(), markerOverlay.getPoint().getZ(), markerOverlay.getPoint().getY());
                listTag.add(overlayTag);
            }
        });
        tag.put(Minecraft.getInstance().player.getUUID().toString(), listTag);
        return tag;
    }

    public static OverlaySavedData load(CompoundTag tag, ServerLevel level){
        RockScannerMod.LOGGER.info("LOAD");
        OverlaySavedData data = new OverlaySavedData();
        for(String key : tag.getAllKeys()) {
            List<MarkerOverlay> tempList = new ArrayList<>();
            ListTag listTag = (ListTag) tag.get(key);
            listTag.forEach(tag1 -> {
                if (tag1 instanceof CompoundTag currentTag) {
                    tempList.add(MarkerOverlayFactory.TagToMarker(currentTag.getCompound("marker")));
                }
            });
            data.overlayList.put(UUID.fromString(key), tempList);
            RockScannerMod.LOGGER.info("LOAD -> {}", Integer.valueOf(data.getOverlayList().size()).toString());
            RockScannerMod.LOGGER.info("LOAD -> {}", data);
        }
        return data;
    }

    public static OverlaySavedData get(Level level){
        if(level.isClientSide){
            throw new RuntimeException("Can't be called from client side");
        }

        DimensionDataStorage dataStorage = ((ServerLevel)level).getDataStorage();
        return dataStorage.computeIfAbsent(compoundTag -> new OverlaySavedData(compoundTag, (ServerLevel)level), OverlaySavedData::new, "rockscanner_overlay_waypoint");
    }

    public void addOverlay(Player player, MarkerOverlay marker){
        if(overlayList.size() > 0) {
            List<MarkerOverlay> markers = new ArrayList<>();
            boolean newPlayer = true;
            for (Map.Entry<UUID, List<MarkerOverlay>> current : overlayList.entrySet()) {
                RockScannerMod.LOGGER.info(" addOverlay : {}", current.getValue());
                RockScannerMod.LOGGER.info(" addOverlay UUID: {}", current.getKey().toString());
                if (current.getKey() == player.getUUID()) {
                    newPlayer = false;
                    RockScannerMod.LOGGER.info("NOT NEW PLAYER");
                    markers.addAll(current.getValue());
                }
            }
            if (markers.stream().filter(tempMarker ->
                    tempMarker.getPoint().getZ() == marker.getPoint().getZ() &&
                            tempMarker.getPoint().getX() == marker.getPoint().getX() &&
                            tempMarker.getPoint().getY() == marker.getPoint().getY()
            ).toList().isEmpty()) {
                if (newPlayer) {
                    List<MarkerOverlay> tempMarker = new ArrayList<>();
                    tempMarker.add(marker);
                    overlayList.put(player.getUUID(), tempMarker);
                    setDirty();
                } else {
                    overlayList.get(player.getUUID()).add(marker);
                    setDirty();
                }
                RockScannerMod.LOGGER.info("AFTER ADD: {}", markers);
            }
        } else {
            List<MarkerOverlay> tempMarker = new ArrayList<>();
            tempMarker.add(marker);
            overlayList.put(player.getUUID(), tempMarker);
            setDirty();
        }
    }
}
