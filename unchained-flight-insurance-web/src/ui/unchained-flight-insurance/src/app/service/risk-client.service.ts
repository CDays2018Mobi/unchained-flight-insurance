import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Coverage} from '../model/coverage.model';
import 'rxjs/add/observable/of';

@Injectable()
export class RiskClient {

  constructor(private http: HttpClient) {
  }

  get url(): string {
    return 'http://localhost:9000/api/v1/risk';
  }

  getCoverages$(): Observable<Coverage[]> {
    return Observable.of([
      new Coverage('Basic', 250, 10, true),
      new Coverage('Medium', 500, 20, true),
      new Coverage('Ultimate', 10000, 100, true)
    ]);
    //return this.http.get<Coverage[]>(`${this.url}/coverages`);
  }

}
