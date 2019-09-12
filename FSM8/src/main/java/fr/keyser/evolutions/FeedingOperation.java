package fr.keyser.evolutions;

import java.util.List;

public interface FeedingOperation {
    
    public SpeciesId getSpecies();
    
    public String getType();
    
    public List<FeedingSummary> getFeedings();

}
