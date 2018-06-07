import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {RiskCoverage} from '../model/coverage.model';
import 'rxjs/add/observable/of'

@Injectable()
export class RiskClient {

  constructor(private http: HttpClient) {
  }

  get url(): string {
    return 'http://localhost:9000/api/v1/risk';
  }

  getRiskCoverages$(flightNumber: string, arrivalDate: string): Observable<RiskCoverage[]> {
    /*return Observable.of([
      new RiskCoverage('Bronze', 250, 10, true),
      new RiskCoverage('Silver', 500, 20, true),
      new RiskCoverage('Gold', 10000, 100, true)
    ]);*/
    return this.http.get<RiskCoverage[]>(`${this.url}/coverages?flightNumber=${flightNumber}&arrivalDate=${arrivalDate}`);
  }

}
