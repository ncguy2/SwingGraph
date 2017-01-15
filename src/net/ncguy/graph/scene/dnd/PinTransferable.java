package net.ncguy.graph.scene.dnd;

import net.ncguy.graph.scene.logic.Pin;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Created by Guy on 14/01/2017.
 */
public class PinTransferable implements Transferable {

    public static DataFlavor pinFlavour = new DataFlavor(Pin.class, "A Pin Object");
    private Pin pin;

    public static DataFlavor[] supportedFlavours = new DataFlavor[] {
            pinFlavour
    };

    public PinTransferable(Pin pin) {
        this.pin = pin;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavours;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(pinFlavour);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if(flavor.equals(pinFlavour))
            return pin;
        throw new UnsupportedFlavorException(flavor);
    }

    public Pin getPin() {
        return pin;
    }
}
