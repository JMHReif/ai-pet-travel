package com.jmhreif.ai_pet_travel;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PlaceController {
    private final PlaceRepository placeRepository;
    private final ChatClient chatClient;
    private final Neo4jVectorStore vectorStore;

    String prompt = """
            You are a travel expert providing recommendations from high-quality information in the CONTEXT section.
            Please summarize the places provided in the context section.
            
            CONTEXT:
            {context}
            
            SEARCHTERM:
            {searchTerm}
            """;

    public PlaceController(PlaceRepository placeRepository, ChatClient.Builder builder, Neo4jVectorStore vectorStore) {
        this.placeRepository = placeRepository;
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/rag")
    public String rag(@RequestParam String searchTerm) {
        List<Document> results = vectorStore.doSimilaritySearch(SearchRequest.builder().query(searchTerm).build());

        List<Place> places = placeRepository.findPlaces(results.stream().map(Document::getId).collect(Collectors.toList()));

        var template = new PromptTemplate(prompt,
                Map.of("context", places.stream().map(Place::toString).collect(Collectors.joining("\n")),
                        "searchTerm", searchTerm));
        System.out.println("----- PROMPT -----");
        System.out.println(template.render());

        return chatClient.prompt(template.create()).call().content();
    }
}
