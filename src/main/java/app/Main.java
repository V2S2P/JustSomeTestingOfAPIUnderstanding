package app;

import DTO.StructureDTO;
import Service.TmdbFetcher;

public class Main {
    public static void main(String[] args) {
        TmdbFetcher tmdbFetcher = new TmdbFetcher();

        StructureDTO result = tmdbFetcher.findByImdbId("tt11126994");
        if (result != null && result.getTvDTOList() != null) {
            result.getTvDTOList().forEach(System.out::println);
        }
    }
}