package priv.seventeen.artist.orbit.converter.wrapper;

import priv.seventeen.artist.orbit.loader.component.BBElement;
import priv.seventeen.artist.orbit.loader.component.Outliner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrappedOutliner {
    private final Outliner handler;
    private final WrappedOutliner parent;
    private final List<WrappedOutliner> children;
    private final List<BBElement> elements;

    public WrappedOutliner(Outliner handler, Map<UUID, BBElement> elementDict, WrappedOutliner parent) {
        this.handler = handler;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.elements = new ArrayList<>();

        for (Outliner child : handler.children()) {
            if (child.isRedirection()) {
                BBElement element = elementDict.get(child.uniqueId());
                if (element != null) {
                    elements.add(element);
                }
            } else {
                children.add(new WrappedOutliner(child, elementDict, this));
            }
        }
    }

    public Outliner handler() { return handler; }
    public WrappedOutliner parent() { return parent; }
    public List<WrappedOutliner> children() { return children; }
    public List<BBElement> elements() { return elements; }
}
