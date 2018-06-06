package ch.mobi.ufi.domain.risk.predictor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.mobi.ufi.domain.flight.entity.Flight;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BayesianDelayEstimator implements DelayEstimator {
	@AllArgsConstructor
	class Dimension {
		public String name;
		public Function<Flight, Object> function;
	}
	
	class ProbabilityMap<T> {
		int objectCount=0;
		Map<T, AtomicLong> probabilityMap = new HashMap<>();
		public void add(T key) {
			AtomicLong count = probabilityMap.computeIfAbsent(key, k->new AtomicLong());
			count.incrementAndGet();
			objectCount++;
		}
		
		public Set<T> keySet() {
			return probabilityMap.keySet();
		}
		
		public double getProbability(T key) {
			return objectCount>0?probabilityMap.getOrDefault(key, new AtomicLong()).doubleValue()/objectCount:0;
		}
		
		public int getObjectCount() {
			return objectCount;
		}
	}

	Map<Object, ProbabilityMap<String>> countPerAllDimensionGivenVariable = new HashMap<>();
	ProbabilityMap<String> countPerAllDimension = new ProbabilityMap<String>();
	int validFlightCount=0;

	
	public String mapHour(int hour) {
		if (hour<=11) {
			return "0-11";
		} else if (hour<=15) {
			return "12-15";
		} else if (hour<=19) {
			return "16-19";
		} else {
			return "20-23";
		}
	}
	
	List<Dimension> dimensions = Arrays.asList(
			new Dimension("companyName", f->f.getAirline().getCompanyName()),
			new Dimension("expectedArrivalHour", f->mapHour(f.getExpectedArrivalDate().getHour())),
			//new Dimension("startingAirport", f->f.getStartingAirport()), // on ne peut pas tenir compte de l'aéroport de départ car ce n'est pas une variable indépendante du triplet companyName/expectedArrivalHour/arrivalDayOfWeek
			new Dimension("expectedArrivalDayOfWeek", f->f.getExpectedArrivalDate().getDayOfWeek()));
	Dimension variable;
	List<Integer> flightDelayThresholds;
	
	/**
	 * Détermine le seuil à utiliser,
	 * EXemple:
	 *  flightDelayThresholds = [30, 60]
	 *  effectiveDelay -> threshold
	 *              10 -> 0
	 *              40 -> 30
	 *              70 -> 60
	 *              
	 * @param flightDelayThresholds
	 * @param effectiveDelay
	 * @return
	 */
	private Integer findThreshold(List<Integer> flightDelayThresholds, Long effectiveDelay) {
		if (flightDelayThresholds==null || flightDelayThresholds.isEmpty()) {
			return 0;
		}
		int previousThreshold = 0;
		for (Integer flightDelayThreshold : flightDelayThresholds) {
			if (effectiveDelay<flightDelayThreshold) {
				return previousThreshold;
			}
			previousThreshold = flightDelayThreshold;
		}
		return flightDelayThresholds.get(flightDelayThresholds.size()-1);
	}

	/* (non-Javadoc)
	 * @see ch.mobi.ufi.supplier.DelayEstimator#initialize(java.util.List, java.util.List)
	 */
	@Override
	public void initialize(List<Flight> allFlights, List<Integer> flightDelayThresholds) {
		this.flightDelayThresholds = flightDelayThresholds;
		variable =  new Dimension("effectiveDelay",  f->{
			Long effectiveDelay=f.getEffectiveDelay(); return effectiveDelay!=null?
					//(effectiveDelay>=60?"gte60":"less60"):
					findThreshold(flightDelayThresholds, effectiveDelay):
				null;
		});

		/*
		 * Dimensions: (=les données de base)
		 * - airline company name
		 * - flight arrival hour
		 * - flight arrival day of week
		 * Variable: (=ce que l'on cherche)
		 * - Function
		 */
		
		for (Flight flight : allFlights) {
			if (flight.getEffectiveArrivalDate()==null) {
				continue;
			}

			// count the elements given the variable with all dimensions
			Object variableKey = variable.name+"="+variable.function.apply(flight);
			ProbabilityMap<String> countPerVariable = countPerAllDimensionGivenVariable.computeIfAbsent(variableKey, v->new ProbabilityMap<>());
			String allDimensionsKey = buildAllDimensionKey(dimensions, flight);
			countPerVariable.add(allDimensionsKey);

			countPerAllDimension.add(allDimensionsKey);

			validFlightCount++;
		}

		// show debug info
		for (Entry<Object, ProbabilityMap<String>> entry : countPerAllDimensionGivenVariable.entrySet()) {
			Object given = entry.getKey();
			for (String key : entry.getValue().keySet()) {
				LOG.info("p({}|{})={}", key, given, entry.getValue().getProbability(key));
			}
		}
		for (String key : countPerAllDimension.keySet()) {
			LOG.info("p({})={}", key, countPerAllDimension.getProbability(key));
		}
		
		// validation
		// Note: on calcule la probabilité de retard sur chaque vol de l'ensemble 
		// d'apprentissage. Ce faisant, on tord un peu le système (car le vol de 
		// l'ensemble de test est aussi dans l'ensemble d'apprentissage, mais
		// cela donne au moins une idée).
		int minDelay=60; // 60 minutes
		try (PrintWriter out = new PrintWriter("bayesValidation.csv")) {
			out.println(dimensions.stream()
					.map(d->d.name)
					.collect(Collectors.joining(";"))+
					";probabilityOf"+minDelay+"Delay");
			for (Flight flight : allFlights) {
				out.println(dimensions.stream()
						.map(d->d.function.apply(flight).toString()).collect(Collectors.joining(";"))+
						";"+computeProbabilityOfBeingDelayed(flight, minDelay));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	/* (non-Javadoc)
	 * @see ch.mobi.ufi.supplier.DelayEstimator#computeProbabilityOfBeingDelayed(ch.mobi.ufi.domain.flight.entity.Flight, java.lang.Integer)
	 */
	@Override
	public double computeProbabilityOfBeingDelayed(Flight flight, Integer minDelay) {
		if (minDelay!=0 && !flightDelayThresholds.contains(minDelay)) {
			// incoherence between minDelay and the initial thresholds => raise error
			throw new IllegalArgumentException("expected the minDelay "+minDelay+" to be in "+flightDelayThresholds);
		}
		
		// Bayesian calculation
		String allDimensionsKey = buildAllDimensionKey(dimensions, flight);
		ProbabilityMap<String> map = countPerAllDimensionGivenVariable.get("effectiveDelay="+minDelay);
		double pAllDimensionGivenGtMinDelay = map.getProbability(allDimensionsKey);
		double pGtMinDelay = map.getObjectCount()/(double)validFlightCount;
		double pAllDimension = countPerAllDimension.getProbability(allDimensionsKey);
		double pGtMinDelayGivenAllDimension = pAllDimensionGivenGtMinDelay*pGtMinDelay/pAllDimension;
		LOG.info("pAllDimensionGivenGt"+minDelay+"={}, pGt"+minDelay+"={}, pAllDimension={}, p(gt"+minDelay+"|{})={}", 
				pAllDimensionGivenGtMinDelay, pGtMinDelay, pAllDimension, allDimensionsKey, pGtMinDelayGivenAllDimension);
		return pGtMinDelayGivenAllDimension;
	}
	

	private String buildAllDimensionKey(List<Dimension> dimensions, Flight flight) {
		String allDimensionsKey = dimensions.stream().map(d->d.name+"="+d.function.apply(flight)).collect(Collectors.joining(","));
		return allDimensionsKey;
	}
}
