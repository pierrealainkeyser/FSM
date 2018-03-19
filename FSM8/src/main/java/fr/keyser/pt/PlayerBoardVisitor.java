package fr.keyser.pt;

import java.util.List;
import java.util.UUID;

public interface PlayerBoardVisitor {

    public void state(UUID uuid, String state);

    public void player(UUID uuid, int gold, int legend);

    public void victoriousWar(UUID player, int winned);

    public void unit(UUID player, CardPosition position, Unit unit, boolean isNew, int age, int strength, boolean mayCombat);

    public void building(UUID player, CardPosition position, Building building, BuildingLevel level, int strength, boolean mayCombat);

    public void toDraft(UUID player, List<MetaCard> toDraft);

    public void toDeploy(UUID player, List<MetaCard> toDeploy);

    public void toBuild(UUID player, List<BuildingConstruction> bluePrint);
}
