package com.play.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter personalizzato per serializzare e deserializzare oggetti {@link LocalDateTime} con Gson.
 * Utilizza il formato ISO_LOCAL_DATE_TIME (es: 2025-07-21T15:30:00).
 *
 * Questo adapter è registrato globalmente nel {@link com.play.utils.FileManager} per gestire correttamente
 * le date durante il salvataggio e il caricamento dei file JSON (es. tentativi con timestamp).
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // Formatter standard ISO: "yyyy-MM-dd'T'HH:mm:ss"
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Serializza un oggetto LocalDateTime in formato stringa JSON.
     *
     * @param dateTime Oggetto LocalDateTime da serializzare
     * @param type     Tipo del dato (ignorato)
     * @param context  Contesto di serializzazione
     * @return JsonPrimitive contenente la data formattata
     */
    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(dateTime.format(formatter));
    }

    /**
     * Deserializza una stringa JSON in formato ISO in un oggetto LocalDateTime.
     *
     * @param json     JsonElement da convertire
     * @param type     Tipo di ritorno previsto
     * @param context  Contesto di deserializzazione
     * @return Oggetto LocalDateTime parsato dal testo
     * @throws JsonParseException Se il formato è errato o non compatibile
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}
