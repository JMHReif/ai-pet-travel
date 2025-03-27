package com.jmhreif.ai_pet_travel;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

public record Place(@Id String id,
                    String name,
                    String description,
                    String address,
                    String city,
                    String state,
                    @Relationship(value = "CONTAINS", direction = Relationship.Direction.INCOMING) List<Subcategory> subcategories,
                    @Relationship("PROVIDES") List<Amenity> amenities,
                    @Relationship("HOSTED_BY") Host host) {
}
