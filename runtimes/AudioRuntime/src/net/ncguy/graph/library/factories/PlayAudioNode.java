package net.ncguy.graph.library.factories;

import net.ncguy.graph.data.MutableProperty;
import net.ncguy.graph.library.AudioNode;
import net.ncguy.graph.scene.logic.Pin;
import net.ncguy.graph.scene.logic.SceneGraph;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PlayAudioNode extends AudioNode {

    public Pin audioPathPin;
    public Pin volumePin;
    public Pin timePin;

    Clip clip;

    MutableProperty<String> audioPathProperty;
    MutableProperty<Float> volumeProperty;

    public PlayAudioNode(SceneGraph graph) {
        super(graph, "Play clip");
        audioPathProperty = new MutableProperty<>("Clip path", "");
        volumeProperty    = new MutableProperty<>("Volume", 1.f);
        addPin(audioPathPin = new Pin(this, "Clip path", true), audioPathProperty);
        addPin(volumePin    = new Pin(this, "Volume", true), volumeProperty);
        addPin(timePin      = new Pin(this, "Time", true));
    }

    @Override
    public void Invoke() {
        try {
            clip = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(new URL(GetValueFromInputPin(audioPathPin).toString()));
            clip.setMicrosecondPosition((Long) GetValueFromInputPin(timePin));
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object GetValueFromOutputPin(Pin pin) {
        return null; // No output pins
    }

    @Override
    public void GetMutableProperties(List<MutableProperty> list) {
        super.GetMutableProperties(list);
        list.add(audioPathProperty);
        list.add(volumeProperty);
    }
}
