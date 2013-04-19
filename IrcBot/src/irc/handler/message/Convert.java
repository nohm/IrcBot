package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.io.ByteArrayOutputStream;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.pircbotx.hooks.events.MessageEvent;

public class Convert extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			String cleaned = event.getMessage();
			String[] split = cleaned.split(" ");
			String query = cleaned.substring(cleaned.indexOf("convert ") + "convert ".length()).replace(" ", "+");

			if (split[2].equalsIgnoreCase("btc") || split[2].equalsIgnoreCase("bitcoin") || split[4].equalsIgnoreCase("btc") || split[4].equalsIgnoreCase("bitcoin")) {
				if ((split[2].equalsIgnoreCase("btc") || split[2].equalsIgnoreCase("bitcoin")) && (split[4].equalsIgnoreCase("btc") || split[4].equalsIgnoreCase("bitcoin"))) {
					response = "Convert: 1 Bitcoin = 1 Bitcoin";
				} else {
					ByteArrayOutputStream output = Utils.httpRequest("http://data.mtgox.com/api/2/BTCUSD/money/ticker");
					JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
					JSONObject data = jo.getJSONObject("data");

					Double buy = Double.valueOf(data.getJSONObject("buy").getString("display_short").substring(1));

					String other = "Unable to convert :(";
					if (split[2].equalsIgnoreCase("btc") || split[2].equalsIgnoreCase("bitcoin")) {
						String[] calc = calc("10000+usd+to+" + split[4]);
						if (split[4].equalsIgnoreCase("usd")) {
							other = Utils.twoDecimalsString(Double.valueOf(split[1]) * buy) + " " + calc[1];
						} else {
							other = Utils.twoDecimalsString((Double.valueOf(split[1]) * buy) * Double.valueOf(calc[0]) / 10000) + " " + calc[3];
						}
						response = "Convert: " + split[1] + " Bitcoin = " + other;
					} else {
						String[] calc = calc(split[1] + "+" + split[2] + "+to+usd");
						if (split[2].equalsIgnoreCase("usd")) {
							Double d = Double.valueOf(split[1]);
							other = Utils.twoDecimalsString(d / buy) + " Bitcoin";
						} else {
							Double d = Double.valueOf(calc[0]);
							other = Utils.twoDecimalsString(d / buy) + " Bitcoin";
						}
						response = "Convert: " + calc[0] + " " + calc[1] + " = " + other;
					}
				}
			} else {
				String[] calc = calc(query);
				response = "Convert: " + calc[0] + " " + calc[1] + " = " + calc[2] + " " + calc[3];
			}
		} catch (Exception e) {
			response = "Unable to convert :(";
		}

		Startup.print("~INFO Response: " + response);
		event.getBot().sendNotice(event.getUser(), response);
	}

	private String[] calc(String query) throws Exception {
		ByteArrayOutputStream output = Utils.httpRequest("http://www.google.com/search?q=convert+" + query + "&num=100&hl=en&start=0");
		Document doc = Jsoup.parse(output.toString());
		Elements currency = doc.select("li.currency");
		Elements left, right;
		String leftVal, leftUnit, rightVal, rightUnit;
		if (currency.size() != 0) {
			left = doc.select("input[id=pair_base_input]");
			right = doc.select("input[id=pair_targ_input]");
		} else {
			left = doc.select("input[id=ucw_lhs_d]");
			right = doc.select("input[id=ucw_rhs_d]");
		}
		if (left.size() > 0 && right.size() > 0) {
			String select = doc.select("option").size() == 2 ? "option" : "option[selected=1";
			leftVal = left.get(0).attr("value");
			leftUnit = doc.select(select).get(0).text();
			rightVal = right.get(0).attr("value");
			rightUnit = doc.select(select).get(1).text();
			return new String[] {leftVal, leftUnit, rightVal, rightUnit};
		} else {
			throw new Exception();
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ").length == 5 && event.getMessage().split(" ")[0].endsWith("convert") && event.getMessage().split(" ")[3].equalsIgnoreCase("to"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("convert")) {
			return chan.functions.get("convert");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}