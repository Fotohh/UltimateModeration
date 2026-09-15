package me.xaxis.ultimatemoderationplus.codec;

import me.xaxis.ultimatemoderationplus.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderationplus.player.Note;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class NoteCodec implements ProfileEntryCodec<Note> {

    @Override
    public Map<String, Object> encode(Note note) {
        Objects.requireNonNull(note, "Note cannot be null");

        Map<String, Object> values = new LinkedHashMap<>();
        values.put(PlayerProfileSchema.NOTE_AUTHOR_ID, note.authorUUID().toString());
        values.put(PlayerProfileSchema.NOTE_AUTHOR_NAME, note.authorName());
        values.put(PlayerProfileSchema.NOTE_CONTENT, note.content());
        values.put(PlayerProfileSchema.NOTE_TIMESTAMP, note.timestamp());
        return values;
    }

    @Override
    public Note decode(Map<?, ?> values) {
        Objects.requireNonNull(values, "Note values cannot be null");

        return new Note(
                UUID.fromString((String) values.get(PlayerProfileSchema.NOTE_AUTHOR_ID)),
                (String) values.get(PlayerProfileSchema.NOTE_AUTHOR_NAME),
                (String) values.get(PlayerProfileSchema.NOTE_CONTENT),
                ((Number) values.get(PlayerProfileSchema.NOTE_TIMESTAMP)).longValue()
        );
    }
}
