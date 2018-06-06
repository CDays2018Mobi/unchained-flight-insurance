import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Contract} from "../model/contract.model";

@Injectable()
export class ContractClient {

  constructor(private http: HttpClient) {
  }

  get url(): string {
    return 'http://localhost:9000/api/v1/contracts';
  }

  create$(contract: Contract): Observable<Contract> {
    return this.http.post<Contract>(`${this.url}`, contract);
  }

}
