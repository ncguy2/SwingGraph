package net.ncguy.graph.scene.dnd;

import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.render.PinComponent;

import javax.swing.*;
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

    public static class Handler extends TransferHandler {

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            PinComponent comp = (PinComponent) c;
            Pin pin = comp.pin;
            return new PinTransferable(pin);
        }

        @Override
        public boolean canImport(TransferSupport support) {
            if(!support.isDrop())
                return false;
            return support.isDataFlavorSupported(PinTransferable.pinFlavour);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if(!this.canImport(support))
                return false;

            Transferable t = support.getTransferable();
            if(!(t instanceof PinTransferable)) return false;
            PinTransferable pt = (PinTransferable) t;
            Pin pin = null;
            try {
                pin = (Pin) pt.getTransferData(PinTransferable.pinFlavour);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
                return false;
            }

            DropLocation dropLocation = support.getDropLocation();
            System.out.println(dropLocation);

            return super.importData(support);
        }
    }

}
