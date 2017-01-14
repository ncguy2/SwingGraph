package net.ncguy.graph.scene.dnd;

import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.render.PinComponent;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * Created by Guy on 14/01/2017.
 */
public class PinTransferHandler {

    public static class DragGesture implements DragGestureListener {

            @Override
            public void dragGestureRecognized(DragGestureEvent e) {
            Cursor cursor = null;
            PinComponent pinComp = (PinComponent) e.getComponent();
            Pin pin = pinComp.pin;

            if(e.getDragAction() == DnDConstants.ACTION_COPY)
                cursor = DragSource.DefaultCopyDrop;

            e.startDrag(cursor, new PinTransferable(pin));
        }
    }

    public static class DropListener extends DropTargetAdapter {

        private DropTarget target;
        PinComponent thisPin;

        public DropListener(PinComponent thisPin) {
            this.thisPin = thisPin;
            target = new DropTarget(this.thisPin, DnDConstants.ACTION_COPY, this, true, null);
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            try{
                Transferable tr = e.getTransferable();
                if(tr instanceof PinTransferable) {
                    PinTransferable ptr = (PinTransferable) tr;
                    Pin pin = (Pin) ptr.getTransferData(PinTransferable.pinFlavour);
                    if(e.isDataFlavorSupported(PinTransferable.pinFlavour)) {
                        e.acceptDrop(DnDConstants.ACTION_COPY);
                        thisPin.pin.tryConnect(pin);
                        e.dropComplete(true);
                        return;
                    }
                }
                e.rejectDrop();
            } catch (UnsupportedFlavorException | IOException e1) {
                e1.printStackTrace();
                e.rejectDrop();
            }
        }
    }

}
