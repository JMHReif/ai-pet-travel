package com.jmhreif.ai_pet_travel;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface PlaceRepository extends Neo4jRepository<Place, String> {
    @Query("MATCH (p:Place) " +
            "WHERE p.id IN $placeIds " +
            "OPTIONAL MATCH (p)<-[r:CONTAINS]-(s:Subcategory) " +
            "OPTIONAL MATCH (p)-[r2:PROVIDES]-(a:Amenity) " +
            "OPTIONAL MATCH (p)-[r3:HOSTED_BY]-(h:Host) " +
            "RETURN p, collect(r), collect(s), collect(r2), collect(a), collect(r3), collect(h);")
    List<Place> findPlaces(List<String> placeIds);
}
