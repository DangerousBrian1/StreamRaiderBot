package program;

import java.text.ParseException;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import include.JsonParser;

public class Unit {

	private JsonObject unit = null;
	private Date cool = null;
	private int rank = 0;
	private JsonArray ptags = null;
	
	public static final JsonObject uTypes = JsonParser.json(StreamRaiders.get("unitTypes"));
	
	public static JsonArray getAllPlanTypes() {
		return uTypes.getAsJsonArray("allTypes").deepCopy();
	}
	
	public static JsonObject getTypes() {
		JsonObject ret = uTypes.deepCopy();
		ret.remove("allTypes");
		return ret;
	}
	
	public boolean canFly() {
		return uTypes.getAsJsonObject(this.get(SRC.Unit.unitType)).getAsJsonPrimitive("canFly").getAsBoolean();
	}
	
	public static boolean isLegendary(String type) {
		return uTypes.getAsJsonObject(type).getAsJsonPrimitive("rank").getAsInt() == 4;
	}
	
	public Unit(JsonObject unit) throws ClassCastException {
		this.unit = unit;
		JsonElement jcool = unit.get("cooldownTime");
		if(jcool.isJsonPrimitive()) {
			setDate(unit.getAsJsonPrimitive("cooldownTime").getAsString());
		}
		
		JsonObject uType = uTypes.getAsJsonObject(unit.getAsJsonPrimitive(SRC.Unit.unitType).getAsString());
		
		rank = uType.getAsJsonPrimitive("rank").getAsInt();
		ptags = uType.getAsJsonArray("role");
	}
	
	private Unit(String unitType) {
		JsonObject unit = new JsonObject();
		unit.addProperty(SRC.Unit.unitType, unitType);
		this.unit = unit;
	}
	
	public static Unit createTypeOnly(String unitType) {
		return new Unit(unitType);
	}

	public String get(String con) {
		if(con.equals(SRC.Unit.rank)) return ""+rank;
		
		JsonElement el = unit.get(con);
		if(el == null) return null;
		if(!el.isJsonPrimitive()) return null;
		return el.getAsString();
	}
	
	public boolean isAvailable(String serverTime) {
		if(cool == null) return true;
		try {
			return SRC.dateParse(serverTime).after(cool);
		} catch (ParseException e) {
			StreamRaiders.log("Unit -> isAvailable: st=" + serverTime, e);
		}
		return false;
	}
	
	public boolean hasPlanType(String tag) {
		return ptags.contains(new JsonPrimitive(tag));
	}
	
	public JsonArray getPlanTypes() {
		return ptags;
	}
	
	public void setDate(String date) {
		try {
			this.cool = SRC.dateParse(date);
		} catch (ParseException e) {
			StreamRaiders.log("Unit -> setDate: date=" + date, e);
		}
	}


	
	
}
