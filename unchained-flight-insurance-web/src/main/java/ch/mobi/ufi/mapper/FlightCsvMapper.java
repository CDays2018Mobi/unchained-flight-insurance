package ch.mobi.ufi.mapper;

import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.function.Function;

import ch.mobi.ufi.model.Flight;

public class FlightCsvMapper extends CsvMapper<Flight> {
	private static final LinkedHashMap<String, Function<Flight, String>> columns = new LinkedHashMap<>();
	static {
		columns.put("Expected Arrival Date", f -> CsvMapper.toCsvValue(f.getExpectedArrivalDate()));
		columns.put("Effective Arrival Date", f -> CsvMapper.toCsvValue(f.getEffectiveArrivalDate()));
		columns.put("Starting Airport", Flight::getStartingAirport);
		columns.put("Airline", f->f.getAirline().getCompanyName());
		columns.put("Flight number", Flight::getFlightNumber);
		columns.put("Flight status", f->f.getFlightStatus()!=null?f.getFlightStatus().name():"");
		columns.put("ExpectedDelay", f-> CsvMapper.toCsvValue(f.getExpectedDelay()));
		columns.put("Arrival Date Delta [minutes]", f-> 
			f.getEffectiveArrivalDate()!=null?
				Long.toString(ChronoUnit.MINUTES.between(f.getExpectedArrivalDate(), f.getEffectiveArrivalDate())):
				"");
	}
	
	public FlightCsvMapper() {
		super(columns);
	}

}
