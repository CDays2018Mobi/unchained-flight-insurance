package ch.mobi.ufi.domain.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DurationFormatUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CsvMapper<T> {
	private LinkedHashMap<String, Function<T, String>> columns;

	public String getCsvHeader() {
		return columns.keySet().stream().collect(Collectors.joining(";"));
	}
	
	public String toCsvRow(T data) {
		return columns.values().stream().map(f->f.apply(data)).collect(Collectors.joining(";"));
	}
	
	public static String toCsvValue(LocalDateTime localDateTime) {
		return localDateTime!=null?localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)):"";
	}
	public static String toCsvValue(Duration duration) {
		return duration!=null?DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm"):"";
	}
}
