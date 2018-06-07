import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of'
import {Movement, MovementSummary} from "../model/movement.model";

@Injectable()
export class FinanceClient {

  constructor(private http: HttpClient) {
  }

  get url(): string {
    return 'http://localhost:9000/api/v1/finance/movements';
  }

  getMovements$(): Observable<MovementSummary> {
    return Observable.of(new MovementSummary(-170, -440, [
        new Movement('2018-05-20', 'La Mobilière', 10),
        new Movement('2018-05-21', 'La Mobilière', 20),
        new Movement('2018-06-07', 'doug.doe@gmail.com', -200)
      ],
      [
        new Movement('2018-06-07', 'La Mobilière', 20),
        new Movement('2018-06-07', 'La Mobilière', 40),
        new Movement('2018-06-06', 'john.doe@gmail.com', -500),
      ]));
    /*
        return this.http.get<RiskCoverage[]>(`${this.url}/coverages?flightNumber=${flightNumber}&arrivalDate=${isoDate}`);
      */
  }

}
