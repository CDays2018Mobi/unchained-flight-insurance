import { Component, OnInit } from '@angular/core';
import {Movement, MovementSummary} from "../../model/movement.model";
import {FinanceClient} from "../../service/finance-client.service";
import {Observable} from "rxjs/Observable";

@Component({
  selector: 'app-financial-movement-summary',
  templateUrl: './financial-movement-summary.component.html',
  styleUrls: ['./financial-movement-summary.component.css']
})
export class FinancialMovementSummaryComponent implements OnInit {

  summary$: Observable<MovementSummary>;

  constructor(private financeClient: FinanceClient) { }

  ngOnInit() {
    this.summary$ = this.financeClient.getMovements$();
  }

  get effectiveMovements$(): Observable<Movement[]> {
    return this.summary$.map(summary => summary.effective);
  }

  get predictedMovements$(): Observable<Movement[]> {
    return this.summary$.map(summary => summary.predicted);
  }
}
