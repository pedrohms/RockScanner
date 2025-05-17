package org.pessoal.rockscanner.journey;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.util.UIState;
import net.minecraft.core.BlockPos;

import java.awt.*;
import java.awt.geom.Point2D;

public class MarkerListener implements IOverlayListener {

    final IClientAPI jwAPI;
    final MarkerOverlay markerOverlay;
    final int color;
    final float opacity;

    public MarkerListener(IClientAPI jwAPI, MarkerOverlay markerOverlay, int color, float opacity){
        this.jwAPI = jwAPI;
        this.markerOverlay = markerOverlay;
        this.color = color;
        this.opacity = opacity;
    }

    @Override
    public void onActivate(UIState uiState) {
        refresh(uiState);
    }

    @Override
    public void onDeactivate(UIState uiState) {
        refresh(uiState);
    }

    @Override
    public void onMouseMove(UIState uiState, Point2D.Double aDouble, BlockPos blockPos) {

        double size = uiState.blockSize*8;
        if(markerOverlay.getIcon().getDisplayWidth() != size){
            markerOverlay.getIcon()
                    .setDisplayHeight(size)
                    .setDisplayWidth(size)
                    .setAnchorX(size/2)
                    .setAnchorY(size);
            markerOverlay.flagForRerender();
        }
    }

    @Override
    public void onMouseOut(UIState uiState, Point2D.Double aDouble, BlockPos blockPos) {
        refresh(uiState);
    }

    @Override
    public boolean onMouseClick(UIState uiState, Point2D.Double aDouble, BlockPos blockPos, int i, boolean b) {
        jwAPI.remove(markerOverlay);
        return true;
    }

    @Override
    public void onOverlayMenuPopup(UIState uiState, Point2D.Double aDouble, BlockPos blockPos, ModPopupMenu modPopupMenu) {
        modPopupMenu.addMenuItem("Delete", (b) -> jwAPI.remove(markerOverlay));
    }

    private void refresh(UIState uiState){
        double size = uiState.blockSize*8;

        markerOverlay.getIcon()
                .setColor(color)
                .setOpacity(opacity)
                .setDisplayWidth(size)
                .setDisplayHeight(size)
                .setAnchorX(size/2)
                .setAnchorY(size);
        markerOverlay.flagForRerender();
    }
}
