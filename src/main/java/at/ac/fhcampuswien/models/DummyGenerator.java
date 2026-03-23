package at.ac.fhcampuswien.models;
import java.util.*; // Import the Random class

public class DummyGenerator {

    private static Random random = new Random(); // Create a random number generator

    // Arrays for generating random movie titles
    private static String[] adjectives = {
            "The Silent", "The Dark", "The Golden", "The Lost", "The Hidden",
            "The Last", "The Final", "The Eternal", "The Infinite", "The Forgotten",
            "The Crimson", "The Silver", "The Midnight", "The Rising", "The Falling",
            "The Breaking", "The Burning", "The Frozen", "The Melting", "The Shattered",
            "The Broken", "The Mending", "The Haunted", "The Blessed", "The Cursed",
            "The Ancient", "The Modern", "The New", "The Old", "The Timeless",
            "The Endless", "The Distant", "The Close", "The Deep", "The High",
            "The Low", "The Vast", "The Tiny", "The Bright", "The Dim",
            "The Clear", "The Cloudy", "The Empty", "The Full", "The Hollow",
            "The Solid", "The Liquid", "The Mighty", "The Weak", "The Strong",
            "The Fragile", "The Durable", "The Whole", "The Twisted", "The Straight",
            "The Curved", "The Rough", "The Smooth", "The Jagged", "The Sleek",
            "The Clean", "The Dirty", "The Pure", "The Tainted", "The Sacred",
            "The Profane", "The Noble", "The Vile", "The Graceful", "The Clumsy",
            "The Swift", "The Slow", "The Quick", "The Sluggish", "The Sharp",
            "The Dull", "The Brilliant", "The Drab", "The Vivid", "The Muted",
            "The Loud", "The Quiet", "The Roaring", "The Whispered", "The Screaming",
            "The Violent", "The Gentle", "The Wild", "The Tame", "The Savage",
            "The Civilized", "The Primal", "The Refined", "The Polished", "The Rusted",
            "The Shining", "The Fading", "The Growing", "The Shrinking", "The Soaring"
    };

    private static String[] nouns = {
            "Knight", "Dragon", "City", "Kingdom", "Empire",
            "Tower", "Prophecy", "Mystery", "Secret", "Legend",
            "Throne", "Crown", "Sword", "Shield", "Quest",
            "Journey", "Curse", "Blessing", "Shadow", "Light",
            "Fire", "Ice", "Storm", "Wind", "Ocean",
            "Mountain", "Valley", "Forest", "Desert", "Wasteland",
            "Ruin", "Temple", "Castle", "Manor", "Mansion",
            "Prison", "Dungeon", "Grave", "Tomb", "Crypt",
            "Vault", "Chamber", "Hall", "Palace", "Fort",
            "Stronghold", "Citadel", "Wall", "Gate", "Bridge",
            "River", "Lake", "Sea", "Island", "Continent",
            "Planet", "Star", "Moon", "Sun", "Sky",
            "Heaven", "Hell", "Paradise", "Abyss", "Void",
            "Portal", "Dimension", "Realm", "World", "Universe",
            "Galaxy", "Cosmos", "Eclipse", "Aurora", "Nebula",
            "Horizon", "Horizon", "Spectrum", "Rainbow", "Prism",
            "Mirror", "Reflection", "Echo", "Sound", "Silence",
            "Whisper", "Roar", "Scream", "Cry", "Laughter",
            "Truth", "Lie", "Fate", "Destiny", "Hope",
            "Despair", "Love", "Hate", "War", "Peace"
    };

    // Generate random movie title
    public static String generateMovieTitle() {
        String adjective = adjectives[random.nextInt(adjectives.length)]; // Random adjective
        String noun = nouns[random.nextInt(nouns.length)]; // Random noun
        return adjective + " " + noun; // Combine adjective and noun
    }
}