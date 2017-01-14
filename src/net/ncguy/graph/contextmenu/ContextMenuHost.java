package net.ncguy.graph.contextmenu;

import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.event.OpenContextMenuEvent;

/**
 * Created by Guy on 14/01/2017.
 */
public class ContextMenuHost implements OpenContextMenuEvent.OpenContextMenuListener {

    ContextMenuForm form;

    public ContextMenuForm getForm() {
        if(form == null)
            form = new ContextMenuForm();
        return form;
    }

    public ContextMenuHost() {
        EventBus.instance().register(this);
    }

    @Override
    public void onOpenContextMenu(OpenContextMenuEvent event) {
        getForm().setLocationRelativeTo(null);
        getForm().setLocation((int)event.x, (int)event.y);
        getForm().setVisible(true);
        System.out.printf("Open context menu at coordinates [%s, %s]\n", event.x, event.y);
    }
}
