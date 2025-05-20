package org.pessoal.rockscanner.journey;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.DisplayUpdateEvent;
import net.minecraft.client.Minecraft;
import org.pessoal.rockscanner.RockScannerMod;
import org.pessoal.rockscanner.util.saveddata.OverlaySavedData;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@journeymap.client.api.ClientPlugin
public class JourneyImpl implements IClientPlugin {

    public static JourneyImpl INSTANCE = null;
    public static IClientAPI CLIENT_API = null;

    @Override
    public void initialize(IClientAPI iClientAPI) {
        System.out.println("PluginImpl.initialize chamado!");
        INSTANCE = this;
        CLIENT_API = iClientAPI;
        CLIENT_API.subscribe(RockScannerMod.MODID, EnumSet.of(ClientEvent.Type.MAPPING_STARTED, ClientEvent.Type.MAPPING_STOPPED));
    }

    @Override
    public String getModId() {
        return RockScannerMod.MODID;
    }

    public void onDisplayUpdate(DisplayUpdateEvent displayUpdateEvent){
        RockScannerMod.LOGGER.info("onEvent -> TYPE: {}", displayUpdateEvent.type);
        switch(displayUpdateEvent.type){
            case DISPLAY_UPDATE: {
                onDisplayUpdated(displayUpdateEvent);
            }
        }
    }
    @Override
    public void onEvent(ClientEvent clientEvent) {
        RockScannerMod.LOGGER.info("onEvent -> TYPE: {}", clientEvent.type);
        switch(clientEvent.type){
            case MAPPING_STARTED: {
//                onMappingStarted(clientEvent);
            }
            case MAPPING_STOPPED: {
                onMappingStopped(clientEvent);
            }
        }
    }


    private void onDisplayUpdated(DisplayUpdateEvent displayUpdateEvent) {
//        if(CLIENT_API.playerAccepts(RockScannerMod.MODID, DisplayType.Marker)){
        System.out.println("onDisplayUpdated");
        for(Map.Entry<UUID, List<MarkerOverlay>> markerList : RockScannerMod.getOverlayData().getOverlayList().entrySet()){
            try {
                for(MarkerOverlay marker : markerList.getValue()){
                    if(!CLIENT_API.exists(marker)) {
                        RockScannerMod.LOGGER.info("Marker: {}, X: {}, Z: {}, Y: {}", marker.getId(), marker.getPoint().getX(), marker.getPoint().getZ(), marker.getPoint().getY());
                        CLIENT_API.show(marker);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        }
    }

    private void onMappingStopped(ClientEvent event){
        CLIENT_API.removeAll(RockScannerMod.MODID);
    }
}
