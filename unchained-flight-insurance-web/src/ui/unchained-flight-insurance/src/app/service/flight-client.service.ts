import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {RiskCoverage} from '../model/coverage.model';
import 'rxjs/add/observable/of'
import {Flight, InsurableFlight} from '../model/flight.model';

const insurableFlightsMock: InsurableFlight[] = [
  new InsurableFlight(new Flight('GVA123', '2018-06-07', 'On time'), 0.5, [
    new RiskCoverage('Basic', 250, 50, true),
    new RiskCoverage('Medium', 500, 80, true),
    new RiskCoverage('Ultimate', 10000, 200, false)
  ]),
  new InsurableFlight(new Flight('BRA2424', '2018-06-15', 'Delayed'), 0.9, [
    new RiskCoverage('Basic', 250, 700, true),
    new RiskCoverage('Medium', 500, 1200, false),
    new RiskCoverage('Ultimate', 10000, 3000, false)
  ]),
  new InsurableFlight(new Flight('DID69', '2018-07-22', 'On time'), 0.1, [
    new RiskCoverage('Basic', 100, 10, true),
    new RiskCoverage('Medium', 500, 20, true),
    new RiskCoverage('Ultimate', 10000, 30, true)
  ])
];


@Injectable()
export class FlightClient {

  constructor(private http: HttpClient) {
  }

  get url(): string {
    return 'http://localhost:9000/api/v1/flights/insurable';
  }

  getInsurableFlights$(): Observable<InsurableFlight[]> {
    return this.http.get<InsurableFlight[]>(this.url);
    //return Observable.of(insurableFlightsMock);
  }
}
