package org.pessoal.rockscanner.journey;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.pessoal.rockscanner.RockScannerMod;

public class MarkerOverlayFactory {
    private MarkerOverlayFactory() {
    }

    public static CompoundTag MarkerToTag( MarkerOverlay marker){
        try {
            CompoundTag tag = new CompoundTag();
            if(marker != null){
                tag.putString("name", marker.getId());
                tag.putString("title", marker.getTitle());
                tag.putString("label", marker.getLabel());
                tag.putInt("x", marker.getPoint().getX());
                tag.putInt("z", marker.getPoint().getZ());
                tag.putInt("y", marker.getPoint().getY());
                tag.putInt("color", marker.getIcon().getColor());
            }
            return tag;
        } catch ( Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    public static MarkerOverlay TagToMarker(CompoundTag tag){
        try{
            MarkerOverlay marker = MarkerOverlayFactory.create(JourneyImpl.CLIENT_API,
                    tag.getString("name"),
                    tag.getString("title"),
                    tag.getString("label"),
                    new BlockPos(
                            tag.getInt("x"),
                            tag.getInt("y"),
                            tag.getInt("z")
                    ),
                    tag.getInt("color"));
            return marker;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static MarkerOverlay create(@NotNull IClientAPI api,
                                @NotNull String name,
                                @NotNull String title,
                                @NotNull String label,
                                @NotNull BlockPos pos,
                                int color) {
        MapImage emptyIcon = new MapImage(
                new ResourceLocation(RockScannerMod.MODID, "textures/misc/blank.png"),
                pos.getX(), pos.getY(), 1, 1,
                color,
                1.0f
        );
        emptyIcon.setAnchorX(pos.getX());
        emptyIcon.setAnchorY(pos.getY()+1);
        TextProperties textProperties = new TextProperties();
        textProperties.setColor(0xFFFFFF);
        textProperties.setScale(1.5F);
        textProperties.setBackgroundColor(0xAA000000);

        BlockPos adjustPos = new BlockPos(pos.getX(), pos.getY()-5,pos.getZ());
        MarkerOverlay markerOreVein = new MarkerOverlay(RockScannerMod.MODID, name, adjustPos, emptyIcon);
        markerOreVein.setTextProperties(textProperties);
        markerOreVein.setTitle(label);
        markerOreVein.setDimension(Level.OVERWORLD);
        markerOreVein.setLabel(title);
//        markerOreVein.setOverlayGroupName(RockScannerMod.MODID);
        markerOreVein.setOverlayListener(new MarkerListener(JourneyImpl.CLIENT_API, markerOreVein, color, 1.0f));
        return markerOreVein;
    }
}
