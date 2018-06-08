package ch.mobi.ufi.domain.flight.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import ch.mobi.ufi.domain.flight.entity.Airline;
import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.parameters.DefaultFlightParameters;
import ch.mobi.ufi.domain.flight.parameters.FlightParameters;
import ch.mobi.ufi.domain.flight.vo.FlightStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class
GvaFlightsSupplier implements FlightsSupplier {
    private static final File FLIGHTCACHE_DIRECTORY = new File("./src/main/resources/flightcache");

    {
        LOG.info("flightcache directory for storing = {}", FLIGHTCACHE_DIRECTORY.getAbsolutePath());
    }

    private static final String CACHED_FILENAME_PREFIX = "gva_";
    private static final String API_URL = "http://gva.ch/ajax/ArrivalDepartureSearch" +
            ".aspx?type=A&day={day}&lang=2&time=0000&nline=1000&offset=0&_={timestamp}";
    private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Override
    public List<Flight> getFlights(FlightParameters flightParameters) {
        DefaultFlightParameters params = (DefaultFlightParameters) flightParameters;
        long dayParam = ChronoUnit.DAYS.between(LocalDate.now(), params.getDate());
        LOG.info("dayParam=" + Long.toString(dayParam) + ", date=" + params.getDate());
        List<Flight> flights = new ArrayList<>();

        String queryUrl = null;
        File cachedFile = null;
        if (dayParam > 1) {
            // no data after tomorrow => return no data
            return flights;
        } else if (dayParam <= 1) {
            // service has no data before yesterday => use cached data
            Resource[] resources;
            File[] files;
            try {
                resources = resolver.getResources("classpath*:/flightcache/" + CACHED_FILENAME_PREFIX + params.getDate() + "*.html");
                files = Arrays.stream(resources).map(r -> {
                    try {
                        return r.getFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                }).collect(Collectors.toList()).toArray(new File[resources.length]);
                for (Resource resource : resources) {
                    LOG.info(resource.getFilename() + " " + resource.getFile());
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return flights;
            }

            //File[] files = FLIGHTCACHE_DIRECTORY.listFiles(file -> file.getName().startsWith(CACHED_FILENAME_PREFIX + params
            // .getDate()));
            if (files.length == 0) {
                // no cached data => no flights
                return flights;
            } else {
                Arrays.sort(files, Comparator.comparing(f -> -f.length()));
                cachedFile = files[0];
            }
        } else {
            // standard
            queryUrl = API_URL
                    .replaceFirst("\\{day\\}", Long.toString(dayParam))
                    .replaceFirst("\\{timestamp\\}", Long.toString(System.currentTimeMillis()));
        }
        LOG.info("queryUrl=" + (cachedFile != null ? cachedFile.getAbsolutePath() : queryUrl));

        try {

            System.setProperty("java.net.useSystemProxies", "true");

            Document doc;
            if (cachedFile != null) {
                doc = Jsoup.parse(cachedFile, "UTF-8");
            } else {
                doc = Jsoup
                        .connect(queryUrl)
                        .timeout(10000 * 1000) // some requests takes 22 seconds
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0")
                        //.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0")
                        //.header("Accept-Encoding", "gzip, deflate")
                        //.header("Connection", "keep-alive")
                        //.header("Upgrade-Insecure-Requests", "1")
                        .header("Cache-Control", "max-age=0")
                        //.header("Host", "gva.ch")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .header("Accept-Language", "fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
                        //.header("Cookie", "ASP.NET_SessionId=tetp5w0odszc1mkelfw05ovw; BT_ctst=;
                        // BT_sdc=eyJldF9jb2lkIjoiNTU1ZWMxMzcwYmQyMGI5YzdiODJiZjUyNmFmYWU2MjciLCJyZnIiOiIiLCJ0aW1lIjoxNTI2ODI3NTA2MzkwLCJwaSI6NCwicmV0dXJuaW5nIjowLCJldGNjX2NtcCI6Ik5BIn0%3D; BT_pdc=eyJldGNjX2N1c3QiOjAsImVjX29yZGVyIjowLCJldGNjX25ld3NsZXR0ZXIiOjB9; nmstat=1526827553010; _ga=GA1.2.1106959508.1526827508; _gid=GA1.2.1224295352.1526827508; noWS_nQx2oE=true; _et_coid=555ec1370bd20b9c7b82bf526afae627")
                        .get();
                // content not yet cached => cache it
                try (PrintWriter out = new PrintWriter(new File(FLIGHTCACHE_DIRECTORY, CACHED_FILENAME_PREFIX + params.getDate()
                        .toString() + "_" + System.currentTimeMillis() + ".html"))) {
                    out.println(doc.html());
                    out.close();
                } catch (IOException e) {
                    // TODO gérer l'exception proprement
                    e.printStackTrace();
                }
            }

            String dateString = doc.select("h2").text(); // div[class=titlein]
            //LOG.info("dateString="+dateString);
            Matcher m = Pattern.compile("([\\d\\.]+)").matcher(dateString);
            LocalDate day;
            LocalDate nextDay;
            if (m.find()) {
                day = LocalDate.parse(m.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } else {
                LOG.warn("could not parse date from ", dateString);
                day = LocalDate.now();
            }
            nextDay = day.plusDays(1);

            Elements newsHeadlines = doc.select("tbody > tr");
            for (Element element : newsHeadlines) {
                try {
                    Flight.FlightBuilder fb = Flight.builder();

                    String expectedTimeStr = element.select("div[class=col1]").text();
                    LocalTime expectedTime = LocalTime.parse(expectedTimeStr); // expected
                    fb.expectedArrivalDate(day.atTime(expectedTime));

                    String actualTimeStr = element.select("div[class=col2]").text();
                    if (!actualTimeStr.isEmpty()) {
                        LocalTime actualTime = LocalTime.parse(actualTimeStr); // actual
                        long deltaHours = ChronoUnit.HOURS.between(expectedTime, actualTime);
                        fb.effectiveArrivalDate((Math.abs(deltaHours) < 12 ? day : nextDay).atTime(actualTime));
                        // Notes:
                        // - effective arrival date is not provided when the flight is canceled
                        // - when the absolute time difference is too high (e.g. 21:00 vs 00:10),
                        //   this means that the flight arrived the next day
                    }

                    fb.startingAirport(element.select("div[class=col3]").text()); // starting airport

                    fb.airline(Airline.builder().companyName(element.select("div[class=col4]").text()).build()); // company

                    fb.flightNumber(element.select("div[class=col5]").text()); // flight number

                    String statusString = element.select("div[class=col6]").text(); // status
                    if (!statusString.isEmpty()) {
                        fb.flightStatus(FlightStatus.valueOf(statusString));
                        // Note: status is blank when the flight is not arrived yet
                    }

                    String expectedDelayStr = element.select("div[class=col7]").text();
                    if (!expectedDelayStr.isEmpty()) {
                        fb.expectedDelay(Duration.between(LocalTime.MIN,
                                LocalTime.parse(expectedDelayStr))); // delay
                    }
                    flights.add(fb.build());
                } catch (Exception e) {
                    LOG.error("could not process html row {}", element.html(), e);
                }
            }

        } catch (IOException e) {
            LOG.info("endDate=" + new Date());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return flights;
    }


}
