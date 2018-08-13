package computer.lanoel.json.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.TimeZone;

public class DateSerializer implements JsonDeserializer<Calendar> {

    @Override
    public Calendar deserialize(JsonElement element, Type arg1, JsonDeserializationContext context) throws JsonParseException {
        String date = element.getAsString();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        Long timeInMillis = Long.parseLong(date);
        cal.setTimeInMillis(timeInMillis);
        return cal;
    }
}
