package org.pessoal.rockscanner.events;

import it.unimi.dsi.fastutil.longs.LongList;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.display.WaypointGroup;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.gtreimagined.gtlib.GTLibConfig;
import org.gtreimagined.gtlib.block.BlockSurfaceRock;
import org.gtreimagined.gtlib.material.Material;
import org.pessoal.rockscanner.RockScannerMod;
import org.pessoal.rockscanner.journey.MarkerOverlayFactory;
import org.pessoal.rockscanner.journey.JourneyImpl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = RockScannerMod.MODID)
public class CustomEvents {

    private static void SendMessage(Player p, String message) {
        p.sendMessage(new TextComponent(message), p.getUUID());
    }

    @SubscribeEvent
    public static void OnRightClick(PlayerInteractEvent.RightClickBlock playerInteractEvent) {
        try {
            if (ModList.get().isLoaded("journeymap") && ModList.get().isLoaded("gtlib")) {
                Player player = playerInteractEvent.getPlayer();
                if (player.isLocalPlayer()) return;
                if (playerInteractEvent.getHand() == InteractionHand.OFF_HAND) return;
                BlockState state = playerInteractEvent.getWorld().getBlockState(playerInteractEvent.getPos());
                Item item = Item.BY_BLOCK.get(state.getBlock());
//        SendMessage(player, "1 - " + item.asItem().getDescription().getString());
//        SendMessage(player, "2 - " + item.toString());
//        SendMessage(player, "3 - " + item.asItem().toString());
//        SendMessage(player, "4 - " + state.getBlock().toString());
                if (state.getBlock() instanceof BlockSurfaceRock surfaceRock) {
//            SendMessage(player, "5 - " + surfaceRock.getStoneType().getMaterial().toString());
//            SendMessage(player, "5 - " + surfaceRock.toString());
//            SendMessage(player, "5.1 - " + surfaceRock.getMaterial());


                    if (!surfaceRock.asItem().toString().contains("surface_rock")) return;

                    if (!item.asItem().getDescription().getString().endsWith("Surface Rock")) return;

                    String str = item.asItem().getDescription().getString();
                    final String ROCK = "Surface Rock";
                    try {
                        str = str.substring(0, str.indexOf(ROCK) - 1).trim();
                    } catch (Exception e) {
                        return;
                    }
                    StringBuilder vein_string = new StringBuilder(str.substring(str.lastIndexOf(" ")).trim());

                    if (JourneyImpl.CLIENT_API != null) {
                        LevelChunk chunk = playerInteractEvent.getWorld().getChunkAt(playerInteractEvent.getPos());
                        if (item.asItem().getDescription().getString().split(" ").length > 3) {

                            String materialChunkName = "";
                            String seedId = "";
                            Map<Material, LongList> oresInChunk = RockScannerMod.getVeinSavedData().geOresInChunk(playerInteractEvent.getPos().getX() >> 4, playerInteractEvent.getPos().getZ() >> 4);
                            AtomicReference<ChunkPos> chunkTemp = new AtomicReference<>();
                            List<Map.Entry<Material, LongList>> top4 = new ArrayList<>();
                            if (oresInChunk != null && !oresInChunk.isEmpty()) {
                                top4 = oresInChunk.entrySet().stream().sorted((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()))
                                        .limit(4)
                                        .toList();
                            }

//                        Vein vein = null;
                            ChunkPos finalChunk = chunk.getPos();
                            if (chunkTemp.get() != null) {
                                finalChunk = chunkTemp.get();
                            }

                            StringBuilder oreVeinList = new StringBuilder(vein_string.toString());
                            for (Map.Entry<Material, LongList> ores : top4) {
                                oreVeinList.append("\n").append(ores.getKey().getId());
                            }
                            final ChunkPos f = finalChunk;
                            List<Waypoint> waypointList = JourneyImpl.CLIENT_API.getAllWaypoints().stream().filter(waypoint -> {
                                if (waypoint.getPosition().getX() >= f.getMinBlockX() - (GTLibConfig.ORE_VEIN_MAX_SIZE.get()) &&
                                        waypoint.getPosition().getX() <= f.getMaxBlockX() + (GTLibConfig.ORE_VEIN_MAX_SIZE.get()) &&
                                        waypoint.getPosition().getZ() >= f.getMinBlockZ() - (GTLibConfig.ORE_VEIN_MAX_SIZE.get()) &&
                                        waypoint.getPosition().getZ() <= f.getMaxBlockZ() + (GTLibConfig.ORE_VEIN_MAX_SIZE.get())) {
                                    return true;
                                }
                                return false;
                            }).toList();

                            if (!waypointList.isEmpty()) return;
                            if (!top4.isEmpty() && top4.stream().toList().get(0).getValue().size() < 200) return;

                            for (Map.Entry<Material, LongList> ores : top4) {
                                SendMessage(player, "Ore: " + ores.getKey().getId() + " - " + ores.getValue().size());
                            }

                            Random rdn = new Random();
                            int r = rdn.nextInt(256);
                            int g = rdn.nextInt(256);
                            int b = rdn.nextInt(256);
//                        MarkerOverlay overlay = MarkerOverlayFactory.create(
//                                JourneyImpl.CLIENT_API,
//                                vein_string.toString(),
//                                vein_string.toString(),
//                                oreVeinList.toString(),
//                                playerInteractEvent.getPos(),
//                                new Color(r,g,b).getRGB()
//                        );
//                        RockScannerMod.getOverlayData().addOverlay(player, overlay);

                            WaypointGroup group = new WaypointGroup(RockScannerMod.MODID, "OreVeins");


                            Waypoint wDirt = new Waypoint(RockScannerMod.MODID, vein_string.toString(), player.getLevel().dimension(), playerInteractEvent.getPos());
                            wDirt.setColor(new Color(r, g, b).getRGB());
                            wDirt.setGroup(group);
                            try {
//                            JourneyImpl.CLIENT_API.show(overlay);
                                JourneyImpl.CLIENT_API.show(wDirt);
                            } catch (Exception e) {
                                RockScannerMod.LOGGER.error("{0}", e);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }

    }

}
