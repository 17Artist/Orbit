package priv.seventeen.artist.orbit.converter.wrapper;

import priv.seventeen.artist.orbit.loader.component.BBElement;
import priv.seventeen.artist.orbit.loader.component.BBModel;
import priv.seventeen.artist.orbit.loader.component.Outliner;

import java.util.*;

public class WrappedModel {
    private final BBModel handler;
    private final List<WrappedOutliner> outliners;

    public WrappedModel(BBModel handler) {
        this.handler = handler;
        this.outliners = new ArrayList<>();

        Map<UUID, BBElement> elementDict = new HashMap<>();
        if (handler.elements() != null) {
            for (BBElement element : handler.elements()) {
                UUID uuid = element.uniqueId();
                if (uuid != null) {
                    elementDict.put(uuid, element);
                }
            }
        }

        List<Outliner> noOutlinerElements = new ArrayList<>();

        if (handler.outliner() != null) {
            for (Outliner outliner : handler.outliner()) {
                if (outliner.isRedirection()) {
                    noOutlinerElements.add(outliner);
                } else {
                    outliners.add(new WrappedOutliner(outliner, elementDict, null));
                }
            }
        }

        if (!noOutlinerElements.isEmpty()) {
            outliners.add(new WrappedOutliner(Outliner.createRoot(noOutlinerElements), elementDict, null));
        }
    }

    public BBModel handler() { return handler; }
    public List<WrappedOutliner> outliners() { return outliners; }
}
