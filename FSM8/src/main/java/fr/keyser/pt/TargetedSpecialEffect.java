package fr.keyser.pt;

import java.util.List;
import java.util.Map;

public interface TargetedSpecialEffect {
    public List<TargetedEffectDescription> asyncEffect(DeployedCard source);

    public void apply(DeployedCard source, Map<String, CardPosition> positions);
}
