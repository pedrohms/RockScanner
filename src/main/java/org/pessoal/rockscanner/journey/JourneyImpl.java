package org.pessoal.rockscanner.journey;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.event.ClientEvent;
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

    @Override
    public void onEvent(ClientEvent clientEvent) {
        RockScannerMod.LOGGER.info("onEvent -> TYPE: {}", clientEvent.type);
        switch(clientEvent.type){
            case MAPPING_STARTED: {
                onMappingStarted(clientEvent);
            }
            case MAPPING_STOPPED: {
                onMappingStopped(clientEvent);
            }
        }
    }

    private void onMappingStarted(ClientEvent clientEvent) {
//        if(CLIENT_API.playerAccepts(RockScannerMod.MODID, DisplayType.Marker)){
        System.out.println("onMappingStarted");
        for(Map.Entry<UUID, List<MarkerOverlay>> markerList : RockScannerMod.getOverlayData().getOverlayList().entrySet()){
            try {
                for(MarkerOverlay marker : markerList.getValue()){
                    System.out.println("Marker: " + marker.getId());
                    CLIENT_API.show(marker);
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
