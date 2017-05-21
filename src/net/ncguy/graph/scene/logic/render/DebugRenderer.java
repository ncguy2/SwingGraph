package net.ncguy.graph.scene.logic.render;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import net.ncguy.graph.event.EventBus;
import net.ncguy.graph.event.ExecNodeProcessedEvent;
import net.ncguy.graph.scene.render.SceneGraphForm;
import net.ncguy.graph.tween.JComponentTweenAccessor;

import java.awt.*;

public class DebugRenderer implements ExecNodeProcessedEvent.ExecNodeProcessedListener {

    private final SceneGraphRenderer renderer;

    public DebugRenderer(SceneGraphRenderer renderer) {
        this.renderer = renderer;
        activeColour = Color.RED;
        pulseTime = 1.f;
        EventBus.instance().register(this);
    }

    public Color activeColour;
    public float pulseTime;

    public Color getActiveColour() {
        return activeColour;
    }

    public void setActiveColour(Color activeColour) {
        this.activeColour = activeColour;
    }

    public float getPulseTime() {
        return pulseTime;
    }

    public void setPulseTime(float pulseTime) {
        this.pulseTime = pulseTime;
    }

    @Override
    public void onExecNodeProcessed(ExecNodeProcessedEvent event) {
        if(event.node == null) return;
        NodeWrapper wrapper = renderer.getNodeWrapper(event.node);
        Color baseColour = wrapper.getTrueBackground();
        Timeline.createSequence()
                .push(Tween.to(wrapper.nodeComponent, JComponentTweenAccessor.FULL, .4f).target(activeColour.getRed(), activeColour.getGreen(), activeColour.getBlue(), 255))
                .pushPause(pulseTime)
                .push(Tween.to(wrapper.nodeComponent, JComponentTweenAccessor.FULL,1.2f).target(baseColour.getRed(), baseColour.getGreen(), baseColour.getBlue(), 255 * .4f))
                .start(SceneGraphForm.instance.tweenManager);
    }
}
